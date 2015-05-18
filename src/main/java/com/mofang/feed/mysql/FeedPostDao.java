package com.mofang.feed.mysql;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedReply;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostDao
{
	public long getMaxId() throws Exception;
	
	public void add(FeedPost model) throws Exception;
	
	public void update(FeedPost model) throws Exception;
	
	public void delete(long postId) throws Exception;
	
	public void deleteByThreadId(long threadId) throws Exception;
	
	public FeedPost getInfo(long postId) throws Exception;
	
	public List<FeedPost> getList(Operand operand) throws Exception;
	
	public void updateStatus(long postId, int status) throws Exception;
	
	public void updateStatusByThreadId(long threadId, int status) throws Exception;
	
	public void updateStatusByForumId(long forumId, int status) throws Exception;
	
	public void updateForumIdByThreadId(long threadId, long destForumId) throws Exception;
	
	public void incrComments(long postId) throws Exception;
	
	public void decrComments(long postId) throws Exception;
	
	public void incrRecommends(long postId) throws Exception;
	
	public void decrRecommends(long postId) throws Exception;
	
	/**
	 * 获取楼层列表
	 * @param threadId 主题ID(等于0时不区分主题)
	 * @param status 楼层状态
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<FeedPost> getPostList(long threadId, int status, int start, int end) throws Exception;
	
	/**
	 * 获取楼层总数
	 * @param threadId 主题ID(等于0时不区分主题)
	 * @param status 楼层状态
	 * @return
	 * @throws Exception
	 */
	public long getPostCount(long threadId, int status) throws Exception;
	
	/**
	 * 获取用户楼层ID列表
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getUserPostList(long userId, int start, int end) throws Exception;
	
	/**
	 * 获取用户楼层总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserPostCount(long userId) throws Exception; 
	
	/**
	 * 获取用户回复列表(楼层+评论)
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<FeedReply> getUserReplyList(long userId, int start, int end) throws Exception;
	
	/**
	 * 获取用户回复总数(楼层+评论)
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserReplyCount(long userId) throws Exception;
	
	public Map<Long,ForumCount> getReplyCount(Set<Long> forumIds, long startTime, long endTime) throws Exception;
}