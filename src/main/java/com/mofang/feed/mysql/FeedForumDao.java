package com.mofang.feed.mysql;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.external.FeedForumOrder;
import com.mofang.feed.model.external.ForumCount;
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
	
	public void incrFollows(long forumId) throws Exception;
	
	public void decrFollows(long forumId) throws Exception;
	
	public FeedForum getInfo(long forumId) throws Exception;
	
	public List<FeedForum> getList(Operand operand) throws Exception;
	
	public List<FeedForum> getForumList(int type, int start, int end) throws Exception;
	
	public long getForumCount(int type) throws Exception;
	
	public List<FeedForumOrder> getForumOrderList(long type) throws Exception;
	
	public Map<Long,ForumCount> getRecommendCount(Set<Long> forumids, long startTime, long endTime) throws Exception;
}