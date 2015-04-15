package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModeratorApplyDao
{
	public void add(FeedModeratorApply model) throws Exception;
	
	public void updateStatus(int applyId, int status) throws Exception;
	
	public List<FeedModeratorApply> getApplyList(Operand operand) throws Exception;
	
	public long getApplyCount(Operand operand) throws Exception;
}
