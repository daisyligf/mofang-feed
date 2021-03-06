package com.mofang.feed.mysql;

import java.util.List;
import com.mofang.feed.model.FeedHomeTitle;

public interface FeedHomeTitleDao {
	
	public void add(FeedHomeTitle model) throws Exception;
	
	public void delete(long threadId) throws Exception;
	
	public void deleteAll() throws Exception;
	
	public List<FeedHomeTitle> getList() throws Exception;
}
