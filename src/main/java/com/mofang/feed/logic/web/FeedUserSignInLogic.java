package com.mofang.feed.logic.web;

import com.mofang.feed.global.ResultValue;

public interface FeedUserSignInLogic {

	public ResultValue sign(long userId) throws Exception;
	
	public ResultValue isSignIned(long userId) throws Exception;
}
