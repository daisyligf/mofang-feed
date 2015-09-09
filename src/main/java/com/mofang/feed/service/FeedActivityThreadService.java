package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;
import com.mofang.feed.model.external.FeedActivityUser;

public interface FeedActivityThreadService {

	public List<FeedActivityUser> generateRewardUserList(long threadId, FeedActivityThreadRewardCondition condition) throws Exception;
}
