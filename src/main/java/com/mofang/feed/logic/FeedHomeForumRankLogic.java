package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeForumRank;

public interface FeedHomeForumRankLogic {

	public ResultValue update(List<FeedHomeForumRank> modelList) throws Exception;
	
	public ResultValue  getList() throws Exception;
}
