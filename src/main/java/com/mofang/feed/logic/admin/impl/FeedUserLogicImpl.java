package com.mofang.feed.logic.admin.impl;

import org.json.JSONObject;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedUserLogic;
import com.mofang.feed.model.FeedBlackList;
import com.mofang.feed.model.external.User;
import com.mofang.feed.redis.UserRedis;
import com.mofang.feed.redis.impl.UserRedisImpl;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedBlackListService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.feed.service.impl.task.UserTPCRemoveServiceImpl;
import com.mofang.feed.service.task.UserTPCRemoveService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedUserLogicImpl implements FeedUserLogic
{
	private final static FeedUserLogicImpl LOGIC = new FeedUserLogicImpl();
	private FeedBlackListService blackListService = FeedBlackListServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	private UserRedis userRedis = UserRedisImpl.getInstance();
	private UserTPCRemoveService userTpcRemoveService = UserTPCRemoveServiceImpl.getInstance();
	
	private FeedUserLogicImpl()
	{}
	
	public static FeedUserLogicImpl getInstance()
	{
		return LOGIC;
	}
	
	@Override
	public ResultValue setProhibit(long forumId, long userId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断该用户是否已经是禁言用户
			boolean isExists = blackListService.exists(forumId, userId);
			if(isExists)
			{
				result.setCode(ReturnCode.USER_HAS_PROHIBITED);
				result.setMessage(ReturnMessage.USER_HAS_PROHIBITED);
				return result;
			}
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///添加禁言用户
			FeedBlackList blackListInfo = new FeedBlackList();
			blackListInfo.setForumId(forumId);
			blackListInfo.setUserId(userId);
			blackListService.add(blackListInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserLogicImpl.setProhibit throw an error.", e);
		}
	}

	@Override
	public ResultValue cancelProhibit(long forumId, long userId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断该用户是否已经是禁言用户
			boolean isExists = blackListService.exists(forumId, userId);
			if(!isExists)
			{
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除禁言用户
			blackListService.delete(forumId, userId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserLogicImpl.cancelProhibit throw an error.", e);
		}
	}

	@Override
	public ResultValue getInfo(long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			User userInfo = UserComponent.getInfo(userId);
			if(null == userInfo)
			{
				result.setCode(ReturnCode.USER_NOT_EXISTS);
				result.setMessage(ReturnMessage.USER_NOT_EXISTS);
				return result;				
			}
			
			data.put("user_id", userId);
			data.put("nickname", userInfo.getNickName());
			data.put("register_time", userInfo.getRegisterTime());
			data.put("status", userInfo.getStatus());  ///0:正常   1:冻结
			
			///获取用户是否为管理员
			boolean isAdmin = adminService.exists(userId);
			data.put("is_admin", isAdmin);
			
			///获取用户发帖总数
			long threads = threadService.getUserThreadCount(userId);
			//精华帖子数
			long eliteThreadCount = threadService.getUserEliteThreadCount(userId);
			///获取用户回帖总数
			long replies = postService.getUserReplyCount(userId);
			data.put("threads", threads);
			data.put("replies", replies);
			data.put("elite_threads", eliteThreadCount);
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue updateStatus(long userId, int status, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///更新用户状态
			boolean isSuccess = UserComponent.updateUserStatus(userId, status);
			if(isSuccess)
			{
				///删除用户缓存
				userRedis.delete(userId);
			}
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserLogicImpl.updateStatus throw an error.", e);
		}
	}

	@Override
	public ResultValue clearUserTPC(long userId, long operatorId)
			throws Exception {
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			userTpcRemoveService.delete(userId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserLogicImpl.clearUserTPC throw an error.", e);
		}
		
	}
	
}