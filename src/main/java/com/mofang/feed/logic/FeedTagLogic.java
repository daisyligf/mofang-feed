package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedTag;

public interface FeedTagLogic {

	public ResultValue getList() throws Exception;
	
	public ResultValue delete(List<Integer> tagIdList, long userId) throws Exception;
	
	public ResultValue add(FeedTag model, long userId) throws Exception;
	
}
