package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeHotForumRank;

public interface FeedHomeHotForumRankDao {

	public void add(FeedHomeHotForumRank model) throws Exception;
	
	public void delete(long forumId) throws Exception;
	
	public void deleteAll() throws Exception;
	
	public List<FeedHomeHotForumRank> getList() throws Exception;
}
