package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGameRank;

public interface FeedHomeRecommendGameRankService {

	public void edit(List<FeedHomeRecommendGameRank> modelList) throws Exception;
	
	public List<FeedHomeRecommendGameRank> getList() throws Exception;
}
