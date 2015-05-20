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
@Action(url = "feed/v2/backend/thread/list")
public class ThreadListAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strForumId = context.getParameters("fid");
		String strStatus = context.getParameters("status");
		String strPageNum = context.getParameters("startPage");
		String strPageSize = context.getParameters("pageSize");
		
		long forumId = 0L;
		if(StringUtil.isLong(strForumId))
			forumId = Long.parseLong(strForumId);
		
		int status = 1;
		if(StringUtil.isInteger(strStatus))
			status = Integer.parseInt(strStatus);
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getThreadList(forumId, status, pageNum, pageSize);
	}
}