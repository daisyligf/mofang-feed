package com.mofang.feed.controller.v3.admin.tag;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedTagLogic;
import com.mofang.feed.logic.impl.FeedTagLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "backend/tag/delete")
public class TagDeleteAction extends AbstractActionExecutor {

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
		JSONArray jsonArr = json.optJSONArray("tag_ids");
		if (StringUtil.isNullOrEmpty(jsonArr.toString())) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		int length = jsonArr.length();
		List<Integer> list = new ArrayList<Integer>(length);
		for(int idx = 0; idx < length; idx++){
			int tagId = jsonArr.optInt(idx);
			list.add(tagId);
		}
		
		return logic.delete(list);
	}

}
