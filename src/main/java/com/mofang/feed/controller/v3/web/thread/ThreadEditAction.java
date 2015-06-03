package com.mofang.feed.controller.v3.web.thread;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.LimitConstants;
import com.mofang.feed.logic.web.FeedThreadLogic;
import com.mofang.feed.logic.web.impl.FeedThreadLogicImpl;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/web/thread/edit")
public class ThreadEditAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strOperatorId = context.getParamMap().get("uid");
		if (!StringUtil.isLong(strOperatorId))
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

		long operatorId = Long.parseLong(strOperatorId);
		JSONObject json = new JSONObject(postData);
		long threadId = json.optLong("tid", 0L);
		String subject = json.optString("subject", "");
		String content = json.optString("content", "");
		String htmlContent = content;
		int tagId = json.optInt("tag_id", 0);

		// /参数检查
		if (threadId <= 0 || StringUtil.isNullOrEmpty(subject) || StringUtil.isNullOrEmpty(content))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		if(subject.length() > LimitConstants.SUBJECT_LENGTH  || content.length() > LimitConstants.THREAD_CONTENT_LENGTH)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		// /构建主题实体
		FeedThread threadInfo = new FeedThread();
		threadInfo.setThreadId(threadId);
		threadInfo.setSubject(subject);
		threadInfo.setTagId(tagId);

		// /构建楼层实体
		FeedPost postInfo = new FeedPost();
		postInfo.setContent(content);
		postInfo.setHtmlContent(htmlContent);
		threadInfo.setPost(postInfo);
		return logic.edit(threadInfo, operatorId);
	}
}