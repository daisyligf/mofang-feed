package com.mofang.feed.controller.v3.app.forum;

import java.util.HashSet;
import java.util.Set;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.app.FeedForumLogic;
import com.mofang.feed.logic.app.impl.FeedForumLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author milo
 *
 */
@Action(url="feed/v3/app/forum/appstore/list")
public class ForumAppstoreAction extends AbstractActionExecutor
{
	private FeedForumLogic logic = FeedForumLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		if(!StringUtil.isLong(strUserId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		Set<Long> forumIds = new HashSet<Long>();
		forumIds.add(360L);
		forumIds.add(44712L);
		forumIds.add(44717L);
		
		return logic.getForumListByAppstore(forumIds);
	}
}