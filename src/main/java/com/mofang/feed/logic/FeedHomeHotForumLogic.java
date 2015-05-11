package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeHotForum;

public interface FeedHomeHotForumLogic {

	public ResultValue update(List<FeedHomeHotForum> modelList) throws Exception;
	
	public ResultValue getList() throws Exception;
}
