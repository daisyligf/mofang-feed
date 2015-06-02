package com.mofang.feed.logic.web;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeRecommendGame;

public interface FeedHomeRecommendGameLogic {

	public ResultValue edit(List<FeedHomeRecommendGame> modelList, long operatorId) throws Exception;
	
	public ResultValue getList() throws Exception;
	
	/***
	 * 
	 * @param letterGroup
	 * 				  字母分组
	 * @return
	 * @throws Exception
	 */
	public ResultValue getListByLetterGroup(String letterGroup, int pageNum, int pageSize) throws Exception; 
}
