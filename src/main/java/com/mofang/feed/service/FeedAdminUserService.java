package com.mofang.feed.service;

import com.mofang.feed.model.FeedAdminUser;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedAdminUserService
{
	public boolean exists(long userId) throws Exception;
	
	public void add(FeedAdminUser model) throws Exception;
	
	public void delete(long userId) throws Exception;
	
	public Page<FeedAdminUser> getAdminList(int pageNum, int pageSize) throws Exception;
}