package com.mofang.feed.controller.v3.admin.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedHomeTickerLogic;
import com.mofang.feed.logic.impl.FeedHomeTickerLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/backend/home/ticker/get")
public class HomeTickerListAction extends AbstractActionExecutor{

	private FeedHomeTickerLogic logic = FeedHomeTickerLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
