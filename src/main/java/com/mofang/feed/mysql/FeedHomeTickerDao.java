package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeTicker;

public interface FeedHomeTickerDao {

	public void update(FeedHomeTicker model) throws Exception;
	
	public List<FeedHomeTicker> getList() throws Exception;
}
