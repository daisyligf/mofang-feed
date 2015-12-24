package com.mofang.feed.controller.v3.web.post;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedPostLogic;
import com.mofang.feed.logic.web.impl.FeedPostLogicImpl;
import com.mofang.feed.model.FeedPost;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url="feed/v3/web/post/edit")
public class PostEditAction extends AbstractActionExecutor {

	private FeedPostLogic logic = FeedPostLogicImpl.getInstance();
	
	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {

		ResultValue result = new ResultValue();
		String strOperatorId = context.getParamMap().get("uid");
		if(!StringUtil.isLong(strOperatorId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		long operatorId = Long.parseLong(strOperatorId);
		JSONObject json = new JSONObject(postData);
		long postId = json.optLong("pid", 0L);
		String content = json.optString("content", "");
		String htmlContent = content;
		String pics = json.optString("pic", "");
		
		///参数检查
		if(postId <= 0 || StringUtil.isNullOrEmpty(content))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造Post实体对象
		FeedPost postInfo = new FeedPost();
		postInfo.setPostId(postId);
		postInfo.setContent(content);
		postInfo.setHtmlContent(htmlContent);
		postInfo.setPictures(pics);
		
		return logic.edit(postInfo, operatorId);
	
	}

}
