package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGameRank;

public interface FeedHomeRecommendGameRankDao {

	public void update(FeedHomeRecommendGameRank model) throws Exception;
	
	public List<FeedHomeRecommendGameRank> getList() throws Exception;
}
