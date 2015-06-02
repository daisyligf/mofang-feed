package com.mofang.feed.logic.web;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeRecommendGameRank;

public interface FeedHomeRecommendGameRankLogic {

	public ResultValue edit(List<FeedHomeRecommendGameRank> modelList ,long operatorId) throws Exception;
	
	public ResultValue getList() throws Exception;
}
