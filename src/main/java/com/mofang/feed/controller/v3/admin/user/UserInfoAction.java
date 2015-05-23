package com.mofang.feed.controller.v3.admin.user;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.admin.FeedUserLogic;
import com.mofang.feed.logic.admin.impl.FeedUserLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/backend/user/info")
public class UserInfoAction extends AbstractActionExecutor
{
	private FeedUserLogic logic = FeedUserLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		String strUserId = context.getParameters("user_id");
		
		long userId = 0L;
		if(StringUtil.isLong(strUserId))
			userId = Long.parseLong(strUserId);
		
		return logic.getInfo(userId);
	}
}