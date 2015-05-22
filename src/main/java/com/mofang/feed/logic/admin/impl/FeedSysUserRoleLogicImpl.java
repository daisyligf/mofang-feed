package com.mofang.feed.logic.admin.impl;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.global.common.ModeratorApplyStatus;
import com.mofang.feed.logic.admin.FeedSysUserRoleLogic;
import com.mofang.feed.model.FeedAdminUser;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.model.ModeratorApplyCondition;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;

/**
 * 
 * @author milo
 *
 */
public class FeedSysUserRoleLogicImpl implements FeedSysUserRoleLogic
{
	private final static FeedSysUserRoleLogicImpl LOGIC = new FeedSysUserRoleLogicImpl();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	
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
			boolean hasPrivilege = userRoleService.hasPrivilege(0, operatorId, FeedPrivilege.ADD_SYS_USER_ROLE);
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
			boolean hasPrivilege = userRoleService.hasPrivilege(0, operatorId, FeedPrivilege.DELETE_SYS_USER_ROLE);
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
	public ResultValue addToAdmin(long forumId, long userId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///权限检查
			boolean hasPrivilege = userRoleService.hasPrivilege(0, operatorId, FeedPrivilege.ADD_ADMIN_USER);
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
	public ResultValue getModeratorList() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
}