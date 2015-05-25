package com.mofang.feed.controller.app.forum;

import java.util.HashSet;
import java.util.Set;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedForumLogic;
import com.mofang.feed.logic.impl.FeedForumLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/forumdetails")
public class ForumListAction extends AbstractActionExecutor
{
	private FeedForumLogic logic = FeedForumLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strForumIds = context.getParamMap().get("fids");
		
		///参数检查
		if(StringUtil.isNullOrEmpty(strForumIds))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		String[] arrForumIds = strForumIds.split(",");
		Set<Long> forumIds = new HashSet<Long>();
		if(arrForumIds.length > 0)
		{
			for(String strForumId : arrForumIds)
			{
				if(StringUtil.isLong(strForumId))
					forumIds.add(Long.parseLong(strForumId));
			}
		}
		
		return logic.getForumList(forumIds);
	}
}