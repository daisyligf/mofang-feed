package com.mofang.feed.controller.v3.admin.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedHomeRecommendGameRankLogic;
import com.mofang.feed.logic.web.impl.FeedHomeRecommendGameRankLogicImpl;
import com.mofang.feed.model.FeedHomeRecommendGameRank;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;
import com.mysql.jdbc.StringUtils;

@Action(url = "feed/v2/backend/home/rank/recommendgame/edit")
public class HomeRecommendGameRankEditAction extends AbstractActionExecutor {

	private FeedHomeRecommendGameRankLogic logic = FeedHomeRecommendGameRankLogicImpl
			.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String strOperatorId = context.getParameters("uid");
		if(!StringUtil.isLong(strOperatorId)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		String postData = context.getPostData();
		if (StringUtils.isNullOrEmpty(postData)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		long operatorId = Long.parseLong(strOperatorId);
		JSONObject json = new JSONObject(postData);
		JSONArray jsonArr = json.optJSONArray("forum_ids");
		if (StringUtil.isNullOrEmpty(jsonArr.toString())) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		int length = jsonArr.length();
		List<FeedHomeRecommendGameRank> modelList = new ArrayList<FeedHomeRecommendGameRank>(
				length);
		for (int idx = 0; idx < length; idx++) {
			int displayOrder = idx + 1;
			long forumId = jsonArr.optLong(idx);
			FeedHomeRecommendGameRank model = new FeedHomeRecommendGameRank();
			model.setForumId(forumId);
			model.setDisplayOrder(displayOrder);
			modelList.add(model);
		}
		return logic.edit(modelList, operatorId);
	}

}
