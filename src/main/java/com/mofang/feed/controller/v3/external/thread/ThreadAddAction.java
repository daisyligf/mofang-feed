package com.mofang.feed.controller.v3.external.thread;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.app.FeedThreadLogic;
import com.mofang.feed.logic.app.impl.FeedThreadLogicImpl;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v3/external/thread/add")
public class ThreadAddAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

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
		long forumId = json.optLong("fid", 0L);
		String subject = json.optString("subject", "");
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
		if(forumId <= 0 || StringUtil.isNullOrEmpty(subject) || StringUtil.isNullOrEmpty(content))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造Thread实体对象
		FeedThread threadInfo = new FeedThread();
		threadInfo.setForumId(forumId);
		threadInfo.setUserId(userId);
		threadInfo.setSubject(subject);
		threadInfo.setLastPostUid(userId);
		
		///构造Post实体对象
		FeedPost postInfo = new FeedPost();
		postInfo.setForumId(forumId);
		postInfo.setUserId(userId);
		postInfo.setContent(content);
		postInfo.setHtmlContent(htmlContent);
		postInfo.setPictures(pics);
		threadInfo.setPost(postInfo);
		
		return logic.add(threadInfo);
	}
}