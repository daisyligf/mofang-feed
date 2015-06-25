package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGameRank;

public interface FeedHomeRecommendGameRankDao {

	public void add(FeedHomeRecommendGameRank model) throws Exception;
	
	public void delete(long forumId) throws Exception;
	
	public void deleteAll() throws Exception;
	
	public List<FeedHomeRecommendGameRank> getList() throws Exception;
}
