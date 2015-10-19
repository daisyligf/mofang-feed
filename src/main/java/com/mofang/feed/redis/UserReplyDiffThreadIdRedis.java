package com.mofang.feed.redis;

import java.util.Set;

public interface UserReplyDiffThreadIdRedis {

	public void addDiffThreadId(long userId, long threadId) throws Exception;
	
	public void addDiffThreadIdAndExpire(long userId, long threadId) throws Exception;
	
	public Set<String> getDiffThreadIds(long userId) throws Exception;
}
