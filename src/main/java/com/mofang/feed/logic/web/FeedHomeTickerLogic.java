package com.mofang.feed.logic.web;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeTicker;

public interface FeedHomeTickerLogic {

	public ResultValue edit(List<FeedHomeTicker> modelList, long operatorId) throws Exception;
	
	public ResultValue getList() throws Exception;
}
