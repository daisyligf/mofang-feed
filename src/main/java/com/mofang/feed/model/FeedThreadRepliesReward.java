package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

@TableName(name = "feed_thread_replies_reward")
public class FeedThreadRepliesReward {
	@ColumnName(name = "thread_id")
	@PrimaryKey
	private long threadId;
	@ColumnName(name = "level")
	private int level;
	@ColumnName(name = "exp")
	private int exp;
	
	public long getThreadId() {
		return threadId;
	}
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	
}
