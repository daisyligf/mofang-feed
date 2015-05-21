package com.mofang.feed.controller.v3.front.web.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedHomeRecommendGameRankLogic;
import com.mofang.feed.logic.impl.FeedHomeRecommendGameRankLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/home/recommendGameRank")
public class HomeRecommendGameRankAction extends AbstractActionExecutor{

	private FeedHomeRecommendGameRankLogic logic = FeedHomeRecommendGameRankLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
