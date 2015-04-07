package com.mofang.feed.model.external;

import org.json.JSONObject;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedRecommendNotify
{
	private String action = "push_feed_recommend";
	private long userId;
	private long threadId;
	private String subject;
	private int recommendType; // /1:主题点赞 2:楼层点赞
	private long recommendUserId;
	private long postId; // /楼层点赞时使用
	private long forumId;
	private String forumName;
	private int position = 0;
	private boolean isShowNotify = true;
	private String clickAction;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getRecommendType() {
		return recommendType;
	}

	public void setRecommendType(int recommendType) {
		this.recommendType = recommendType;
	}

	public long getRecommendUserId() {
		return recommendUserId;
	}

	public void setRecommendUserId(long recommendUserId) {
		this.recommendUserId = recommendUserId;
	}

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isShowNotify() {
		return isShowNotify;
	}

	public void setShowNotify(boolean isShowNotify) {
		this.isShowNotify = isShowNotify;
	}

	public String getClickAction() {
		return clickAction;
	}

	public void setClickAction(String clickAction) {
		this.clickAction = clickAction;
	}

	public JSONObject toJson()
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put("act", action);
			json.put("uid", userId);
			JSONObject jsonMsg = new JSONObject();
			jsonMsg.put("thread_id", threadId);
			jsonMsg.put("subject", subject);
			jsonMsg.put("recommend_type", recommendType);
			jsonMsg.put("recommend_uid", recommendUserId);
			jsonMsg.put("post_id", postId);
			jsonMsg.put("forum_id", forumId);
			jsonMsg.put("forum_name", forumName);
			jsonMsg.put("position", position);
			json.put("msg", jsonMsg);
			json.put("is_show_notify", isShowNotify);
			json.put("click_act", clickAction);
			
			return json;
		}
		catch(Exception e)
		{
			return null;
		}
	}
}