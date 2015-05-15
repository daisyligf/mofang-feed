package com.mofang.feed.mysql;

import com.mofang.feed.model.FeedForumFollow;

public interface FeedForumFollowDao {

	public void add(FeedForumFollow model) throws Exception;
	
	public void edit(FeedForumFollow model) throws Exception;
	
	public long getYesterdayFollow() throws Exception;
}
