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

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/web/thread/toplist")
public class ThreadTopListAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		String strForumId = context.getParamMap().get("fid");
		
		///参数检查
		if(!StringUtil.isLong(strForumId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		long userId = 0L;
		if(StringUtil.isLong(strUserId))
			userId = Long.parseLong(strUserId);
		
		long forumId = Long.parseLong(strForumId);
		return logic.getForumTopThreadList(forumId, userId);
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}