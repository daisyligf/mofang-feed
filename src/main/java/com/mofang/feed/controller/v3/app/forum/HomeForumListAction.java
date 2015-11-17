package com.mofang.feed.controller.v3.app.forum;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.app.HomeForumListLogic;
import com.mofang.feed.logic.app.impl.HomeForumListLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/app/homeForumList")
public class HomeForumListAction extends AbstractActionExecutor
{

	private HomeForumListLogic logic = HomeForumListLogicImpl.getInstatnce();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.getHomeFourmList();
	}

	protected boolean needCheckAtom()
	{
		return false;
	}
}
