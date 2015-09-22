package com.mofang.feed.controller.v3.admin.statistics;

import java.util.LinkedHashSet;
import java.util.Set;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedStatisticsLogic;
import com.mofang.feed.logic.admin.impl.FeedStatisticsLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;
import com.mysql.jdbc.StringUtils;

@Action(url = "feed/v3/backend/statistics/forumCount")
public class ForumStatisticsInfoAction extends AbstractActionExecutor {

	private FeedStatisticsLogic logic = FeedStatisticsLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		String strStartTime = context.getParameters("start_time");
		String strEndTime = context.getParameters("end_time");
		String strForumIds = context.getParameters("fids");
		String strType = context.getParameters("type");
		
		long startTime = 0l;
		if(StringUtil.isLong(strStartTime))
			startTime = Long.parseLong(strStartTime);
		
		long endTime = 0l;
		if(StringUtil.isLong(strEndTime))
			endTime = Long.parseLong(strEndTime);

		if(StringUtils.isNullOrEmpty(strForumIds)) {
			ResultValue result = new ResultValue();
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		
		int type = 0;
		if(StringUtil.isLong(strType)) 
			type = Integer.parseInt(strType);
		
		String[] strForumIdArr = strForumIds.split(",");
		Set<Long> forumIds = new LinkedHashSet<Long>();
		for(int idx = 0; idx < strForumIdArr.length; idx ++) {
			forumIds.add(Long.valueOf(strForumIdArr[idx]));
		}
		
		return logic.forumStatisticsInfos(forumIds, startTime, endTime, type);
	}

}
