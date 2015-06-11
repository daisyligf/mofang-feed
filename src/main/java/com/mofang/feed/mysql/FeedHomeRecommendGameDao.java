package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGame;

public interface FeedHomeRecommendGameDao {

	public void edit(FeedHomeRecommendGame model) throws Exception;

	public void delete(long forumId) throws Exception;
	
	public List<FeedHomeRecommendGame> getList() throws Exception;
	
}
