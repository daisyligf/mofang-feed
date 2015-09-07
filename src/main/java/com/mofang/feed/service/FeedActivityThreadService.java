package com.mofang.feed.service;

import java.util.Map;

import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;
import com.mofang.feed.model.external.FeedActivityUser;

public interface FeedActivityThreadService {

	public Map<Long, FeedActivityUser> generateRewardUserList(long threadId, FeedActivityThreadRewardCondition condition) throws Exception;
}
