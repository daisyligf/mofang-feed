package com.mofang.feed.service;

public interface FeedUserSignInService {

	public void sign(long userId) throws Exception;
	
	public boolean isSignIned(long userId) throws Exception;
}
