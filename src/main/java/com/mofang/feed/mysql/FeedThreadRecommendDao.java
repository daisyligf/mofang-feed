package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedThreadRecommend;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedThreadRecommendDao
{
	public void add(FeedThreadRecommend model) throws Exception;
	
	public void delete(long userId, long threadId) throws Exception;
	
	public void deleteByThreadId(long threadId) throws Exception;
	
	public void deleteByUserId(long userId) throws Exception;
	
	public List<FeedThreadRecommend> getList(Operand operand) throws Exception;
}