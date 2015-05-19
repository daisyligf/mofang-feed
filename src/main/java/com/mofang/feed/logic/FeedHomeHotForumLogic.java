package com.mofang.feed.logic;

import java.util.List;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedHomeHotForum;

public interface FeedHomeHotForumLogic {

	public ResultValue edit(List<FeedHomeHotForum> modelList) throws Exception;
	
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
