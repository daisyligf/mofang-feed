package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeTicker;

public interface FeedHomeTickerLogic {

	public ResultValue edit(List<FeedHomeTicker> modelList, long userId) throws Exception;
	
	public ResultValue getList() throws Exception;
}
