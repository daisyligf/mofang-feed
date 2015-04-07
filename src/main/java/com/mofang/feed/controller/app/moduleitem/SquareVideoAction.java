package com.mofang.feed.controller.app.moduleitem;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedModuleItemLogic;
import com.mofang.feed.logic.impl.FeedModuleItemLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v2/square_video")
public class SquareVideoAction extends AbstractActionExecutor
{
	private FeedModuleItemLogic logic = FeedModuleItemLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strPageNum = context.getParameters("p");
		String strPageSize = context.getParameters("pagesize");
		String version = context.getParameters("cv");
		
		int pageNum = 1;
		int pageSize = 50;
		
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getSquareVideo(pageNum, pageSize, version);
	}
}