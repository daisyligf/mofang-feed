package com.mofang.feed.controller.v3.admin.forum;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.admin.FeedForumLogic;
import com.mofang.feed.logic.admin.impl.FeedForumLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v3/backend/forum/list")
public class ForumListAction extends AbstractActionExecutor
{
	private FeedForumLogic logic = FeedForumLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strType = context.getParameters("type");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		int type = 0;
		if(StringUtil.isInteger(strType))
			type = Integer.parseInt(strType);
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getForumList(type, pageNum, pageSize);
	}
}