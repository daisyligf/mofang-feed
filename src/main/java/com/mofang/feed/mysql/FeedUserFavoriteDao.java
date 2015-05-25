package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedUserFavorite;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedUserFavoriteDao
{
	public boolean exists(long userId, long threadId) throws Exception;
	
	public void add(FeedUserFavorite model) throws Exception;
	
	public void delete(long userId, long threadId) throws Exception;
	
	public void deleteByThreadId(long threadId) throws Exception;
	
	public void deleteByUserId(long userId) throws Exception;
	
	public List<FeedUserFavorite> getList(Operand operand) throws Exception;
	
	public List<FeedUserFavorite> getListByThreadId(long threadId) throws Exception;
	
	/**
	 * 获取用户主题ID列表
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getUserFavoriteThreadList(long userId, int start, int end) throws Exception;
	
	/**
	 * 获取用户主题总数
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public long getUserFavoriteThreadCount(long userId) throws Exception; 
}