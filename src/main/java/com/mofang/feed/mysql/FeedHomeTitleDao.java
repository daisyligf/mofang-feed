package com.mofang.feed.mysql;

import java.util.List;
import com.mofang.feed.model.FeedHomeTitle;

public interface FeedHomeTitleDao {
	
	public void edit(FeedHomeTitle model) throws Exception;
	
	public List<FeedHomeTitle> getList() throws Exception;
}
