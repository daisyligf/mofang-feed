package com.mofang.feed.controller.v3.admin.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeHotForumRankLogic;
import com.mofang.feed.logic.impl.FeedHomeHotForumRankLogicImpl;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;
import com.mysql.jdbc.StringUtils;

@Action(url = "backend/home/rank/hotforum/edit")
public class HomeHotForumRankEditAction extends AbstractActionExecutor {

	private FeedHomeHotForumRankLogic logic = FeedHomeHotForumRankLogicImpl
			.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String postData = context.getPostData();
		if (StringUtils.isNullOrEmpty(postData)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		JSONObject json = new JSONObject(postData);
		JSONArray jsonArr = json.optJSONArray("forum_ids");
		if (StringUtil.isNullOrEmpty(jsonArr.toString())) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		int length = jsonArr.length();
		List<FeedHomeHotForumRank> modelList = new ArrayList<FeedHomeHotForumRank>(
				length);
		for (int idx = 0; idx < length; idx++) {
			JSONObject jsonObj = jsonArr.getJSONObject(idx);

			int displayOrder = idx + 1;
			long forumId = jsonObj.optLong("forum_id", 0l);
			FeedHomeHotForumRank model = new FeedHomeHotForumRank();
			model.setForumId(forumId);
			model.setDisplayOrder(displayOrder);

			modelList.add(model);
		}
		return logic.edit(modelList);
	}

}
