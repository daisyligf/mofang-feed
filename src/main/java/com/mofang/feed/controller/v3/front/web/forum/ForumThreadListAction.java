package com.mofang.feed.controller.v3.front.web.forum;

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

@Action(url="feed/v2/forum/threadList")
public class ForumThreadListAction extends AbstractActionExecutor {

	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		String strForumId = context.getParamMap().get("fid");
		String strPageNum = context.getParameters("p");
		String strPageSize = context.getParameters("pagesize");
		String strType = context.getParameters("type");
		String strTagId = context.getParameters("tagId");
		String strTimeType = context.getParameters("timeType");
		
		///参数检查
		if(!StringUtil.isLong(strForumId)){
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
		
		if(tagId == 0){
			if(type == 1)
				return logic.getForumEliteThreadList(forumId, pageNum, pageSize, userId, timeType);
			else{
				if(timeType == QueryTimeType.CREATE_TIME)
					return logic.getForumThreadListByCreateTime(forumId, pageNum, pageSize, userId);
				else
					return logic.getForumThreadList(forumId, pageNum, pageSize, userId);
			}
		}else{
			if(type == 1)
				return logic.getForumEliteThreadList(forumId, tagId, pageNum, pageSize, userId, timeType);
			else
				return logic.getForumThreadListByTagId(forumId, tagId, pageNum, pageSize, userId, timeType);
		}
	}

}
