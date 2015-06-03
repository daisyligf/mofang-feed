package com.mofang.feed.controller.v3.admin.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedHomeTickerLogic;
import com.mofang.feed.logic.web.impl.FeedHomeTickerLogicImpl;
import com.mofang.feed.model.FeedHomeTicker;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/backend/home/ticker/edit")
public class HomeTickerEditAction extends AbstractActionExecutor {

	private FeedHomeTickerLogic logic = FeedHomeTickerLogicImpl
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
		if (StringUtil.isNullOrEmpty(postData)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long operatorId = Long.parseLong(strOperatorId);
		JSONObject json = new JSONObject(postData);
		JSONArray jsonArr = json.optJSONArray("data");
		if (StringUtil.isNullOrEmpty(jsonArr.toString())) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		int length = jsonArr.length();
		List<FeedHomeTicker> modelList = new ArrayList<FeedHomeTicker>(
				length);
		for (int idx = 0; idx < length; idx++) {
			JSONObject jsonObj = jsonArr.getJSONObject(idx);
			FeedHomeTicker model = new FeedHomeTicker();

			int displayOrder = idx + 1;
			String linkUrl = jsonObj.optString("link_url", "");
			String icon = jsonObj.optString("icon", "");
			model.setDisplayOrder(displayOrder);
			model.setLinkUrl(linkUrl);
			model.setIcon(icon);
			modelList.add(model);
		}
		return logic.edit(modelList, operatorId);
	}

}
