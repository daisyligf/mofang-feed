package com.mofang.feed.service;

import java.util.List;
import com.mofang.feed.model.FeedHomeTitle;

public interface FeedHomeTitleService {

	public void edit(List<FeedHomeTitle> modelList) throws Exception;
	
	public List<FeedHomeTitle> getList() throws Exception;
	
}
