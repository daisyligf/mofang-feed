package com.mofang.feed.controller.admin.role;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedSysRoleLogic;
import com.mofang.feed.logic.impl.FeedSysRoleLogicImpl;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
public class RoleListAction extends AbstractActionExecutor
{
	private FeedSysRoleLogic logic = FeedSysRoleLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		return logic.getList();
	}
}