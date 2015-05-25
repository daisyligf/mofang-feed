package com.mofang.feed.logic.admin;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedThreadLogic
{
	public ResultValue delete(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue restore(long threadId, long operatorId) throws Exception;
	
	public ResultValue remove(long threadId, long operatorId) throws Exception;
	
	public ResultValue setTop(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue cancelTop(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue setElite(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue cancelElite(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue close(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue open(long threadId, long operatorId, String reason) throws Exception;
	
	public ResultValue getInfo(long threadId) throws Exception;
	
	public ResultValue getThreadList(long forumId, int status, int pageNum, int pageSize) throws Exception;
	
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}