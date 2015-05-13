package com.mofang.feed.controller.v3.admin.tag;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedTagLogic;
import com.mofang.feed.logic.impl.FeedTagLogicImpl;
import com.mofang.feed.model.FeedTag;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "backend/tag/add")
public class TagAddAction extends AbstractActionExecutor {

	private FeedTagLogic logic = FeedTagLogicImpl.getInstance();

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
		String tagName = json.optString("tag_name", "");

		FeedTag model = new FeedTag();
		model.setTagName(tagName);
		model.setCreateTime(System.currentTimeMillis());
		return logic.add(model);
	}

}
