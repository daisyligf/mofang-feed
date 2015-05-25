package com.mofang.feed.controller.app.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedUserFavoriteLogic;
import com.mofang.feed.logic.impl.FeedUserFavoriteLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/removefavthread")
public class FavoriteDeleteAction extends AbstractActionExecutor
{
	private FeedUserFavoriteLogic logic = FeedUserFavoriteLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		String strThreadId = context.getParamMap().get("tid");
		
		///参数检查
		if(!StringUtil.isLong(strUserId) || !StringUtil.isLong(strThreadId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long userId = Long.parseLong(strUserId);
		long threadId = Long.parseLong(strThreadId);	
		return logic.delete(userId, threadId);
	}
}