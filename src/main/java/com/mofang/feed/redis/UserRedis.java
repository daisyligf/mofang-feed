package com.mofang.feed.redis;

import com.mofang.feed.model.external.User;

/**
 * 
 * @author zhaodx
 *
 */
public interface UserRedis
{
	public void save(User model) throws Exception;
	
	public User getInfo(long userId) throws Exception;
}