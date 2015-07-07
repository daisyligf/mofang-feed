package com.mofang.feed.controller.v3.external.post;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.LimitConstants;
import com.mofang.feed.logic.app.FeedPostLogic;
import com.mofang.feed.logic.app.impl.FeedPostLogicImpl;
import com.mofang.feed.model.FeedPost;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v3/external/post/add")
public class PostAddAction extends AbstractActionExecutor
{
	private FeedPostLogic logic = FeedPostLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		JSONObject json = new JSONObject(postData);
		long userId = json.optLong("uid", 0L);
		long threadId = json.optLong("tid", 0L);
		String content = json.optString("content", "");
		String htmlContent = content;
		JSONArray arrayPic = json.optJSONArray("pic");
		String pics = "";
		if(null != arrayPic)
		{
			for(int i=0; i<arrayPic.length(); i++)
				pics += arrayPic.getString(i) + ",";
		}
		if(pics.length() > 0)
			pics = pics.substring(0, pics.length() - 1);
		
		///参数检查
		if(threadId <= 0 || StringUtil.isNullOrEmpty(content)
				|| content.length() > LimitConstants.POST_CONTENT_LENGTH)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造Post实体对象
		FeedPost postInfo = new FeedPost();
		postInfo.setUserId(userId);
		postInfo.setThreadId(threadId);
		postInfo.setContent(content);
		postInfo.setHtmlContent(htmlContent);
		postInfo.setPictures(pics);
		
		return logic.add(postInfo);
	}
}