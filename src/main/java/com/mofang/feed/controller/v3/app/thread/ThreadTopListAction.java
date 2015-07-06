package com.mofang.feed.controller.v3.app.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.app.FeedThreadLogic;
import com.mofang.feed.logic.app.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/app/thread/toplist")
public class ThreadTopListAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strForumId = context.getParameters("fid");
		
		long forumId = 0l;
		if(StringUtil.isLong(strForumId))
			forumId = Long.parseLong(strForumId);
		
		return logic.getForumTopThreadList(forumId);
	}
}
