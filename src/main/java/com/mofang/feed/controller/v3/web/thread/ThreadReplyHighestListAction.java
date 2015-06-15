package com.mofang.feed.controller.v3.web.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedThreadLogic;
import com.mofang.feed.logic.web.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/web/threads/replyHighest")
public class ThreadReplyHighestListAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strForumId = context.getParameters("fid");
		long forumId  = 0l;
		if(StringUtil.isLong(strForumId))
			forumId = Long.parseLong(strForumId);
		
		if(forumId <= 0)
		{
			ResultValue result = new ResultValue();
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		return logic.getReplyHighestThreadList(forumId);
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}