package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedTag;

public interface FeedTagDao {

	public int getMaxId() throws Exception;
	
	public List<FeedTag> getList() throws Exception;
	
	public void delete(List<Integer> tagIdList) throws Exception;
	
	public void add(FeedTag tag) throws Exception;
}
