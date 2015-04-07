package com.mofang.feed.controller.admin.forum;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
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
@Action(url="backend/forum/list")
public class ForumListAction extends AbstractActionExecutor
{
	private FeedForumLogic logic = FeedForumLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strParentId = context.getParameters("parentId");
		String strPageNum = context.getParameters("startPage");
		String strPageSize = context.getParameters("pageSize");
		
		long parentId = 0L;
		if(StringUtil.isLong(strParentId))
			parentId = Long.parseLong(strParentId);
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getForumList(parentId, pageNum, pageSize);
	}
}