package com.mofang.feed.controller.v3.admin.post;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedPostLogic;
import com.mofang.feed.logic.admin.impl.FeedPostLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/backend/post/remove")
public class PostRemoveAction extends AbstractActionExecutor
{
	private FeedPostLogic logic = FeedPostLogicImpl.getInstance();

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
		long postId = json.optLong("pid", 0L);
		if(postId <= 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		return logic.remove(postId, operatorId);
	}
}