package com.mofang.feed.controller.v3.front.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.QueryTimeType;
import com.mofang.feed.logic.FeedThreadLogic;
import com.mofang.feed.logic.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/forumdisplay")
public class ForumThreadListAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		String strForumId = context.getParamMap().get("fid");
		String strPageNum = context.getParameters("p");
		String strPageSize = context.getParameters("pagesize");
		String strType = context.getParameters("type");
		
		///参数检查
		if(!StringUtil.isLong(strForumId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long forumId = Long.parseLong(strForumId);		
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
		
		if(1 == type)
			return logic.getForumEliteThreadList(forumId, pageNum, pageSize, userId, QueryTimeType.LAST_POST_TIME);
		else if(2 == type)
			return logic.getForumQuestionThreadList(forumId, pageNum, pageSize, userId);
		else if(3 == type)
			return logic.getForumVideoThreadList(forumId, pageNum, pageSize, userId);
		else if(4 == type)
			return logic.getForumMarkThreadList(forumId, pageNum, pageSize, userId);
		else
			return logic.getForumThreadList(forumId, pageNum, pageSize, userId);
	}
}