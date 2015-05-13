package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedTag;

public interface FeedTagService {
	
	public List<FeedTag> getList()  throws Exception;
	
	public void delete(List<Integer> tagIdList) throws Exception;
	
	public void add(FeedTag tag) throws Exception;
	
}
