package com.mofang.feed.logic.app;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedThread;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedThreadLogic
{
	public ResultValue add(FeedThread model) throws Exception;
	
	public ResultValue delete(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue setTop(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue cancelTop(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue setElite(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue cancelElite(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue recommend(long userId, long threadId) throws Exception;
	
	public ResultValue getForumThreadList(long forumId, int pageNum, int pageSize, long currentUserId) throws Exception;
	
	public ResultValue getForumTopThreadList(long forumId) throws Exception;
	
	public ResultValue getForumEliteThreadList(long forumId, int pageNum, int pageSize, long currentUserId) throws Exception;
	
	public ResultValue getUserThreadList(long userId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getUserFavoriteThreadList(long userId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getGlobalEliteThreadList(int pageNum, int pageSize) throws Exception;
	
	public ResultValue getForumEliteThreadList(long userId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}