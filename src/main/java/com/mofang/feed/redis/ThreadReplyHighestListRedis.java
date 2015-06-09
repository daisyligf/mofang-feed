package com.mofang.feed.redis;

import java.util.Set;

public interface ThreadReplyHighestListRedis {

	public void add(long forumId, long threadId) throws Exception;
	
	public Set<String> getThreadIdList(long forumId) throws Exception;
	
	public void del(long forumId) throws Exception;
}
