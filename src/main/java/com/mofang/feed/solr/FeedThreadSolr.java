package com.mofang.feed.solr;

import java.util.List;

import com.mofang.feed.model.FeedThread;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedThreadSolr
{
	public void add(FeedThread model) throws Exception;
	
	public void batchAdd(List<FeedThread> list) throws Exception;
	
	public void deleteById(long threadId) throws Exception;
	
	public void deleteByIds(List<String> threadIds) throws Exception;
	
	public void deleteByForumId(long forumId) throws Exception;
}