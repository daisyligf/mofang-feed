package com.mofang.feed.controller.admin.userrole;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedSysUserRoleLogic;
import com.mofang.feed.logic.impl.FeedSysUserRoleLogicImpl;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
public class UserRoleAddAction extends AbstractActionExecutor
{
	private FeedSysUserRoleLogic logic = FeedSysUserRoleLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("uid");
		if(!StringUtil.isLong(strUserId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		long operatorId = Long.parseLong(strUserId);
		JSONObject json = new JSONObject(postData);
		long forumId = json.optLong("fid", 0L);
		long userId = json.optLong("uid", 0L);
		int roleId = json.optInt("role_id", 0);
		
		///参数检查
		if(forumId <= 0L || userId <= 0L || roleId <= 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造UserRole实体对象
		FeedSysUserRole userRoleInfo = new FeedSysUserRole();
		userRoleInfo.setForumId(forumId);
		userRoleInfo.setUserId(userId);
		userRoleInfo.setRoleId(roleId);
		
		return logic.add(userRoleInfo, operatorId);
	}
}