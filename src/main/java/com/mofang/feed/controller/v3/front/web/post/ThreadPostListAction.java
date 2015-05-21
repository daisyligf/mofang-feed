package com.mofang.feed.controller.v3.front.web.post;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.RequestFrom;
import com.mofang.feed.logic.FeedPostLogic;
import com.mofang.feed.logic.impl.FeedPostLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/floorlist")
public class ThreadPostListAction extends AbstractActionExecutor
{
	private FeedPostLogic logic = FeedPostLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		String strThread = context.getParamMap().get("tid");
		String strPageNum = context.getParameters("p");
		String strPageSize = context.getParameters("pagesize");
		String strType = context.getParameters("type");
		
		///参数检查
		if(!StringUtil.isLong(strThread))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long threadId = Long.parseLong(strThread);		
		long userId = 0L;
		int pageNum = 1;
		int pageSize = 10;
		int type = 0;
		
		if(StringUtil.isLong(strUserId))
			userId = Long.parseLong(strUserId);
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		if(StringUtil.isInteger(strType))
			type = Integer.parseInt(strType);
		
		if(1 == type)    ///只看楼主
			return logic.getHostPostList(threadId, pageNum, pageSize, userId, RequestFrom.WEB);
		else
			return logic.getThreadPostList(threadId, pageNum, pageSize, userId, RequestFrom.WEB);
	}
}