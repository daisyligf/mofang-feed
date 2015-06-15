package com.mofang.feed.controller.v3.admin.comment;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.ThreadStatus;
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
@Action(url = "feed/v3/backend/comment/search")
public class CommentSearchAction extends AbstractActionExecutor
{
	private FeedCommentLogic logic = FeedCommentLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String keyword = context.getParameters("keyword");
		String strForumId = context.getParameters("fid");
		String strStatus = context.getParameters("status");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		if(StringUtil.isNullOrEmpty(keyword))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		long forumId = 0;
		if(StringUtil.isLong(strForumId))
			forumId = Long.parseLong(strForumId);
		
		int status = ThreadStatus.NORMAL;
		if(StringUtil.isInteger(strStatus))
			status = Integer.parseInt(strStatus);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.search(forumId, null, null, keyword, status, pageNum, pageSize);
	}
}