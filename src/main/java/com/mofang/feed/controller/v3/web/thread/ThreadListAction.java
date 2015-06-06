package com.mofang.feed.controller.v3.web.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.QueryTimeType;
import com.mofang.feed.logic.web.FeedThreadLogic;
import com.mofang.feed.logic.web.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url="feed/v2/web/thread/list")
public class ThreadListAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		String strForumId = context.getParamMap().get("fid");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		String strType = context.getParameters("type");
		String strTagId = context.getParameters("tagid");
		String strTimeType = context.getParameters("timetype");
		
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
		int tagId = 0;
		int timeType = 0;
		
		if(StringUtil.isLong(strUserId))
			userId = Long.parseLong(strUserId);
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		if(StringUtil.isInteger(strType))
			type = Integer.parseInt(strType);
		if(StringUtil.isInteger(strTagId))
			tagId = Integer.parseInt(strTagId);
		if(StringUtil.isInteger(strTimeType))
			timeType = Integer.parseInt(strTimeType);
		
		boolean filterTag = tagId != 0;
		boolean filterElite = type == 1;
		boolean filterLastPostTime = timeType == QueryTimeType.LAST_POST_TIME;
		boolean useRedis = (!filterTag && !filterElite && filterLastPostTime);
		if(useRedis)
			return logic.getForumThreadList(forumId, pageNum, pageSize, userId);
		else
			return logic.getForumThreadListByCondition(forumId, tagId, filterElite, timeType, pageNum, pageSize, userId);
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}