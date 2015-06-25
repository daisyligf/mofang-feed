package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeRecommendGame;

public interface FeedHomeRecommendGameDao {

	public void add(FeedHomeRecommendGame model) throws Exception;

	public void delete(long forumId) throws Exception;
	
	public void deleteAll() throws Exception;
	
	public List<FeedHomeRecommendGame> getList() throws Exception;
	
}
