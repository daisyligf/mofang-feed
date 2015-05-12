package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

@TableName(name = "feed_home_hot_forum_list")
public class FeedHomeHotForum {

	@PrimaryKey
	@ColumnName(name = "forum_id")
	private long forumId;
	@ColumnName(name = "prefecture_url")
	private String prefectureUrl;
	@ColumnName(name = "gift_url")
	private String giftUrl;
	@ColumnName(name = "display_order")
	private int displayOrder;

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public String getPrefectureUrl() {
		return prefectureUrl;
	}

	public void setPrefectureUrl(String prefectureUrl) {
		this.prefectureUrl = prefectureUrl;
	}

	public String getGiftUrl() {
		return giftUrl;
	}

	public void setGiftUrl(String giftUrl) {
		this.giftUrl = giftUrl;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

}
