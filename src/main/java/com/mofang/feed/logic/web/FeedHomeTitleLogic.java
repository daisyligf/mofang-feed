package com.mofang.feed.logic.web;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeTitle;

public interface FeedHomeTitleLogic {

	public ResultValue edit(List<FeedHomeTitle> modelList, long operatorId) throws Exception;
	
	public ResultValue getList() throws Exception;
}
