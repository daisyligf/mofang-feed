package com.mofang.feed.logic;

import java.util.Set;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedForum;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumLogic
{
	public ResultValue add(FeedForum model, long operatorId) throws Exception;
	
	public ResultValue edit(FeedForum model, long operatorId) throws Exception;
	
	public ResultValue delete(long forumId, long operatorId) throws Exception;
	
	public ResultValue getInfo(long forumId) throws Exception;
	
	public ResultValue getForumList(long parentId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getForumList(Set<Long> forumIds) throws Exception;
	
	public ResultValue saveRecommendForumList(Set<Long> forumIds) throws Exception;
	
	public ResultValue getRecommendForumList() throws Exception;
	
	public ResultValue getHotForumList(int size) throws Exception;
	
	public ResultValue getHotForumList(Set<Long> forumIds) throws Exception;
	
	public ResultValue search(String forumName, int pageNum, int pageSize) throws Exception;
}