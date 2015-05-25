package com.mofang.feed.controller.admin.userrole;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedSysUserRoleLogic;
import com.mofang.feed.logic.impl.FeedSysUserRoleLogicImpl;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
public class UserRoleEditAction extends AbstractActionExecutor
{
	private FeedSysUserRoleLogic logic = FeedSysUserRoleLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
}