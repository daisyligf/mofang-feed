package com.mofang.feed.service;

import com.mofang.feed.model.external.SignInResult;

public interface FeedUserSignInService {

	public SignInResult sign(long userId) throws Exception;
	
	public SignInResult getResult(long userId) throws Exception;
	
	public int totalMember() throws Exception;
	
}
