package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_black_list")
public class FeedBlackList
{
	@ColumnName(name = "forum_id")
	private long forumId;
	@ColumnName(name = "user_id")
	private long userId;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();

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

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}