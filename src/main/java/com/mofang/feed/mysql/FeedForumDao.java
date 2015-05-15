package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedForum;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumDao
{
	public long getMaxId() throws Exception;
	
	public void add(FeedForum model) throws Exception;
	
	public void update(FeedForum model) throws Exception;
	
	public void delete(long forumId) throws Exception;
	
	public void incrThreads(long forumId) throws Exception;
	
	public void decrThreads(long forumId) throws Exception;
	
	public FeedForum getInfo(long forumId) throws Exception;
	
	public List<FeedForum> getList(Operand operand) throws Exception;
	
	public List<FeedForum> getForumList(long parentId, int start, int end) throws Exception;
	
	public long getForumCount(long parentId) throws Exception;
	
	public List<Long> getForumIdList(long type) throws Exception;
}