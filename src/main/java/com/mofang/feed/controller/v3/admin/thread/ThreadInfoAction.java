package com.mofang.feed.controller.v3.admin.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.admin.FeedThreadLogic;
import com.mofang.feed.logic.admin.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/backend/thread/info")
public class ThreadInfoAction  extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strThreadId = context.getParameters("tid");
		
		long threadId = 0L;
		if(StringUtil.isLong(strThreadId))
			threadId = Long.parseLong(strThreadId);
		
		return logic.getInfo(threadId);
	}
}