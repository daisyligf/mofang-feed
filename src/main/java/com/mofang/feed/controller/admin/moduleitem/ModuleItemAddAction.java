package com.mofang.feed.controller.admin.moduleitem;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedModuleItemLogic;
import com.mofang.feed.logic.impl.FeedModuleItemLogicImpl;
import com.mofang.feed.model.FeedModuleItem;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/piazza/addPiazza")
public class ModuleItemAddAction extends AbstractActionExecutor
{
	private FeedModuleItemLogic logic = FeedModuleItemLogicImpl.getInstance();

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
		
		JSONObject json = new JSONObject(postData);
		long operatorId = Long.parseLong(strUserId);
		String threadIds = json.optString("tids", "");
		long moduleId = json.optLong("vid", 0L);
		
		///参数检查
		if(StringUtil.isInteger(threadIds) || moduleId <= 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		String[] arrThreadIds = threadIds.split(",");
		FeedModuleItem itemInfo = null;
		for(String strThreadId : arrThreadIds)
		{
			itemInfo = new FeedModuleItem();
			itemInfo.setModuleId(moduleId);
			itemInfo.setThreadId(Long.parseLong(strThreadId));
			logic.add(itemInfo, operatorId);
		}
		
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		return result;
	}
}