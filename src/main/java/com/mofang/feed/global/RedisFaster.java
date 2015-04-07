package com.mofang.feed.global;

import java.util.Set;

import redis.clients.jedis.Jedis;

import com.mofang.framework.data.redis.RedisWorker;

/**
 * Redis命令快速执行工具(只提供部分方法)
 * 亲，我只是一个小工具，不要要求太多哦^_^
 * @author zhaodx
 *
 */
public class RedisFaster
{
	public static long hincrBy(final String key, final String field, final long value) throws Exception
	{
		RedisWorker<Long> worker = new RedisWorker<Long>()
		{
			@Override
			public Long execute(Jedis jedis) throws Exception
			{
				return jedis.hincrBy(key, field, value);
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
	
	public static boolean zadd(final String key, final long score, final Object value) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				jedis.zadd(key, score, value.toString());
				return true;
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
	
	public static boolean zrem(final String key, final Object value) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				jedis.zrem(key, value.toString());
				return true;
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
	
	public static Set<String> zrange(final String key, final long start, final long end) throws Exception
	{
		RedisWorker<Set<String>> worker = new RedisWorker<Set<String>>()
		{
			@Override
			public Set<String> execute(Jedis jedis) throws Exception
			{
				return jedis.zrange(key, start, end);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
	
	public static Set<String> zrevrange(final String key, final long start, final long end) throws Exception
	{
		RedisWorker<Set<String>> worker = new RedisWorker<Set<String>>()
		{
			@Override
			public Set<String> execute(Jedis jedis) throws Exception
			{
				return jedis.zrevrange(key, start, end);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
	
	public static long zcard(final String key) throws Exception
	{
		RedisWorker<Long> worker = new RedisWorker<Long>()
		{
			@Override
			public Long execute(Jedis jedis) throws Exception
			{
				return jedis.zcard(key);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
	
	public static boolean sadd(final String key, final Object value) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				jedis.sadd(key, value.toString());
				return true;
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
	
	public static boolean srem(final String key, final Object value) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				jedis.srem(key, value.toString());
				return true;
			}
		};
		return GlobalObject.REDIS_MASTER_EXECUTOR.execute(worker);
	}
	
	public static boolean sismember(final String key, final Object value) throws Exception
	{
		RedisWorker<Boolean> worker = new RedisWorker<Boolean>()
		{
			@Override
			public Boolean execute(Jedis jedis) throws Exception
			{
				return jedis.sismember(key, value.toString());
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
	
	public static Set<String> smembers(final String key) throws Exception
	{
		RedisWorker<Set<String>> worker = new RedisWorker<Set<String>>()
		{
			@Override
			public Set<String> execute(Jedis jedis) throws Exception
			{
				return jedis.smembers(key);
			}
		};
		return GlobalObject.REDIS_SLAVE_EXECUTOR.execute(worker);
	}
}