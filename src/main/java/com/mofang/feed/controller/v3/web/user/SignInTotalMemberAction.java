package com.mofang.feed.controller.v3.web.user;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.logic.web.FeedUserSignInLogic;
import com.mofang.feed.logic.web.impl.FeedUserSignInLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/web/user/signInTotalMember")
public class SignInTotalMemberAction extends AbstractActionExecutor {

	private FeedUserSignInLogic logic = FeedUserSignInLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		return logic.totalMember();
	}

}
