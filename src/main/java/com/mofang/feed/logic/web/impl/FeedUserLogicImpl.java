package com.mofang.feed.logic.web.impl;

import org.json.JSONObject;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.logic.web.FeedUserLogic;
import com.mofang.feed.model.FeedBlackList;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedBlackListService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedUserLogicImpl implements FeedUserLogic
{
	private final static FeedUserLogicImpl LOGIC = new FeedUserLogicImpl();
	private FeedBlackListService blackListService = FeedBlackListServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	
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
			if(isExists) {
				result.setCode(ReturnCode.USER_HAS_PROHIBITED);
				result.setMessage(ReturnMessage.USER_HAS_PROHIBITED);
				return result;
			}
			///权限检查
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.PROHIBIT_USER);
			if(!hasPrivilege) {
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
			if(!isExists) {
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			///权限检查
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.PROHIBIT_USER);
			if(!hasPrivilege) {
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
			data.put("avatar", userInfo.getAvatar());
			data.put("coin", userInfo.getCoin());
			data.put("register_time", userInfo.getRegisterTime());
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
}