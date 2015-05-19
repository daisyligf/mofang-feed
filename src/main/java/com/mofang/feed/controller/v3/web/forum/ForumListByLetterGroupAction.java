package com.mofang.feed.controller.v3.web.forum;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.logic.FeedHomeHotForumLogic;
import com.mofang.feed.logic.FeedHomeRecommendGameLogic;
import com.mofang.feed.logic.impl.FeedHomeHotForumLogicImpl;
import com.mofang.feed.logic.impl.FeedHomeRecommendGameLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/forum/listByLetterGroup")
public class ForumListByLetterGroupAction extends AbstractActionExecutor {

	private FeedHomeHotForumLogic hotForumLogic = FeedHomeHotForumLogicImpl
			.getInstance();
	private FeedHomeRecommendGameLogic recommendGameLogic = FeedHomeRecommendGameLogicImpl
			.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();

		String strPageNum = context.getParamMap().get("p");
		String strPageSize = context.getParamMap().get("pagesize");
		String strType = context.getParamMap().get("type");
		// 字母分组
		String strLetterGroup = context.getParamMap().get("letterGroup");

		int pageNum = 1;
		int pageSize = 8;
		int type = 0;

		if (StringUtil.isInteger(strType))
			type = Integer.parseInt(strType);
		if (StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		if (StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);

		if (type == ForumType.HOT_FORUM)
			return hotForumLogic.getListByLetterGroup(strLetterGroup, pageNum,
					pageSize);
		else if (type == ForumType.RECOMMEND_GAME)
			return recommendGameLogic.getListByLetterGroup(strLetterGroup,
					pageNum, pageSize);

		result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
		result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
		return result;
	}

}
