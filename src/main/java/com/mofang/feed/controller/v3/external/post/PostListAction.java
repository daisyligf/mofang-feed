package com.mofang.feed.controller.v3.external.post;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.app.FeedPostLogic;
import com.mofang.feed.logic.app.impl.FeedPostLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v3/external/post/list")
public class PostListAction extends AbstractActionExecutor
{
	private FeedPostLogic logic = FeedPostLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strThreadId = context.getParamMap().get("tid");
		String strPostId = context.getParameters("pid");
		String strPageSize = context.getParameters("size");
		String strSort = context.getParameters("sort");
		
		///参数检查
		if(!StringUtil.isLong(strThreadId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		long postId = 0L;
		if(StringUtil.isLong(strPostId))
			postId = Long.parseLong(strPostId);
		
		if(postId < 0L)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}	

		long threadId = Long.parseLong(strThreadId);	
		int pageSize = 50;
		int sort = 0;   ///默认正序
		
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		if(StringUtil.isInteger(strSort))
			sort = Integer.parseInt(strSort);
		
		if(sort == 0)
			return logic.getThreadPostList(threadId, 1, pageSize, 0);
		else
			return logic.getThreadPostList(threadId, postId, pageSize);
	}
}