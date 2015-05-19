package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

@TableName(name = "feed_forum_follow")
public class FeedForumFollow {
	@PrimaryKey
	@ColumnName(name = "forum_id")
	private long forumId;
	@PrimaryKey
	@ColumnName(name = "user_id")
	private long userId;
	@ColumnName(name = "is_follow")
	private boolean isFollow;
	@ColumnName(name = "create_time")
	private long createTime;

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public boolean getIsFollow() {
		return isFollow;
	}

	public void setIsFollow(boolean isFollow) {
		this.isFollow = isFollow;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
