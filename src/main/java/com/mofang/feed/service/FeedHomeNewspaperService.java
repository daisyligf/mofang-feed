package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeNewspaper;

public interface FeedHomeNewspaperService {

	public void update(List<FeedHomeNewspaper> modelList) throws Exception;
	
	public List<FeedHomeNewspaper> getList() throws Exception;
}
