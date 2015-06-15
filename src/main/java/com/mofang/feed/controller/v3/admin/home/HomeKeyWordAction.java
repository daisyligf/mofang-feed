package com.mofang.feed.controller.v3.admin.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.web.FeedHomeKeyWordLogic;
import com.mofang.feed.logic.web.impl.FeedHomeKeyWordLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/backend/home/search/keyworkd/get")
public class HomeKeyWordAction extends AbstractActionExecutor {

	private FeedHomeKeyWordLogic logic = FeedHomeKeyWordLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getKeyWord();
	}

}
