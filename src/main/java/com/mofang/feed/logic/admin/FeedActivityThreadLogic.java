package com.mofang.feed.logic.admin;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;

public interface FeedActivityThreadLogic {

	public ResultValue generateRewardUserList(long operatorId, long threadId, FeedActivityThreadRewardCondition condition) throws Exception;
}
