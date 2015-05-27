package com.mofang.feed.controller.v3.front.web.thread;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.LimitConstants;
import com.mofang.feed.global.common.ThreadTag;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.feed.logic.FeedThreadLogic;
import com.mofang.feed.logic.impl.FeedThreadLogicImpl;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

@Action(url = "feed/v2/web/newthread")
public class ThreadAddAction extends AbstractActionExecutor {

	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception {
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("uid");
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

		long userId = Long.parseLong(strUserId);
		JSONObject json = new JSONObject(postData);
		long forumId = json.optLong("fid", 0L);
		String subject = json.optString("title", "");
		String content = json.optString("content", "");
		String htmlContent = content;
		String pics = json.optString("pic", "");
		String linkUrl = json.optString("linkurl", "");
		long videoId = json.optLong("video_id", 0L);
		int type = json.optInt("type", 0);
		String tags = json.optString("tags", "");
		//long moduleId = json.optLong("vid", 0L);
		int gameId = json.optInt("game_id", 0);
		int tagId = json.optInt("tag_id", 0);

		// /参数检查
		if (forumId <= 0 || StringUtil.isNullOrEmpty(subject)
				|| StringUtil.isNullOrEmpty(content) ) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}

		if(subject.length() > LimitConstants.SUBJECT_LENGTH 
				|| content.length() > LimitConstants.THREAD_CONTENT_LENGTH) {
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		
		// /判断是否为精华帖
		boolean isElite = tags.contains(String.valueOf(ThreadTag.ELITE));
		// /判断是否为视频帖
		boolean isVideo = videoId > 0L;
		// /判断是否为标红帖
		boolean isMark = tags.contains(String.valueOf(ThreadTag.MARK));

		// /主题类型
		int threadType = ThreadType.NORMAL;
		if (!StringUtil.isNullOrEmpty(linkUrl))
			threadType = ThreadType.ACTIVITY;
		else if (2 == type)
			threadType = ThreadType.QUESTION;

		// /构造Thread实体对象
		FeedThread threadInfo = new FeedThread();
		threadInfo.setForumId(forumId);
		threadInfo.setUserId(userId);
		threadInfo.setSubject(subject);
		threadInfo.setLinkUrl(linkUrl);
		threadInfo.setType(threadType);
		threadInfo.setLastPostUid(userId);
		threadInfo.setElite(isElite);
		threadInfo.setVideo(isVideo);
		threadInfo.setMark(isMark);
		threadInfo.setGameId(gameId);
		threadInfo.setTagId(tagId);

		// /构造Post实体对象
		FeedPost postInfo = new FeedPost();
		postInfo.setForumId(forumId);
		postInfo.setUserId(userId);
		postInfo.setContent(content);
		postInfo.setHtmlContent(htmlContent);
		postInfo.setPictures(pics);
		postInfo.setVideoId(videoId);
		threadInfo.setPost(postInfo);

		return logic.add(threadInfo, 0);
	}

}
