package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeForumRank;

public interface FeedHomeForumRankService {

	public void update(List<FeedHomeForumRank> modelList) throws Exception;
	
	public List<FeedHomeForumRank> getList() throws Exception;
}
