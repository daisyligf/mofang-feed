package com.mofang.feed.controller.admin.module;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.FeedModuleLogic;
import com.mofang.feed.logic.impl.FeedModuleLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/vitforum/getVitForums")
public class ModuleListAction extends AbstractActionExecutor
{
	private FeedModuleLogic logic = FeedModuleLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		return logic.getList();
	}
}