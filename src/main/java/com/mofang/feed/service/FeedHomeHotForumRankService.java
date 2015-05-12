package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeHotForumRank;

public interface FeedHomeHotForumRankService {

	public void edit(List<FeedHomeHotForumRank> modelList) throws Exception;
	
	public List<FeedHomeHotForumRank> getList() throws Exception;
}
