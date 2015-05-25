package com.mofang.feed.model;

import com.mofang.feed.global.common.ReplyType;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedReply
{
	private long sourceId;
	private int type = ReplyType.THREAD;

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}