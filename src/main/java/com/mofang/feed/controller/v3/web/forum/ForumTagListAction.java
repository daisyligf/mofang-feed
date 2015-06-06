package com.mofang.feed.controller.v3.web.forum;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedForumTagLogic;
import com.mofang.feed.logic.web.impl.FeedForumTagLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/web/forum/taglist")
public class ForumTagListAction extends AbstractActionExecutor
{
	private FeedForumTagLogic logic = FeedForumTagLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strForumId = context.getParameters("fid");
		if(!StringUtil.isLong(strForumId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		long	forumId = Long.parseLong(strForumId);
		if(forumId <= 0)
		{	
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		return logic.getTagList(forumId);
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}