package com.mofang.feed.logic.web.impl;

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
import com.mofang.feed.logic.web.FeedSysUserRoleLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedSysRoleService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedSysRoleServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
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
	private FeedSysRoleService sysRoleService = FeedSysRoleServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedSysUserRoleLogicImpl()
	{}
	
	public static FeedSysUserRoleLogicImpl getInstance()
	{
		return LOGIC;
	}
	
	@Override
	public ResultValue delete(long forumId, long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
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
	public ResultValue getInfo(long forumId, long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///验证用户角色有效性
			int roleId = userRoleService.getRoleId(forumId, userId);
			if(roleId <= 0)
			{
				result.setCode(ReturnCode.USER_ROLE_NOT_EXISTS);
				result.setMessage(ReturnMessage.USER_ROLE_NOT_EXISTS);
				return result;
			}
			
			FeedSysRole roleInfo = sysRoleService.getInfo(roleId);
			if(null == roleInfo)
			{
				result.setCode(ReturnCode.SYS_ROLE_NOT_EXISTS);
				result.setMessage(ReturnMessage.SYS_ROLE_NOT_EXISTS);
				return result;
			}
			
			JSONObject data = new JSONObject();
			data.put("fid", forumId);
			data.put("user_id", userId);
			data.put("role_id", roleId);
			data.put("role_name", roleInfo.getRoleName());
			data.put("color", roleInfo.getColor());
			data.put("icon", roleInfo.getIcon());
			data.put("privilege_list", roleInfo.getPrivileges());
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue getRoleList(long forumId) throws Exception 
	{
		try
		{
			ResultValue result = new ResultValue();
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			JSONObject data = new JSONObject();
			JSONArray arrayModerators = new JSONArray();
			List<FeedSysUserRole> list = userRoleService.getUserListByForumId(forumId);
			if(list != null)
			{
				JSONObject jsonUserRole = null;
				User userInfo = null;
				for(FeedSysUserRole userRoleInfo : list)
				{
					jsonUserRole = new JSONObject();
					jsonUserRole.put("user_id", userRoleInfo.getUserId());
					
					userInfo = UserComponent.getInfoFromCache(userRoleInfo.getUserId());
					if(null == userInfo)
						uids.add(userRoleInfo.getUserId());
					else
					{
						jsonUserRole.put("nickname", userInfo.getNickName());
						jsonUserRole.put("avatar", userInfo.getAvatar());
					}
					arrayModerators.put(jsonUserRole);
				}
				
				///填充用户信息
				if(uids.size() > 0)
				{
					Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
					if(null != userMap)
					{
						for(int i=0; i<arrayModerators.length(); i++)
						{
							jsonUserRole = arrayModerators.getJSONObject(i);
							String nickName = jsonUserRole.optString("nickname", "");
							long userId = jsonUserRole.optLong("user_id", 0L);
							
							///填充用户信息
							if(StringUtil.isNullOrEmpty(nickName))
							{
								if(userMap.containsKey(userId))
								{
									userInfo = userMap.get(userId);
									jsonUserRole.put("nickname", userInfo.getNickName());
									jsonUserRole.put("avatar", userInfo.getAvatar());
								}
							}
						}
					}
				}
			}
			
			///判断版主是否满额
			boolean isFull = userRoleService.isFull(forumId);
			data.put("is_full", isFull);
			data.put("moderators", arrayModerators);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch (Exception e) 
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.getRoleList throw an error.", e);
		}
	}

	@Override
	public ResultValue getUserRoleInfo(long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			boolean isAdmin = adminService.exists(userId);
			boolean isModerator = false;
			JSONArray arrayForums = new JSONArray();
			List<FeedSysUserRole> list = userRoleService.getForumListByUserId(userId);
			if(null != list && list.size() > 0)
			{
				isModerator = true;
				JSONObject jsonForum = null;
				FeedForum forumInfo = null;
				for(FeedSysUserRole userRoleInfo : list)
				{
					forumInfo = forumService.getInfo(userRoleInfo.getForumId());
					if(null != forumInfo)
					{
						jsonForum = new JSONObject();
						jsonForum.put("fid", forumInfo.getForumId());
						jsonForum.put("name", forumInfo.getName());
						arrayForums.put(jsonForum);
					}
				}
			}
			JSONObject data = new JSONObject();
			data.put("user_id", userId);
			data.put("is_admin", isAdmin);
			data.put("is_moderator", isModerator);
			data.put("moderator_forums", arrayForums);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.getUserRoleInfo throw an error.", e);
		}
	}
}