package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

@TableName(name = "feed_home_newspaper")
public class FeedHomeNewspaper {

	@PrimaryKey
	@ColumnName(name = "display_order")
	private int displayOrder;
	@ColumnName(name = "icon")
	private String icon;
	@ColumnName(name = "linkUrl")
	private String linkUrl;

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

}
