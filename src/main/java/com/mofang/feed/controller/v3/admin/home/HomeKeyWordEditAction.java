package com.mofang.feed.controller.v3.admin.home;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeKeyWordLogic;
import com.mofang.feed.logic.impl.FeedHomeKeyWordLogicImpl;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;
import com.mysql.jdbc.StringUtils;

@Action(url = "feed/v2/backend/home/search/keyword/edit")
public class HomeKeyWordEditAction extends AbstractActionExecutor {

	private FeedHomeKeyWordLogic logic = FeedHomeKeyWordLogicImpl.getInstance();
	
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
		String keyWord = json.optString("key_word", "");
		if(StringUtils.isNullOrEmpty(keyWord)){
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		return logic.setKeyWord(keyWord);
	}

}
