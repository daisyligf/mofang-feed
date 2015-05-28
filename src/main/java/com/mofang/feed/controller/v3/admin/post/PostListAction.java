package com.mofang.feed.controller.v3.admin.post;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.admin.FeedPostLogic;
import com.mofang.feed.logic.admin.impl.FeedPostLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/backend/post/list")
public class PostListAction extends AbstractActionExecutor
{
	private FeedPostLogic logic = FeedPostLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strThreadId = context.getParameters("tid");
		String strStatus = context.getParameters("status");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		long threadId = 0L;
		if(StringUtil.isLong(strThreadId))
			threadId = Long.parseLong(strThreadId);
		
		int status = 1;
		if(StringUtil.isInteger(strStatus))
			status = Integer.parseInt(strStatus);
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getPostList(threadId, status, pageNum, pageSize);
	}
}