package com.mofang.feed.solr;

import java.util.List;

import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumSolr
{
	public void add(FeedForum model) throws Exception;
	
	public void batchAdd(List<FeedForum> forumList) throws Exception;
	
	public void deleteById(long forumId) throws Exception;
	
	public void deleteByIds(List<String> forumIds) throws Exception;
	
	public Page<FeedForum> search(String forumName, int start, int size) throws Exception;
}