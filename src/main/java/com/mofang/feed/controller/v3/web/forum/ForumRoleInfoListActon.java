package com.mofang.feed.controller.v3.web.forum;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedSysUserRoleLogic;
import com.mofang.feed.logic.impl.FeedSysUserRoleLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url="feed/v2/forum/roleInfoList")
public class ForumRoleInfoListActon extends AbstractActionExecutor {

	private FeedSysUserRoleLogic logic = FeedSysUserRoleLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String strForumId = context.getParamMap().get("fid");
		if(!StringUtil.isLong(strForumId)){
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		long forumId = Long.parseLong(strForumId);	
		return logic.getRoleInfoList(forumId);
	}

}
