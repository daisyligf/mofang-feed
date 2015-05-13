package com.mofang.feed.controller.v3.admin.tag;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedTagLogic;
import com.mofang.feed.logic.impl.FeedTagLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "backend/tag/get")
public class TagListAction extends AbstractActionExecutor {

	private FeedTagLogic logic = FeedTagLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getList();
	}

}
