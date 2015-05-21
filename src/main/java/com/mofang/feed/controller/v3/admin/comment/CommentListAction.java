package com.mofang.feed.controller.v3.admin.comment;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.admin.FeedCommentLogic;
import com.mofang.feed.logic.admin.impl.FeedCommentLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/backend/comment/list")
public class CommentListAction extends AbstractActionExecutor
{
	private FeedCommentLogic logic = FeedCommentLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strPostId = context.getParameters("pid");
		String strStatus = context.getParameters("status");
		String strPageNum = context.getParameters("startPage");
		String strPageSize = context.getParameters("pageSize");
		
		long postId = 0L;
		if(StringUtil.isLong(strPostId))
			postId = Long.parseLong(strPostId);
		
		int status = 1;
		if(StringUtil.isInteger(strStatus))
			status = Integer.parseInt(strStatus);
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getCommentList(postId, status, pageNum, pageSize);
	}
}