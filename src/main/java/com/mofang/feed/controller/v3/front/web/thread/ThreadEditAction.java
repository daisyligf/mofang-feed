package com.mofang.feed.controller.v3.front.web.thread;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.LimitConstants;
import com.mofang.feed.logic.FeedThreadLogic;
import com.mofang.feed.logic.impl.FeedThreadLogicImpl;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/thread/edit")
public class ThreadEditAction extends AbstractActionExecutor {

	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String strUserId = context.getParamMap().get("uid");
		if (!StringUtil.isLong(strUserId)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		String postData = context.getPostData();
		if (StringUtil.isNullOrEmpty(postData)) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		long operatorId = Long.parseLong(strUserId);
		JSONObject json = new JSONObject(postData);
		long threadId = json.optLong("tid", 0L);
		String subject = json.optString("subject", "");
		String content = json.optString("content", "");
		String htmlContent = content;
		String pics = json.optString("pic", "");
		long videoId = json.optLong("video_id", 0);
		String tags = json.optString("tags", "");
		long moduleId = json.optLong("vid", 0);
		int gameId = json.optInt("game_id", 0);

		// /参数检查
		if (threadId <= 0 || StringUtil.isNullOrEmpty(subject)
				|| StringUtil.isNullOrEmpty(content)
				|| content.length() > LimitConstants.THREAD_CONTENT_LENGTH) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		// /判断是否为精华帖
		boolean isElite = tags.contains("1");
		// /判断是否为视频帖
		boolean isVideo = videoId > 0L;
		// /判断是否为标红帖
		boolean isMark = tags.contains("");

		// /构建主题实体
		FeedThread threadInfo = new FeedThread();
		threadInfo.setThreadId(threadId);
		threadInfo.setSubject(subject);
		threadInfo.setElite(isElite);
		threadInfo.setVideo(isVideo);
		threadInfo.setMark(isMark);
		threadInfo.setGameId(gameId);

		// /构建楼层实体
		FeedPost postInfo = new FeedPost();
		postInfo.setContent(content);
		postInfo.setHtmlContent(htmlContent);
		postInfo.setPictures(pics);
		postInfo.setVideoId(videoId);
		threadInfo.setPost(postInfo);
		return logic.edit(threadInfo, moduleId, operatorId);
	}

}
