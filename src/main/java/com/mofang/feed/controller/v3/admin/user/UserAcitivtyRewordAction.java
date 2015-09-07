package com.mofang.feed.controller.v3.admin.user;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedActivityThreadLogic;
import com.mofang.feed.logic.admin.impl.FeedActivityThreadLogicImpl;
import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/backend/user/activityRewardList")
public class UserAcitivtyRewordAction extends AbstractActionExecutor {

	private FeedActivityThreadLogic logic = FeedActivityThreadLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		String strOperatorId = context.getParameters("uid");
		if(!StringUtil.isLong(strOperatorId)) {
			ResultValue result = new ResultValue();
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		
		long operatorId = 0l;
		if(StringUtil.isLong(strOperatorId))
			operatorId = Long.parseLong(strOperatorId);
		
		String strStartTime = context.getParameters("start_time");
		String strEndTime = context.getParameters("end_time");
		String strHavePic = context.getParameters("have_pic");
		String strAdmin = context.getParameters("admin");
		String strThreadId = context.getParameters("tid");
		
		long threadId = 0l;
		if(StringUtil.isLong(strThreadId))
			threadId = Long.parseLong(strThreadId);
		
		long startTime = 0l;
		if(StringUtil.isLong(strStartTime))
			startTime = Long.parseLong(strStartTime);
		
		long endTime = 0l;
		if(StringUtil.isLong(strEndTime))
			endTime = Long.parseLong(strEndTime);
		
		byte havePic = 0;
		if(StringUtil.isByte(strHavePic))
			havePic = Byte.parseByte(strHavePic);
		
		byte admin = 0;
		if(StringUtil.isByte(strAdmin)) 
			admin = Byte.parseByte(strAdmin);
		
		FeedActivityThreadRewardCondition condition = new FeedActivityThreadRewardCondition();
		condition.admin = admin == 1;
		condition.havePic = havePic == 1;
		condition.startTime = startTime;
		condition.endTime = endTime;
		
		return logic.generateRewardUserList(operatorId, threadId, condition);
	}

}
