package com.mofang.feed.component;

import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.external.User;
import com.mofang.feed.redis.UserRedis;
import com.mofang.feed.redis.impl.UserRedisImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class UserComponent
{
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public static User getInfo(long userId)
	{
		try
		{
			///先从redis中获取, 如果redis中没有，则调用接口获取
			UserRedis userRedis = UserRedisImpl.getInstance();
			User user = userRedis.getInfo(userId);
			if(null != user)
				return user;
			
			user = HttpComponent.getUserInfo(userId);
			if(null == user)
				return null;
			
			userRedis.save(user);
			return user;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at UserComponent.getInfo throw an error.", e);
			return null;
		}
	}
	
	public static Map<Long, User> getInfoByIds(Set<Long> userIds)
	{
		try
		{
			UserRedis userRedis = UserRedisImpl.getInstance();
			Map<Long, User> map = HttpComponent.getUserInfoByIds(userIds);
			if(null == map || map.size() == 0)
				return null;
			
			User user = null;
			for(Long userId : map.keySet())
			{
				user = map.get(userId);
				userRedis.save(user);
			}
			return map;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at UserComponent.getInfoByIds throw an error.", e);
			return null;
		}
	}
	
	public static User getInfoFromCache(long userId)
	{
		try
		{
			UserRedis userRedis = UserRedisImpl.getInstance();
			return userRedis.getInfo(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at UserComponent.getInfoFromCache throw an error.", e);
			return null;
		}
	}
}