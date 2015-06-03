package com.mofang.feed.controller.v3.web.moderator;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedModeratorApplyLogic;
import com.mofang.feed.logic.web.impl.FeedModeratorApplyLogicImpl;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/web/moderator/apply")
public class ModeratorApplyAction extends AbstractActionExecutor
{
	private FeedModeratorApplyLogic logic = FeedModeratorApplyLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("uid");
		if(!StringUtil.isLong(strUserId)) 
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
		JSONObject json = new JSONObject(postData);
		long forumId = json.optLong("fid", 0L);
		String qq = json.optString("qq", "");
		String mobile = json.optString("mobile", "");
		long userId = Long.parseLong(strUserId);
		
		FeedModeratorApply model = new FeedModeratorApply();
		model.setUserId(userId);
		model.setForumId(forumId);
		model.setCreateTime(System.currentTimeMillis());
		model.setContactQQ(qq);
		model.setContactMobile(mobile);
		
		return logic.apply(model);
	}
}