package com.mofang.feed.controller.v3.app.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.logic.app.FeedThreadLogic;
import com.mofang.feed.logic.app.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/app/thread/search")
public class ThreadSearchAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strForumId = context.getParameters("fid");
		String keyword = context.getParameters("keyword");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		long forumId = 0L;
		String forumName = "";
		if(StringUtil.isLong(strForumId))
			forumId = Long.parseLong(strForumId);
		else if(!StringUtil.isNullOrEmpty(strForumId))
			forumName = strForumId;
		
		int status = ThreadStatus.NORMAL;
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.search(forumId, forumName, null, keyword, status, pageNum, pageSize);
	}
}