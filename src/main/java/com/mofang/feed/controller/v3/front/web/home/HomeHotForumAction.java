package com.mofang.feed.controller.v3.front.web.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedHomeHotForumLogic;
import com.mofang.feed.logic.impl.FeedHomeHotForumLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/home/hotForumList")
public class HomeHotForumAction extends AbstractActionExecutor {

	private FeedHomeHotForumLogic logic = FeedHomeHotForumLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
