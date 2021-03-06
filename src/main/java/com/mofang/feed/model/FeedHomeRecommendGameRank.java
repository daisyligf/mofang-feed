package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

@TableName(name = "feed_home_recommend_game_rank")
public class FeedHomeRecommendGameRank {
	@PrimaryKey
	@ColumnName(name = "forum_id")
	private long forumId;
	@ColumnName(name = "display_order")
	private int displayOrder;
	@ColumnName(name = "gift_url")
	private String giftUrl;
	@ColumnName(name = "download_url")
	private String downloadUrl;

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getGiftUrl() {
		return giftUrl;
	}

	public void setGiftUrl(String giftUrl) {
		this.giftUrl = giftUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

}
