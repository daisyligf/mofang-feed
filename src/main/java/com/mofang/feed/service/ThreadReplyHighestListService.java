package com.mofang.feed.service;

import java.util.Set;

public interface ThreadReplyHighestListService {

	public void generate() throws Exception;
	
	public Set<String> getThreadIds(long forumId) throws Exception;
	
}
