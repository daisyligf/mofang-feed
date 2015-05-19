package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGame;
import com.mofang.feed.model.Page;

public interface FeedHomeRecommendGameService {

	public void edit(List<FeedHomeRecommendGame> modelList) throws Exception;
	
	public List<FeedHomeRecommendGame> getList() throws Exception;
	
	public Page<FeedHomeRecommendGame> getListByLetterGroup(String letterGroup, int pageNum, int pageSize) throws Exception;
}
