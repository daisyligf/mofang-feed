package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedForumTag;

public interface FeedForumTagDao
{
	public void add(FeedForumTag model) throws Exception;
	
	public void delete(long forumId, long tagId) throws Exception;
	
	public void deleteByTagId(int tagId) throws Exception;
	
	public List<Integer> getTagIdListByForumId(long forumId) throws Exception;
}