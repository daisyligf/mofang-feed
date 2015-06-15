package com.mofang.feed.controller.v3.admin.admin;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.admin.FeedAdminUserLogic;
import com.mofang.feed.logic.admin.impl.FeedAdminUserLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v3/backend/admin/exists")
public class AdminExistsAction extends AbstractActionExecutor
{
	private FeedAdminUserLogic logic = FeedAdminUserLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strUserId = context.getParameters("user_id");
		
		long userId = 0L;
		if(StringUtil.isLong(strUserId))
			userId = Long.parseLong(strUserId);
		
		return logic.exists(userId);
	}
}