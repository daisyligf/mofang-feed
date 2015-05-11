package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeHotForum;

public interface FeedHomeHotForumService {

	public void update(List<FeedHomeHotForum> modelList) throws Exception;
	
	public List<FeedHomeHotForum> getList() throws Exception;
}
