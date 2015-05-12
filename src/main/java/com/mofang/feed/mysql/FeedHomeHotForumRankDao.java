package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeHotForumRank;

public interface FeedHomeHotForumRankDao {

	public void edit(FeedHomeHotForumRank model) throws Exception;
	
	public List<FeedHomeHotForumRank> getList() throws Exception;
}
