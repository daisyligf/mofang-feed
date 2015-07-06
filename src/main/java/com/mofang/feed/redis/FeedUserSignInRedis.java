package com.mofang.feed.redis;

import com.mofang.feed.model.external.UserSignIn;

public interface FeedUserSignInRedis {

	public void update(long userId, long signInTime, int days) throws Exception;
	
	public UserSignIn getInfo(long userId) throws Exception;
	
}
