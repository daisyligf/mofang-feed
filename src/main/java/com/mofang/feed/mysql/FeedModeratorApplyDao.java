package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedModeratorApply;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModeratorApplyDao
{
	public void add(FeedModeratorApply model) throws Exception;
	
	public void updateStatus(int applyId, int status) throws Exception;
	
	public FeedModeratorApply getInfo(int applyId) throws Exception;
	
	public List<FeedModeratorApply> getApplyList(int start, int end) throws Exception;
	
	public long getApplyCount() throws Exception;
}
