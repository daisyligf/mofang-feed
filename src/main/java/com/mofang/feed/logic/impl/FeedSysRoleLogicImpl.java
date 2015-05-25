package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.logic.FeedSysRoleLogic;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.service.FeedSysRoleService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.impl.FeedSysRoleServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysRoleLogicImpl implements FeedSysRoleLogic
{
	private final static FeedSysRoleLogicImpl LOGIC = new FeedSysRoleLogicImpl();
	private FeedSysRoleService sysRoleService = FeedSysRoleServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	
	private FeedSysRoleLogicImpl()
	{}
	
	public static FeedSysRoleLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedSysRole model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.ADD_SYS_ROLE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存系统角色信息
			sysRoleService.add(model);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysRoleLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue edit(FeedSysRole model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///验证系统角色有效性
			FeedSysRole roleInfo = sysRoleService.getInfo(model.getRoleId());
			if(null == roleInfo)
			{
				result.setCode(ReturnCode.SYS_ROLE_NOT_EXISTS);
				result.setMessage(ReturnMessage.SYS_ROLE_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.EDIT_SYS_ROLE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			roleInfo.setRoleName(model.getRoleName());
			roleInfo.setColor(model.getColor());
			roleInfo.setIcon(model.getIcon());
			roleInfo.setPrivileges(model.getPrivileges());
			
			///保存系统角色信息
			sysRoleService.update(roleInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysRoleLogicImpl.edit throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(int roleId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///验证系统角色有效性
			FeedSysRole roleInfo = sysRoleService.getInfo(roleId);
			if(null == roleInfo)
			{
				result.setCode(ReturnCode.SYS_ROLE_NOT_EXISTS);
				result.setMessage(ReturnMessage.SYS_ROLE_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.DELETE_SYS_ROLE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除系统角色信息
			sysRoleService.delete(roleId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysRoleLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue getInfo(int roleId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///验证系统角色有效性
			FeedSysRole roleInfo = sysRoleService.getInfo(roleId);
			if(null == roleInfo)
			{
				result.setCode(ReturnCode.SYS_ROLE_NOT_EXISTS);
				result.setMessage(ReturnMessage.SYS_ROLE_NOT_EXISTS);
				return result;
			}
			
			JSONObject data = new JSONObject();
			data.put("role_id", roleInfo.getRoleId());
			data.put("role_name", roleInfo.getRoleName());
			data.put("color", roleInfo.getColor());
			data.put("icon", roleInfo.getIcon());
			data.put("privilege_list", roleInfo.getPrivileges());
			data.put("create_time", roleInfo.getCreateTime() / 1000);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue getList() throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedSysRole> list = sysRoleService.getList();
			if(null != list)
			{
				JSONObject jsonRole = null;
				for(FeedSysRole roleInfo : list)
				{
					jsonRole = new JSONObject();
					jsonRole.put("role_id", roleInfo.getRoleId());
					jsonRole.put("role_name", roleInfo.getRoleName());
					jsonRole.put("color", roleInfo.getColor());
					jsonRole.put("icon", roleInfo.getIcon());
					jsonRole.put("privilege_list", roleInfo.getPrivileges());
					jsonRole.put("create_time", roleInfo.getCreateTime() / 1000);
					data.put(jsonRole);
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleLogicImpl.getList throw an error.", e);
		}
	}
}