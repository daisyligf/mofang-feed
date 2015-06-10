package com.mofang.feed.logic.admin;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedAdminUser;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedAdminUserLogic
{
	public ResultValue exists(long userId) throws Exception;
	
	public ResultValue add(FeedAdminUser model, long operatorId) throws Exception;
	
	public ResultValue delete(long userId, long operatorId) throws Exception;
	
	public ResultValue getUserList(int pageNum, int pageSize) throws Exception;
	
	public ResultValue searchByUserId(long userId, int pageNum, int pageSize) throws Exception;
}