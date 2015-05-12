package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeTicker;

public interface FeedHomeTickerService {

	public void edit(List<FeedHomeTicker> modelList) throws Exception;
	
	public List<FeedHomeTicker> getList() throws Exception;
}
