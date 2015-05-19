package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.model.Page;

public interface FeedHomeHotForumService {

	public void edit(List<FeedHomeHotForum> modelList) throws Exception;
	
	public List<FeedHomeHotForum> getList() throws Exception;
	
	public Page<FeedHomeHotForum> getListByLetterGroup(String letterGroup, int pageNum, int pageSize) throws Exception;
}
