package com.mofang.feed.controller.v3.web.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.web.FeedHomeHotForumRankLogic;
import com.mofang.feed.logic.web.impl.FeedHomeHotForumRankLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/web/home/hotForumRank")
public class HomeHotForumRankAction extends AbstractActionExecutor
{
	private FeedHomeHotForumRankLogic logic = FeedHomeHotForumRankLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		return logic.getList();
	}
}