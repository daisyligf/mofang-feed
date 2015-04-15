package com.mofang.feed.controller.admin.role;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedSysRoleLogic;
import com.mofang.feed.logic.impl.FeedSysRoleLogicImpl;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
public class RoleAddAction extends AbstractActionExecutor
{
	private FeedSysRoleLogic logic = FeedSysRoleLogicImpl.getInstance();

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
		String roleName = json.optString("name", "");
		String icon = json.optString("icon", "");
		String color = json.optString("color", "");
		String privileges = json.optString("privilege_list", "");
		
		///参数检查
		if(StringUtil.isNullOrEmpty(roleName) || StringUtil.isNullOrEmpty(privileges))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造Role实体对象
		FeedSysRole roleInfo = new FeedSysRole();
		roleInfo.setRoleName(roleName);
		roleInfo.setIcon(icon);
		roleInfo.setColor(color);
		roleInfo.setPrivileges(privileges);
		
		return logic.add(roleInfo, operatorId);
	}
}