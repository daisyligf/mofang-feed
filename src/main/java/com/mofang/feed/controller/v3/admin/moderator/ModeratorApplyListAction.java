package com.mofang.feed.controller.v3.admin.moderator;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.admin.FeedModeratorApplyLogic;
import com.mofang.feed.logic.admin.impl.FeedModeratorApplyLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v3/backend/moderator/applylist")
public class ModeratorApplyListAction extends AbstractActionExecutor
{
	private FeedModeratorApplyLogic logic = FeedModeratorApplyLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.getList(pageNum, pageSize);
	}
}