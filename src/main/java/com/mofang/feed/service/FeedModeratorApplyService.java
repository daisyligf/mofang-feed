package com.mofang.feed.service;

import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModeratorApplyService
{
	public void add(FeedModeratorApply model) throws Exception;
	
	public void audit(int applyId, int status) throws Exception;
	
	public Page<FeedModeratorApply> getList() throws Exception;
}