package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeNewspaper;

public interface FeedHomeNewspaperLogic {

	public ResultValue update(List<FeedHomeNewspaper> modelList) throws Exception;
	
	public ResultValue getList() throws Exception;
}
