package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGame;

public interface FeedHomeRecommendGameService {

	public void update(List<FeedHomeRecommendGame> modelList) throws Exception;
	
	public List<FeedHomeRecommendGame> getList() throws Exception;
}
