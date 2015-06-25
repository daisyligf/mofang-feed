package com.mofang.feed.controller.v3.admin.home;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.UpDownStatus;
import com.mofang.feed.logic.web.FeedHomeHotForumRankLogic;
import com.mofang.feed.logic.web.impl.FeedHomeHotForumRankLogicImpl;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;
import com.mysql.jdbc.StringUtils;

@Action(url = "feed/v3/backend/home/rank/hotforum/edit")
public class HomeHotForumRankEditAction extends AbstractActionExecutor {

	private FeedHomeHotForumRankLogic logic = FeedHomeHotForumRankLogicImpl
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
		//排重
		LinkedHashSet<Long> forumIdSet = new LinkedHashSet<Long>(length);
		for(int idx = 0; idx < length; idx ++) {
			long forumId = jsonArr.optLong(idx);
			forumIdSet.add(forumId);
		}
		List<FeedHomeHotForumRank> modelList = new ArrayList<FeedHomeHotForumRank>(
				length);
		int displayOrder= 0;
		for (Long forumId : forumIdSet) {
			displayOrder++;
			FeedHomeHotForumRank model = new FeedHomeHotForumRank();
			model.setForumId(forumId);
			model.setDisplayOrder(displayOrder);
			model.setUpDown(UpDownStatus.UP);
			modelList.add(model);
		}
		return logic.edit(modelList, operatorId);
	}

}
