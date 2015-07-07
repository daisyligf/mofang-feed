package com.mofang.feed.mysql;

import com.mofang.feed.model.FeedThreadRepliesReward;

public interface FeedThreadRepliesRewardDao {

	public FeedThreadRepliesReward getModel(long threadId) throws Exception;
	
	public void update(long threadId, int level, int exp) throws Exception;
}
