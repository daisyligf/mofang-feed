package com.mofang.feed.service;

import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.ModeratorApplyCondition;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModeratorApplyService
{
	public ModeratorApplyCondition checkCondition(long userId, long forumId, boolean isAudit) throws Exception;
	
	public void add(FeedModeratorApply model) throws Exception;
	
	public void audit(int applyId, int status) throws Exception;
	
	public FeedModeratorApply getInfo(int applyId) throws Exception;
	
	public Page<FeedModeratorApply> getList(int pageNum, int pageSize) throws Exception;
}