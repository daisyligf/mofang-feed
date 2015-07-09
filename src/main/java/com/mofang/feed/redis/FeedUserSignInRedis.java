package com.mofang.feed.redis;

import com.mofang.feed.model.external.SignInResult;
import com.mofang.feed.model.external.UserSignIn;

public interface FeedUserSignInRedis {

	public void update(long userId, long signInTime, int days) throws Exception;
	
	public UserSignIn getInfo(long userId) throws Exception;
	
	public boolean exists() throws Exception;
	
	public void addSignInfo(long userId, long signInTime) throws Exception;
	
	public void addSignInfoAndExpire(long userId, long signInTime) throws Exception;
	
	public SignInResult getResult(long userId) throws Exception;
}
