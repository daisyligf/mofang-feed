package com.mofang.feed.controller.v3.app.forum;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.app.FeedForumLogic;
import com.mofang.feed.logic.app.impl.FeedForumLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url="feed/v3/app/forumRecommend/list")
public class ForumRecommendAction extends AbstractActionExecutor {

	private FeedForumLogic logic = FeedForumLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		if(!StringUtil.isLong(strUserId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		String strGameIds = context.getParamMap().get("gameIds");
		if(StringUtil.isNullOrEmpty(strGameIds)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		JSONArray jsonArray = new JSONArray(strGameIds);
		int length = jsonArray.length();
		Set<Long> gameIds = new HashSet<Long>(length);
		for(int idx = 0; idx < length; idx ++) {
			long gameId = jsonArray.getLong(idx);
			gameIds.add(gameId);
		}
		
		return logic.getForumRecomendList(gameIds);
	}

}
