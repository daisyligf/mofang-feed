package com.mofang.feed.controller.v3.web.post;

import java.util.HashSet;
import java.util.Set;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedPostLogic;
import com.mofang.feed.logic.web.impl.FeedPostLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url="feed/v3/web/post/listByUserIds")
public class PostListActionByUserIdsAction extends AbstractActionExecutor{

	private FeedPostLogic logic = FeedPostLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String strThread = context.getParamMap().get("tid");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		String strUserIds = context.getParameters("userIds");
		String strInclude = context.getParameters("include");
		
		///参数检查
		if(!StringUtil.isLong(strThread) || StringUtil.isNullOrEmpty(strUserIds))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		long threadId = Long.parseLong(strThread);		
		int pageNum = 1;
		int pageSize = 10;
		int include = 0;
		
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		if(StringUtil.isInteger(strInclude))
			include = Integer.parseInt(strInclude);
		
		String[] arrUserId = strUserIds.split(",");
		Set<Long> setUserId = new HashSet<Long>(arrUserId.length);
		for(String strUid : arrUserId) {
			setUserId.add(Long.parseLong(strUid.trim()));
		}
		
		return logic.getThreadPostList(threadId, pageNum, pageSize, setUserId, include == 0);
	}

	protected boolean needCheckAtom()
	{
		return false;
	}
}
