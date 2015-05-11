package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeNewspaper;

public interface FeedHomeNewspaperDao {

	public void update(FeedHomeNewspaper model) throws Exception;
	
	public List<FeedHomeNewspaper> getList() throws Exception;
}
