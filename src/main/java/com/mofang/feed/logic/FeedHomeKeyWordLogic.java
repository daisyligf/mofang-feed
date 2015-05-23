package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;

public interface FeedHomeKeyWordLogic {

	public ResultValue setKeyWord(String word, long operatorId) throws Exception;
	
	public ResultValue getKeyWord() throws Exception;
}
