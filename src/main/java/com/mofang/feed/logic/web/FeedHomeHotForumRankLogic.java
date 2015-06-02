package com.mofang.feed.logic.web;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeHotForumRank;

public interface FeedHomeHotForumRankLogic {

	public ResultValue edit(List<FeedHomeHotForumRank> modelList, long operatorId) throws Exception;
	
	public ResultValue  getList() throws Exception;
}
