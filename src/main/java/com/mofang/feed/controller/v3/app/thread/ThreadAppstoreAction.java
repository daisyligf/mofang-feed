package com.mofang.feed.controller.v3.app.thread;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.app.FeedThreadLogic;
import com.mofang.feed.logic.app.impl.FeedThreadLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author milo
 *
 */
@Action(url="feed/v3/app/thread/appstore/list")
public class ThreadAppstoreAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		List<Long> threadIds = new ArrayList<Long>();
		threadIds.add(1780049L);
		threadIds.add(1796435L);
		threadIds.add(1796318L);
		threadIds.add(1791276L);
		threadIds.add(1338966L);
		threadIds.add(1614784L);
		threadIds.add(1790307L);
		threadIds.add(1796362L);
		threadIds.add(1796326L);
		threadIds.add(1793488L);
		
		return logic.getThreadListByAppstore(threadIds);
	}
}