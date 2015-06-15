package com.mofang.feed.controller.v3.admin.home;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedHomeKeyWordLogic;
import com.mofang.feed.logic.web.impl.FeedHomeKeyWordLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;
import com.mysql.jdbc.StringUtils;

@Action(url = "feed/v3/backend/home/search/keyword/edit")
public class HomeKeyWordEditAction extends AbstractActionExecutor {

	private FeedHomeKeyWordLogic logic = FeedHomeKeyWordLogicImpl.getInstance();
	
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
		String keyWord = json.optString("key_word", "");
		if(StringUtils.isNullOrEmpty(keyWord)){
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		return logic.setKeyWord(keyWord, operatorId);
	}

}
