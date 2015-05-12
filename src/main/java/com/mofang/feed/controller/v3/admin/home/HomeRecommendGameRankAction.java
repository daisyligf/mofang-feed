package com.mofang.feed.controller.v3.admin.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedHomeRecommendGameRankLogic;
import com.mofang.feed.logic.impl.FeedHomeRecommendGameRankLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "backend/home/rank/recommendgame/get")
public class HomeRecommendGameRankAction extends AbstractActionExecutor {
	
	private FeedHomeRecommendGameRankLogic logic = FeedHomeRecommendGameRankLogicImpl
			.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
