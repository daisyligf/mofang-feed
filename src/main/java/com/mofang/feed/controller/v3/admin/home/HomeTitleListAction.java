package com.mofang.feed.controller.v3.admin.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.web.FeedHomeTitleLogic;
import com.mofang.feed.logic.web.impl.FeedHomeTitleLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/backend/home/subject/get")
public class HomeTitleListAction extends AbstractActionExecutor {
	
	private FeedHomeTitleLogic logic = FeedHomeTitleLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
