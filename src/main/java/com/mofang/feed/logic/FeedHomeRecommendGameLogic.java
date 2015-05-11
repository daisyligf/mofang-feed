package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeRecommendGame;

public interface FeedHomeRecommendGameLogic {

	public ResultValue update(List<FeedHomeRecommendGame> modelList) throws Exception;
	
	public ResultValue getList() throws Exception;
}
