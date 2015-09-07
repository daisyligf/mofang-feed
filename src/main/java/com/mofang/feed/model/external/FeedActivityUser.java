package com.mofang.feed.model.external;

public class FeedActivityUser extends User{
	//楼层
	private int postion;
	//板块id（查看是否版主）
	private long forumId;

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public int getPostion() {
		return postion;
	}

	public void setPostion(int postion) {
		this.postion = postion;
	}

}
