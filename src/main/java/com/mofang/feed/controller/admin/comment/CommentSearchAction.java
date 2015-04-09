package com.mofang.feed.controller.admin.comment;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.logic.FeedCommentLogic;
import com.mofang.feed.logic.impl.FeedCommentLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/comment/search")
public class CommentSearchAction extends AbstractActionExecutor
{
	private FeedCommentLogic logic = FeedCommentLogicImpl.getInstance();

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
		
		int status = CommentStatus.NORMAL;
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