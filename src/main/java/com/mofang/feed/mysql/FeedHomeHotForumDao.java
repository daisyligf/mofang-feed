package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedHomeHotForum;

public interface FeedHomeHotForumDao {

	public void edit(FeedHomeHotForum model) throws Exception;
	
	public void delete(long forumId) throws Exception;
	
	public List<FeedHomeHotForum> getList() throws Exception;
}
