package com.mofang.feed.controller.v3.web.moderator;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedSysUserRoleLogic;
import com.mofang.feed.logic.web.impl.FeedSysUserRoleLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url="feed/v3/web/moderator/list")
public class ModeratorListActon extends AbstractActionExecutor {

	private FeedSysUserRoleLogic logic = FeedSysUserRoleLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strForumId = context.getParamMap().get("fid");
		if(!StringUtil.isLong(strForumId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		long forumId = Long.parseLong(strForumId);	
		
		if(forumId <= 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		return logic.getRoleList(forumId);
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
}