package com.mofang.feed.mysql;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedThreadDao
{
	public long getMaxId() throws Exception;
	
	public void add(FeedThread model) throws Exception;
	
	public void update(FeedThread model) throws Exception;
	
	public void delete(long threadId) throws Exception;
	
	public FeedThread getInfo(long threadId) throws Exception;
	
	public List<FeedThread> getList(Operand operand) throws Exception;
	
	public void updateStatus(long threadId, int status) throws Exception;
	
	public void updateStatusByForumId(long forumId, int status) throws Exception;
	
	public void updateLastPost(long threadId, long lastPostUid, long lastPostTime) throws Exception;
	
	public void updateTop(long threadId, boolean isTop, Date topTime) throws Exception;
	
	public void updateElite(long threadId, boolean isElite) throws Exception;
	
	public void updateMark(long threadId, boolean isMark) throws Exception;
	
	public void updateClosed(long threadId, boolean isClosed) throws Exception;
	
	public void updateVideo(long threadId, boolean isVideo) throws Exception;
	
	public void updateUpDown(long threadId, int updown, long updownTime) throws Exception;
	
	public void updateForumId(long threadId, long destForumId) throws Exception;
	
	public void incrReplies(long threadId) throws Exception;
	
	public void decrReplies(long threadId) throws Exception;
	
	public void incrRecommends(long threadId) throws Exception;
	
	public void decrRecommends(long threadId) throws Exception;
	
	public void incrShareTimes(long threadId) throws Exception;
	
	public void incrPageView(long threadId) throws Exception;
	
	/**
	 * 获取主题列表
	 * @param forumId 版块ID(等于0时则不区分版块)
	 * @param status 主题状态
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<FeedThread> getThreadList(long forumId, int status, int start, int end) throws Exception;
	
	/**
	 * 获取主题总数
	 * @param forumId 版块ID(等于0时则不区分版块)
	 * @param status 主题状态
	 * @return
	 * @throws Exception
	 */
	public long getThreadCount(long forumId, int status) throws Exception;
	
	/**
	 * 获取版块精华主题ID列表
	 * @param forumId 版块ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getForumEliteThreadList(long forumId, int start, int end) throws Exception;
	
	/**
	 * 获取版块精华主题总数
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public long getForumEliteThreadCount(long forumId) throws Exception;
	
	/**
	 * 获取版块视频主题ID列表
	 * @param forumId 版块ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getForumVideoThreadList(long forumId, int start, int end) throws Exception;
	
	/**
	 * 获取版块视频主题总数
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public long getForumVideoThreadCount(long forumId) throws Exception; 
	
	/**
	 * 获取版块热门视频主题ID列表
	 * @param forumId 版块ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getForumHotVideoThreadList(long forumId, int start, int end) throws Exception;
	
	/**
	 * 获取版块热门视频主题总数
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public long getForumHotVideoThreadCount(long forumId) throws Exception; 
	
	/**
	 * 获取版块提问主题ID列表
	 * @param forumId 版块ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getForumQuestionThreadList(long forumId, int start, int end) throws Exception;
	
	/**
	 * 获取版块提问主题总数
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public long getForumQuestionThreadCount(long forumId) throws Exception; 
	
	/**
	 * 获取版块标红主题ID列表
	 * @param forumId 版块ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getForumMarkThreadList(long forumId, int start, int end) throws Exception;
	
	/**
	 * 获取版块标红主题总数
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public long getForumMarkThreadCount(long forumId) throws Exception; 
	
	/**
	 * 获取用户主题ID列表
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getUserThreadList(long userId, int start, int end) throws Exception;
	
	/**
	 * 获取用户主题总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserThreadCount(long userId) throws Exception; 
	
	/**
	 * 获取用户主题总数
	 * @param userId 用户ID
	 * @param startTime 起始时间
	 * @param endTime 截止时间
	 * @return
	 * @throws Exception
	 */
	public long getUserThreadCount(long userId, long startTime, long endTime) throws Exception;
	
	/**
	 * 获取用户精华主题ID列表
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getUserEliteThreadList(long userId, int start, int end) throws Exception;
	
	/**
	 * 获取用户精华主题总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserEliteThreadCount(long userId) throws Exception; 
	
	/**
	 * 获取用户提问主题ID列表
	 * @param userId 用户ID
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getUserQuestionThreadList(long userId, int start, int end) throws Exception;
	
	/**
	 * 获取用户提问主题总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserQuestionThreadCount(long userId) throws Exception; 
	
	/**
	 * 获取多个版块的精华主题列表
	 * @param forumIds 版块ID集合
	 * @param start 起始记录
	 * @param end 截止记录
	 * @return
	 * @throws Exception
	 */
	public List<Long> getForumEliteThreadList(Set<Long> forumIds, int start, int end) throws Exception;
	
	/**
	 * 获取多个版块的精华主题总数
	 * @param forumIds 版块ID集合
	 * @return
	 * @throws Exception
	 */
	public long getForumEliteThreadCount(Set<Long> forumIds) throws Exception;
	
	public long getGlobalEliteThreadCount() throws Exception;
	
	public List<Long> getGlobalEliteThreadList(int start, int end) throws Exception;
	
	public Map<Long,ForumCount> getThreadCount(Set<Long> forumIds, long startTime, long endTime) throws Exception;
	
	public long getUserTopOrEliteThreadCount(long userId) throws Exception;
	
	public List<Long> getThreadIdList(long forumId, long startTime, long endTime) throws Exception;
	
	/**
	 * 根据不同条件获取版块下的主题列表(用于web端)
	 * @param forumId 版块ID
	 * @param tagId 标签ID(等于0时为全部主题)
	 * @param isElite 是否过滤精华帖
	 * @param timeType 排序时间类型
	 * @param start 起始记录数
	 * @param end 截止记录数
	 * @return
	 * @throws Exception
	 */
	public List<Long> getForumThreadListByCondition(long forumId, int tagId, boolean isElite, int timeType, int start, int end) throws Exception;
	
	/**
	 * 根据不同条件获取版块下的主题总数(用于web端) 
	 * @param forumId 版块ID
	 * @param tagId 标签ID(等于0时为全部主题)
	 * @param isElite 是否过滤精华帖
	 * @return
	 * @throws Exception
	 */
	public long getForumThreadCountByCondition(long forumId, int tagId, boolean isElite) throws Exception;
}