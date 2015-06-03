package com.mofang.feed.controller.v3.web.user;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedUserLogic;
import com.mofang.feed.logic.web.impl.FeedUserLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/web/user/info")
public class UserInfoAction extends AbstractActionExecutor
{
	private FeedUserLogic logic = FeedUserLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strUserId = context.getParameters("uid");
		long userId = 0L;
		
		if(StringUtil.isLong(strUserId))
			userId = Long.parseLong(strUserId);
		
		if(userId <= 0){
			ResultValue result = new ResultValue();
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		return logic.getInfo(userId);
	}
}