package com.mofang.feed.controller.admin.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.logic.FeedThreadLogic;
import com.mofang.feed.logic.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/thread/search")
public class ThreadSearchAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strForumId = context.getParameters("fid");
		String author = context.getParameters("author");
		String keyword = context.getParameters("keyword");
		String strStatus = context.getParameters("status");
		String strPageNum = context.getParameters("startPage");
		String strPageSize = context.getParameters("pageSize");
		
		long forumId = 0L;
		String forumName = "";
		if(StringUtil.isLong(strForumId))
			forumId = Long.parseLong(strForumId);
		else if(!StringUtil.isNullOrEmpty(strForumId))
			forumName = strForumId;
		
		int status = ThreadStatus.NORMAL;
		if(StringUtil.isInteger(strStatus))
			status = Integer.parseInt(strStatus);
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.search(forumId, forumName, author, keyword, status, pageNum, pageSize);
	}
}