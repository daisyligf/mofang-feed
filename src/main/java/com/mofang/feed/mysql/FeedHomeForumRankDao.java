package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeForumRank;

public interface FeedHomeForumRankDao {

	public void update(FeedHomeForumRank model) throws Exception;
	
	public List<FeedHomeForumRank> getList() throws Exception;
}
