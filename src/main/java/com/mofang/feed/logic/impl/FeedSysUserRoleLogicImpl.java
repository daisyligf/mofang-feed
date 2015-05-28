package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.logic.FeedSysUserRoleLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedSysRoleService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedSysRoleServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;

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
	public ResultValue add(FeedSysUserRole model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.ADD_SYS_USER_ROLE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存用户角色信息
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
	public ResultValue edit(FeedSysUserRole model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///验证用户角色有效性
			boolean exists = userRoleService.exists(model.getForumId(), model.getUserId());
			if(!exists)
			{
				result.setCode(ReturnCode.USER_ROLE_NOT_EXISTS);
				result.setMessage(ReturnMessage.USER_ROLE_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.EDIT_SYS_USER_ROLE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存用户角色信息
			userRoleService.save(model);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.edit throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long forumId, long userId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///验证用户角色有效性
			boolean exists = userRoleService.exists(forumId, userId);
			if(!exists)
			{
				result.setCode(ReturnCode.USER_ROLE_NOT_EXISTS);
				result.setMessage(ReturnMessage.USER_ROLE_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.DELETE_SYS_USER_ROLE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除用户角色信息
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
	public ResultValue getRoleInfoList(long forumId) throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<Integer> list = userRoleService.getRoleIdList(forumId);
			if(list != null){
				JSONObject objRoleInfo = null;
				for(Integer roleId : list){
					FeedSysRole roleInfo = sysRoleService.getInfo(roleId);
					if(roleInfo == null)
						continue;
					
					objRoleInfo = new JSONObject();
					objRoleInfo.put("role_id", roleInfo.getRoleId());
					objRoleInfo.put("role_name", roleInfo.getRoleName());
					objRoleInfo.put("icon", roleInfo.getIcon());
					data.put(data);
				}
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedSysUserRoleLogicImpl.getRoleInfoList throw an error.", e);
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