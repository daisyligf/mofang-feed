package com.mofang.feed.service;

import java.util.Set;

import com.mofang.feed.model.FeedForumTag;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumTagService
{
	public void add(FeedForumTag model) throws Exception;
	
	public void addBatch(long forumId, Set<Integer> tagSet) throws Exception;
	
	public void delete(long forumId, long tagId) throws Exception;
	
	public void deleteByTagId(int tagId) throws Exception;
	
	public void deleteByForumId(long forumId) throws Exception;
	
	public Set<Integer> getTagIdListByForumId(long forumId) throws Exception;
}