package com.mofang.feed.controller.v3.front.web.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedThreadLogic;
import com.mofang.feed.logic.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/tag/list")
public class ThreadTagListAction extends AbstractActionExecutor {

	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		String strThreadId = context.getParameters("tid");
		
		long threadId = 0;
		if(StringUtil.isLong(strThreadId))
			threadId = Integer.parseInt(strThreadId);
		
		return logic.getThreadTagList(threadId);
	}

}
