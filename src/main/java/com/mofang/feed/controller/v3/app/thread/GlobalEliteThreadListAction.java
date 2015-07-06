package com.mofang.feed.controller.v3.app.thread;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.app.FeedThreadLogic;
import com.mofang.feed.logic.app.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v3/app/thread/hotlist")
public class GlobalEliteThreadListAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		int pageNum = 1;
		int pageSize = 50;
		
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getGlobalEliteThreadList(pageNum, pageSize);
	}
}