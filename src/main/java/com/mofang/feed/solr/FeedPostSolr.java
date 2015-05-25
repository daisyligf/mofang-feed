package com.mofang.feed.solr;

import java.util.List;

import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostSolr
{
	public void add(FeedPost model) throws Exception;
	
	public void batchAdd(List<FeedPost> list) throws Exception;
	
	public void deleteById(long postId) throws Exception;
	
	public void deleteByIds(List<String> postIds) throws Exception;
	
	public void deleteByForumId(long forumId) throws Exception;
	
	public void deleteByThreadId(long threadId) throws Exception;
	
	public Page<FeedPost> search(long forumId, String forumName, String author, String keyword, int status, int start, int size) throws Exception;
}