package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeTitle;

public interface FeedHomeTitleLogic {

	public ResultValue update(List<FeedHomeTitle> modelList) throws Exception;
	
	public ResultValue getList() throws Exception;
}
