package com.mofang.feed.controller.v3.web.thread;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedThreadLogic;
import com.mofang.feed.logic.web.impl.FeedThreadLogicImpl;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v3/web/thread/add")
public class ThreadAddAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("uid");
		if (!StringUtil.isLong(strUserId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		String postData = context.getPostData();
		if (StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long userId = Long.parseLong(strUserId);
		JSONObject json = new JSONObject(postData);
		long forumId = json.optLong("fid", 0L);
		String subject = json.optString("subject", "");
		String content = json.optString("content", "");
		String htmlContent = content;
		int tagId = json.optInt("tag_id", 0);

		// /参数检查
		if (forumId <= 0 || StringUtil.isNullOrEmpty(subject) || StringUtil.isNullOrEmpty(content) )
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		// /构造Thread实体对象
		FeedThread threadInfo = new FeedThread();
		threadInfo.setForumId(forumId);
		threadInfo.setUserId(userId);
		threadInfo.setSubject(subject);
		threadInfo.setTagId(tagId);

		// /构造Post实体对象
		FeedPost postInfo = new FeedPost();
		postInfo.setForumId(forumId);
		postInfo.setUserId(userId);
		postInfo.setContent(content);
		postInfo.setHtmlContent(htmlContent);
		threadInfo.setPost(postInfo);

		return logic.add(threadInfo);
	}
}
