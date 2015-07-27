package com.mofang.feed.model;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostAndComment
{
	private long threadId;
	private String subject;
	private long forumId;
	private String forumName;
	private long postId;
	private int position = 1;
	private String replyContent;
	private String replyPics;
	private long replyTime = System.currentTimeMillis();

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

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public long getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(long replyTime) {
		this.replyTime = replyTime;
	}

	public String getReplyPics() {
		return replyPics;
	}

	public void setReplyPics(String replyPics) {
		this.replyPics = replyPics;
	}
	
	
}