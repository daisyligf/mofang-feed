package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedComment;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedCommentDao
{
	public long getMaxId() throws Exception;
	
	public void add(FeedComment model) throws Exception;
	
	public void update(FeedComment model) throws Exception;
	
	public void delete(long commentId) throws Exception;
	
	public void deleteByThreadId(long threadId) throws Exception;
	
	public void deleteByPostId(long postId) throws Exception;
	
	public FeedComment getInfo(long commentId) throws Exception;
	
	public List<FeedComment> getList(Operand operand) throws Exception;
	
	public void updateStatus(long commentId, int status) throws Exception;
	
	public void updateStatusByThreadId(long threadId, int status) throws Exception;
	
	public void updateStatusByPostId(long postId, int status) throws Exception;
	
	public void updateStatusByForumId(long forumId, int status) throws Exception;
	
	public void updateForumIdByThreadId(long threadId, long destForumId) throws Exception;
	
	/**
	 * 获取评论列表
	 * @param postId 楼层ID(等于0时不区分楼层)
	 * @param status 评论状态
	 * @param pageNum 起始记录
	 * @param pageSize 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<FeedComment> getCommentList(long postId, int status, int start, int end) throws Exception;
	
	/**
	 * 获取评论总数
	 * @param postId 楼层ID(等于0时不区分楼层)
	 * @param status 评论状态
	 * @return
	 * @throws Exception
	 */
	public long getCommentCount(long postId, int status) throws Exception;
	
	/**
	 * 获取用户评论ID列表
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getUserCommentList(long userId, int start, int end) throws Exception;
	
	/**
	 * 获取用户评论总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserCommentCount(long userId) throws Exception; 
}