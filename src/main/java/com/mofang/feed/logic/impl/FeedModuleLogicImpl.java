package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.logic.FeedModuleLogic;
import com.mofang.feed.model.FeedModule;
import com.mofang.feed.service.FeedModuleService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.impl.FeedModuleServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModuleLogicImpl implements FeedModuleLogic
{
	private final static FeedModuleLogicImpl LOGIC = new FeedModuleLogicImpl();
	private FeedModuleService moduleService = FeedModuleServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	
	private FeedModuleLogicImpl()
	{}
	
	public static FeedModuleLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedModule model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.ADD_MODULE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存模块信息
			moduleService.add(model);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue edit(FeedModule model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///验证模块有效性
			FeedModule moduleInfo = moduleService.getInfo(model.getModuleId());
			if(null == moduleInfo)
			{
				result.setCode(ReturnCode.MODULE_NOT_EXISTS);
				result.setMessage(ReturnMessage.MODULE_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.EDIT_MODULE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存模块信息
			moduleInfo.setName(model.getName());
			moduleInfo.setIcon(model.getIcon());
			moduleService.edit(moduleInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleLogicImpl.edit throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long moduleId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.DELETE_MODULE);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除版块信息
			moduleService.delete(moduleId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue getInfo(long moduleId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			FeedModule moduleInfo = moduleService.getInfo(moduleId);
			if(null == moduleInfo)
			{
				result.setCode(ReturnCode.MODULE_NOT_EXISTS);
				result.setMessage(ReturnMessage.MODULE_NOT_EXISTS);
				return result;
			}
			
			JSONObject data = new JSONObject();
			data.put("vid", moduleInfo.getModuleId());
			data.put("name", moduleInfo.getName());
			data.put("icon", moduleInfo.getIcon());
			data.put("threads", moduleInfo.getThreads());
			data.put("create_time", moduleInfo.getCreateTime() / 1000);
			
			///老版本字段
			data.put("tags", "");
			data.put("color", "");
			
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
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayModules =new JSONArray();
			List<FeedModule> list = moduleService.getList();
			if(null != list)
			{
				total = list.size();
				JSONObject jsonModule = null;
				for(FeedModule moduleInfo : list)
				{
					jsonModule = new JSONObject();
					jsonModule.put("vid", moduleInfo.getModuleId());
					jsonModule.put("name", moduleInfo.getName());
					jsonModule.put("icon", moduleInfo.getIcon());
					jsonModule.put("threads", moduleInfo.getThreads());
					jsonModule.put("create_time", moduleInfo.getCreateTime() / 1000);
					
					///老版本字段
					jsonModule.put("tags", "");
					jsonModule.put("color", "");
					arrayModules.put(jsonModule);
				}
			}
			data.put("total", total);
			data.put("list", arrayModules);
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