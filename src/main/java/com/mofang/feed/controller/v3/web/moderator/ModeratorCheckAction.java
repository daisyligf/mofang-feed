package com.mofang.feed.controller.v3.web.moderator;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedModeratorApplyLogic;
import com.mofang.feed.logic.web.impl.FeedModeratorApplyLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/web/moderator/check")
public class ModeratorCheckAction extends AbstractActionExecutor
{
	private FeedModeratorApplyLogic logic = FeedModeratorApplyLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("uid");
		String strForumId = context.getParameters("fid");
		if(!StringUtil.isLong(strUserId) || !StringUtil.isLong(strForumId)) 
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);		
			return result;
		}
		
		long userId = Long.parseLong(strUserId);
		long forumId = Long.parseLong(strForumId);
		
		if(userId <= 0L || forumId <= 0L)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);		
			return result;
		}
		
		return logic.check(forumId, userId);
	}
}