package com.mofang.feed.controller.v3.admin.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeNewspaperLogic;
import com.mofang.feed.logic.impl.FeedHomeNewspaperLogicImpl;
import com.mofang.feed.model.FeedHomeNewspaper;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "backend/home/updateNewspaper")
public class HomeNewspaperUpdateAction extends AbstractActionExecutor {

	private FeedHomeNewspaperLogic logic = FeedHomeNewspaperLogicImpl
			.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String postData = context.getPostData();
		if (StringUtil.isNullOrEmpty(postData)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		JSONObject json = new JSONObject(postData);
		JSONArray jsonArr = json.optJSONArray("data");
		if (StringUtil.isNullOrEmpty(jsonArr.toString())) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		int length = jsonArr.length();
		List<FeedHomeNewspaper> modelList = new ArrayList<FeedHomeNewspaper>(
				length);
		for (int idx = 0; idx < length; idx++) {
			JSONObject jsonObj = jsonArr.getJSONObject(idx);
			FeedHomeNewspaper model = new FeedHomeNewspaper();

			int displayOrder = idx + 1;
			String linkUrl = jsonObj.optString("link_url", "");
			String icon = jsonObj.optString("icon", "");
			model.setDisplayOrder(displayOrder);
			model.setLinkUrl(linkUrl);
			model.setIcon(icon);
			modelList.add(model);
		}
		return logic.update(modelList);
	}

}
