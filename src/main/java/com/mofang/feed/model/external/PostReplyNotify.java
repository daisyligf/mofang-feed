package com.mofang.feed.model.external;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.util.MiniTools;

/**
 * 
 * @author zhaodx
 *
 */
public class PostReplyNotify
{
	private String action = "push_post_reply";
	private long userId;
	private int messageType = 1;
	private long postId;
	private String postTitle;
	private long replyId;
	private String replyText;
	private String replyPictures;
	private long replyUserId;
	private int replyType;   /// 1：回复楼主 2：回复层主
	private long forumId;
	private String forumName;
	private int position = 0;
	private boolean isShowNotify = true;
	private String clickAction;

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public int getMessageType() 
	{
		return messageType;
	}

	public void setMessageType(int messageType)
	{
		this.messageType = messageType;
	}

	public long getPostId()
	{
		return postId;
	}

	public void setPostId(long postId)
	{
		this.postId = postId;
	}

	public String getPostTitle()
	{
		return postTitle;
	}

	public void setPostTitle(String postTitle)
	{
		this.postTitle = postTitle;
	}

	public long getReplyId()
	{
		return replyId;
	}

	public void setReplyId(long replyId)
	{
		this.replyId = replyId;
	}

	public String getReplyText()
	{
		return replyText;
	}

	public void setReplyText(String replyText)
	{
		this.replyText = replyText;
	}

	public String getReplyPictures()
	{
		return replyPictures;
	}

	public void setReplyPictures(String replyPictures)
	{
		this.replyPictures = replyPictures;
	}

	public long getReplyUserId()
	{
		return replyUserId;
	}

	public void setReplyUserId(long replyUserId) 
	{
		this.replyUserId = replyUserId;
	}

	public int getReplyType()
	{
		return replyType;
	}

	public void setReplyType(int replyType)
	{
		this.replyType = replyType;
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

	public boolean isShowNotify()
	{
		return isShowNotify;
	}

	public void setShowNotify(boolean isShowNotify)
	{
		this.isShowNotify = isShowNotify;
	}

	public String getClickAction()
	{
		return clickAction;
	}

	public void setClickAction(String clickAction)
	{
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
			JSONObject jsonMsgContent = new JSONObject();
			JSONObject jsonReplyContent = new JSONObject();
			
			jsonMsg.put("msg_type", messageType);
			jsonMsg.put("forum_id", forumId);
			jsonMsg.put("forum_name", forumName);
			jsonMsg.put("position", position);
			jsonMsgContent.put("post_id", postId);
			jsonMsgContent.put("post_title", null == postTitle ? "" : postTitle);
			jsonMsgContent.put("reply_id", replyId);
			jsonReplyContent.put("text", null == replyText ? "" : replyText);
			JSONArray picArray = new JSONArray();
			if(null != replyPictures)
				picArray.put(MiniTools.StringToJSONArray(replyPictures));
			jsonReplyContent.put("pictures", picArray);
			jsonMsgContent.put("reply_content", jsonReplyContent);
			jsonMsgContent.put("reply_uid", replyUserId);
			jsonMsgContent.put("reply_type", replyType);
			jsonMsg.put("content", jsonMsgContent);
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