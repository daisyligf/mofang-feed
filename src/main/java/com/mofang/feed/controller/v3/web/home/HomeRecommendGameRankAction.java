package com.mofang.feed.controller.v3.web.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.web.FeedHomeRecommendGameRankLogic;
import com.mofang.feed.logic.web.impl.FeedHomeRecommendGameRankLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/web/home/recommendGameRank")
public class HomeRecommendGameRankAction extends AbstractActionExecutor
{
	private FeedHomeRecommendGameRankLogic logic = FeedHomeRecommendGameRankLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		return logic.getList();
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}