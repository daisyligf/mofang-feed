package com.mofang.feed.controller.v3.admin.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.web.FeedHomeHotForumLogic;
import com.mofang.feed.logic.web.impl.FeedHomeHotForumLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/backend/home/list/hotforum/get")
public class HomeHotForumListAction extends AbstractActionExecutor {

	private FeedHomeHotForumLogic logic = FeedHomeHotForumLogicImpl
			.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
