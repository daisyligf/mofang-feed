package com.mofang.feed.controller.v3.external.test;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/external/test/stdout")
public class PrintHelloAction extends AbstractActionExecutor {

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();

		JSONObject data = new JSONObject();
		data.put("data", "hello the bbs.");
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		result.setData(data);
		return result;
	}

}
