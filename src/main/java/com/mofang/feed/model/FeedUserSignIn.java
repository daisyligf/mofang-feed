package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

@TableName(name = "feed_user_sign_in")
public class FeedUserSignIn {

	@ColumnName(name = "user_id")
	@PrimaryKey
	private long userId;
	@ColumnName(name = "create_time")
	private long createTime;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	
}
