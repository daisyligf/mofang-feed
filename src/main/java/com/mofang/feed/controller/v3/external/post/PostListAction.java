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
		String strUserId = context.getParamMap().get("uid");
		String strThreadId = context.getParamMap().get("tid");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		String strType = context.getParameters("type");
		
		///参数检查
		if(!StringUtil.isLong(strThreadId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long threadId = Long.parseLong(strThreadId);		
		long userId = 0L;
		int pageNum = 1;
		int pageSize = 50;
		int type = 0;
		
		if(StringUtil.isLong(strUserId))
			userId = Long.parseLong(strUserId);
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		if(StringUtil.isInteger(strType))
			type = Integer.parseInt(strType);
		
		if(1 == type)    ///只看楼主
			return logic.getHostPostList(threadId, pageNum, pageSize, userId);
		else
			return logic.getThreadPostList(threadId, pageNum, pageSize, userId);
	}
}