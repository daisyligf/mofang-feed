package com.mofang.feed.controller.v3.web.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedHomeKeyWordLogic;
import com.mofang.feed.logic.impl.FeedHomeKeyWordLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/home/keyworkd")
public class HomeKeyWordAction extends AbstractActionExecutor {

	private FeedHomeKeyWordLogic logic = FeedHomeKeyWordLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getKeyWord();
	}

}
