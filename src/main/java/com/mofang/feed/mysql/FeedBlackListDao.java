package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedBlackList;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedBlackListDao
{
	public void add(FeedBlackList model) throws Exception;
	
	public void delete(long forumId, long userId) throws Exception;
	
	public void deleteByForumId(long forumId) throws Exception;
	
	public List<FeedBlackList> getUserListByForumId(long forumId) throws Exception;
	
	public List<FeedBlackList> getList(Operand operand) throws Exception;
}