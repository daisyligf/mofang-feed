package com.mofang.feed.logic.admin;

import com.mofang.feed.global.ResultValue;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModeratorApplyLogic
{
	public ResultValue audit(int applyId, int status, int roleId, long operatorId) throws Exception;
	
	public ResultValue getList(int pageNum, int pageSize) throws Exception;
}