package com.mofang.feed.solr;

import java.util.List;

import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;

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
	
	public Page<FeedThread> search(long forumId, String forumName, String author, String keyword, int status, int start, int size) throws Exception;
}