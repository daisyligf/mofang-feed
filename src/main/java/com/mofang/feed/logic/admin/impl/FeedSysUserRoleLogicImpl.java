package com.mofang.feed.logic.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedSysUserRoleLogic;
import com.mofang.feed.model.FeedAdminUser;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysUserRoleLogicImpl implements FeedSysUserRoleLogic
{
	private final static FeedSysUserRoleLogicImpl LOGIC = new FeedSysUserRoleLogicImpl();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	
	private FeedSysUserRoleLogicImpl()
	{}
	
	public static FeedSysUserRoleLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedSysUserRole model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断版主是否满额
			boolean isFull = userRoleService.isFull(model.getForumId());
			if(isFull)
			{
				result.setCode(ReturnCode.FORUM_MODERATOR_IS_FULL);
				result.setMessage(ReturnMessage.FORUM_MODERATOR_IS_FULL);
				return result;
			}
			
			///判断是否已经存在
			boolean isExists = userRoleService.exists(model.getForumId(), model.getUserId());
			if(isExists)
			{
				result.setCode(ReturnCode.USER_ROLE_EXISTS);
				result.setMessage(ReturnMessage.USER_ROLE_EXISTS);
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
			
			userRoleService.save(model);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long forumId, long userId, long operatorId) throws Exception
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
			
			userRoleService.delete(forumId, userId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue addToAdmin(long userId, long operatorId) throws Exception
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
			
			///判断是否已成为管理员
			boolean isExists = adminService.exists(userId);
			if(isExists)
			{
				result.setCode(ReturnCode.ADMIN_USER_EXISTS);
				result.setMessage(ReturnMessage.ADMIN_USER_EXISTS);
				return result;
			}
			
			///添加管理员
			FeedAdminUser adminInfo = new FeedAdminUser();
			adminInfo.setUserId(userId);
			adminService.add(adminInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.addToAdmin throw an error.", e);
		}
	}

	@Override
	public ResultValue getModeratorList(int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			long total = 0;
			JSONArray arrayModerators =new JSONArray();
			Page<FeedSysUserRole> page = userRoleService.getUserList(pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedSysUserRole> moderators = page.getList();
				if(null != moderators)
				{
					JSONObject jsonModerator = null;
					JSONObject jsonForum = null;
					User userInfo = null;
					FeedForum forumInfo = null;
					for(FeedSysUserRole moderatorInfo : moderators)
					{
						jsonModerator = new JSONObject();
						jsonModerator.put("user_id", moderatorInfo.getUserId());          ///用户ID
						///获取用户信息
						userInfo = UserComponent.getInfoFromCache(moderatorInfo.getUserId());
						if(null == userInfo)
							uids.add(moderatorInfo.getUserId());
						else
							jsonModerator.put("nickname", userInfo.getNickName());
						
						///获取用户发帖总数
						long threads = threadService.getUserThreadCount(moderatorInfo.getUserId());
						///获取用户回帖总数
						long replies = postService.getUserReplyCount(moderatorInfo.getUserId());
						jsonModerator.put("threads", threads);
						jsonModerator.put("replies", replies);
						jsonModerator.put("create_time", moderatorInfo.getCreateTime());
						
						///获取版块信息
						jsonForum = new JSONObject();
						jsonForum.put("fid", moderatorInfo.getForumId());
						forumInfo = forumService.getInfo(moderatorInfo.getForumId());
						if(null != forumInfo)
							jsonForum.put("name", forumInfo.getName());
						
						jsonModerator.put("forum", jsonForum);
						arrayModerators.put(jsonModerator);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayModerators.length(); i++)
							{
								jsonModerator = arrayModerators.getJSONObject(i);
								String nickName = jsonModerator.optString("nickname", "");
								long userId = jsonModerator.optLong("user_id", 0L);
								
								///填充用户信息
								if(StringUtil.isNullOrEmpty(nickName))
								{
									if(userMap.containsKey(userId))
									{
										userInfo = userMap.get(userId);
										jsonModerator.put("nickname", userInfo.getNickName());
									}
								}
							}
						}
					}
				}
			}
			data.put("total", total);
			data.put("list", arrayModerators);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.getModeratorList throw an error.", e);
		}
	}
}