package com.mofang.feed.controller.admin.module;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedModuleLogic;
import com.mofang.feed.logic.impl.FeedModuleLogicImpl;
import com.mofang.feed.model.FeedModule;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/vitforum/modifyVitForum")
public class ModuleEditAction extends AbstractActionExecutor
{
	private FeedModuleLogic logic = FeedModuleLogicImpl.getInstance();

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
		long moduleId = json.optLong("vid", 0L);
		String name = json.optString("name", "");
		String icon = json.optString("icon", "");
		
		///参数检查
		if(moduleId < 0 || StringUtil.isNullOrEmpty(name))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造Module实体对象
		FeedModule moduleInfo = new FeedModule();
		moduleInfo.setModuleId(moduleId);
		moduleInfo.setName(name);
		moduleInfo.setIcon(icon);
		
		return logic.edit(moduleInfo, operatorId);
	}
}