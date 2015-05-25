package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedPostRecommend;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostRecommendDao
{
	public void add(FeedPostRecommend model) throws Exception;
	
	public void delete(long userId, long postId) throws Exception;
	
	public void deleteByPostId(long postId) throws Exception;
	
	public void deleteByUserId(long userId) throws Exception;
	
	public List<FeedPostRecommend> getList(Operand operand) throws Exception;
}