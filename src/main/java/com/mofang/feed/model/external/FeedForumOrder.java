package com.mofang.feed.model.external;

public class FeedForumOrder implements Comparable<FeedForumOrder> {

	private long forumId;
	private long createTime;
	private long orderValue;

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getOrderValue() {
		return orderValue;
	}

	public void setOrderValue(long orderValue) {
		this.orderValue = orderValue;
	}

	@Override
	public int compareTo(FeedForumOrder o) {
		return orderValue > o.orderValue ? -1
				: (orderValue == o.orderValue) ? 0 : 1;
	}

}
