package com.mofang.feed.solr;

import java.util.List;

import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedCommentSolr
{
	public void add(FeedComment model) throws Exception;
	
	public void batchAdd(List<FeedComment> list) throws Exception;
	
	public void deleteById(long commentId) throws Exception;
	
	public void deleteByIds(List<String> commentIds) throws Exception;
	
	public void deleteByForumId(long forumId) throws Exception;
	
	public void deleteByThreadId(long threadId) throws Exception;
	
	public void deleteByPostId(long postId) throws Exception;
	
	public Page<FeedComment> search(long forumId, String forumName, String author, String keyword, int status, int start, int size) throws Exception;
}