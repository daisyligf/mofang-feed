package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedAdminUser;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedAdminUserDao
{
	public boolean exists(long userId) throws Exception;
	
	public void add(FeedAdminUser model) throws Exception;
	
	public void delete(long userId) throws Exception;
	
	public List<FeedAdminUser> getList(int start, int end) throws Exception;
	
	public long getCount() throws Exception;
}