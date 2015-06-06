package com.mofang.feed.controller.v3.web.comment;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedCommentLogic;
import com.mofang.feed.logic.web.impl.FeedCommentLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/web/comment/list")
public class CommentListAction extends AbstractActionExecutor
{
	private FeedCommentLogic logic = FeedCommentLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strPostId = context.getParamMap().get("pid");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		///参数检查
		if(!StringUtil.isLong(strPostId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long postId = Long.parseLong(strPostId);
		int pageNum = 1;
		int pageSize = 50;
		
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getPostCommentList(postId, pageNum, pageSize);
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}