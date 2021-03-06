package com.mofang.feed.controller.v3.web.home;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.web.FeedHomeKeyWordLogic;
import com.mofang.feed.logic.web.impl.FeedHomeKeyWordLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/web/home/keyworkd")
public class HomeKeyWordAction extends AbstractActionExecutor
{
	private FeedHomeKeyWordLogic logic = FeedHomeKeyWordLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		return logic.getKeyWord();
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}