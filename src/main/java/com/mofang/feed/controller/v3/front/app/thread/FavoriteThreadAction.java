package com.mofang.feed.controller.v3.front.app.thread;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedUserFavoriteLogic;
import com.mofang.feed.logic.impl.FeedUserFavoriteLogicImpl;
import com.mofang.feed.model.FeedUserFavorite;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/favthread")
public class FavoriteThreadAction extends AbstractActionExecutor
{
	private FeedUserFavoriteLogic logic = FeedUserFavoriteLogicImpl.getInstance();

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
		
		long userId = Long.parseLong(strUserId);
		JSONObject json = new JSONObject(postData);
		long threadId = json.optLong("id", 0L);
		
		///参数检查
		if(threadId <= 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造UserFavorite实体对象
		FeedUserFavorite favoriteInfo = new FeedUserFavorite();
		favoriteInfo.setUserId(userId);
		favoriteInfo.setThreadId(threadId);
		return logic.add(favoriteInfo);
	}
}