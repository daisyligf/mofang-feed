package com.mofang.feed.controller.v3.web.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedHomeNewspaperLogic;
import com.mofang.feed.logic.impl.FeedHomeNewspaperLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/home/newsPaper")
public class HomeNewspaperAction extends AbstractActionExecutor {

	private FeedHomeNewspaperLogic logic = FeedHomeNewspaperLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
