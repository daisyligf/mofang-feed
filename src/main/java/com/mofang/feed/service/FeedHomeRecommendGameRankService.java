package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGameRank;

public interface FeedHomeRecommendGameRankService {

	public void update(List<FeedHomeRecommendGameRank> modelList) throws Exception;
	
	public List<FeedHomeRecommendGameRank> getList() throws Exception;
}
