package com.mofang.feed.controller.v3.admin.admin;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedAdminUserLogic;
import com.mofang.feed.logic.admin.impl.FeedAdminUserLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v3/backend/admin/batchdelete")
public class AdminBatchDeleteAction extends AbstractActionExecutor 
{
	private FeedAdminUserLogic logic = FeedAdminUserLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strOperatorId = context.getParameters("uid");
		if(!StringUtil.isLong(strOperatorId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		
		long operatorId = Long.parseLong(strOperatorId);
		JSONObject json = new JSONObject(postData);
		JSONArray arrayUserIds = json.optJSONArray("user_ids");
		if(null == arrayUserIds || arrayUserIds.length() == 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		
		for(int i=0; i<arrayUserIds.length(); i++)
		{
			result = logic.delete(arrayUserIds.getLong(i), operatorId);
			if(result.getCode() != ReturnCode.SUCCESS)
				return result;
		}
		
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		return result;
	}
}