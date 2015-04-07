package com.mofang.feed.redis;

import java.util.List;
import java.util.Set;

import com.mofang.feed.model.FeedThread;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedThreadRedis
{
	/**
	 * 生成主键ID
	 * @return
	 * @throws Exception
	 */
	public long makeUniqueId() throws Exception;
	
	/**
	 * 保存主题信息
	 * @param model 主题实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedThread model) throws Exception;
	
	/**
	 * 判断主题是否存在
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public boolean exists(long threadId) throws Exception;
	
	/**
	 * 删除主题
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void delete(long threadId) throws Exception;
	
	/**
	 * 获取主题信息
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public FeedThread getInfo(long threadId) throws Exception;
	
	/**
	 * 获取主题信息(包含1楼)
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public FeedThread getFullInfo(long threadId) throws Exception;
	
	/**
	 * 更新主题的最后回复人和最后回复时间
	 * @param threadId 主题ID
	 * @param lastPostUid 最后回复用户ID
	 * @param lastPostTime 最后回复时间
	 * @return
	 * @throws Exception
	 */
	public void updateLastPost(long threadId, long lastPostUid, long lastPostTime) throws Exception;
	
	/**
	 * 设置/取消主题精华
	 * @param threadId 主题ID
	 * @param isElite 是否为精华
	 * @return
	 * @throws Exception
	 */
	public void updateElite(long threadId, boolean isElite) throws Exception;
	
	/**
	 * 设置/取消主题置顶
	 * @param threadId 主题ID
	 * @param isTop 是否置顶
	 * @param topTime  设置/取消置顶时间
	 * @return
	 * @throws Exception
	 */
	public void updateTop(long threadId, boolean isTop, long topTime) throws Exception;
	
	/**
	 * 设置/取消主题关闭
	 * @param threadId 主题ID
	 * @param isClosed 是否关闭
	 * @return
	 * @throws Exception
	 */
	public void updateClosed(long threadId, boolean isClosed) throws Exception;
	
	/**
	 * 设置/取消主题标红
	 * @param threadId 主题ID
	 * @param isMark 是否标红
	 * @return
	 * @throws Exception
	 */
	public void updateMark(long threadId, boolean isMark) throws Exception;
	
	/**
	 * 设置/取消主题是否为视频帖
	 * @param threadId 主题ID
	 * @param isVideo 是否为视频贴
	 * @return
	 * @throws Exception
	 */
	public void updateVideo(long threadId, boolean isVideo) throws Exception;
	
	/**
	 * 设置主题上升下移状态
	 * @param threadId 主题ID
	 * @param updown 上升/下移
	 * @param updownTime 上升下移时间
	 * @throws Exception
	 */
	public void updateUpDown(long threadId, int updown, long updownTime) throws Exception;
	
	/**
	 * 更新主题的版块ID
	 * @param model 主题实体
	 * @param destForumId 目标版块ID
	 * @throws Exception
	 */
	public void updateForumId(FeedThread model, long destForumId) throws Exception;
	
	/**
	 * 递增主题回复数(楼层 + 评论)
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void incrReplies(long threadId) throws Exception;
	
	/**
	 * 递减主题回复数(楼层 + 评论)
	 * @param threadId
	 * @return
	 * @throws Exception
	 */
	public void decrReplies(long threadId) throws Exception;
	
	/**
	 * 递增主题点赞数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public long incrRecommends(long threadId) throws Exception;
	
	/**
	 * 递减主题点赞数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void decrRecommends(long threadId) throws Exception;
	
	/**
	 * 递增主题分享数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void incrShareTimes(long threadId) throws Exception;
	
	/**
	 * 递增主题浏览数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void incrPageView(long threadId) throws Exception;
	
	/**
	 * 将主题ID添加到版块主题列表
	 * @param forumId 版块ID
	 * @param threadId 主题ID
	 * @param score 主题排序值(display_order + 最后回复时间)
	 * @return
	 * @throws Exception
	 */
	public void addForumThreadList(long forumId, long threadId, long score) throws Exception;
	
	/**
	 * 将主题ID从版块主题列表中删除
	 * @param forumId 版块ID
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromForumThreadList(long forumId, long threadId) throws Exception;
	
	/**
	 * 获取版块主题列表
	 * @param forumId 版块ID
	 * @param start 记录起始位置
	 * @param end 记录截止位置
	 * @return
	 * @throws Exception
	 */
	public Set<String> getForumThreadList(long forumId, int start, int end) throws Exception;
	
	/**
	 * 获取版块主题总数
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public long getForumThreadCount(long forumId) throws Exception;
	
	/**
	 * 删除版块主题列表
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public void deleteForumThreadListByForumId(long forumId) throws Exception;
	
	/**
	 * 将主题ID添加到版块置顶列表中
	 * @param forumId 版块ID
	 * @param threadId 主题ID
	 * @param score 主题置顶时间
	 * @return
	 * @throws Exception
	 */
	public void addForumTopThreadList(long forumId, long threadId, long score) throws Exception;
	
	/**
	 * 将主题ID从版块置顶列表中删除
	 * @param forumId 版块ID
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromForumTopThreadList(long forumId, long threadId) throws Exception;
	
	/**
	 * 获取版块置顶列表
	 * @param forumId 版块ID
	 * @param start 记录起始位置
	 * @param end 记录截止位置
	 * @return
	 * @throws Exception
	 */
	public Set<String> getForumTopThreadList(long forumId, int start, int end) throws Exception;
	
	/**
	 * 获取版块置顶总数
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public long getForumTopThreadCount(long forumId) throws Exception;
	
	/**
	 * 删除版块置顶列表
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public void deleteForumTopThreadListByForumId(long forumId) throws Exception;
	
	/**
	 * 将主题ID添加到用户点赞主题列表
	 * @param userId 用户ID
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void addUserRecommendThreadList(long userId, long threadId) throws Exception;
	
	/**
	 * 将主题ID从用户点赞主题列表中删除
	 * @param userId 用户ID
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromUserRecommendThreadList(long userId, long threadId) throws Exception;
	
	/**
	 * 判断用户是否存在用户点赞主题列表中
	 * @param userId 用户ID
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public boolean existsRecommendThread(long userId, long threadId) throws Exception;
	
	/**
	 * 获取用户点赞主题ID集合
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public Set<String> getUserRecommendThreadSet(long userId) throws Exception;
	
	/**
	 * 将Set转换成实体列表
	 * @param idSet id集合
	 * @return
	 * @throws Exception
	 */
	public List<FeedThread> convertEntityList(Set<String> idSet) throws Exception;
	
	/**
	 * 将List转换成实体列表
	 * @param idList id列表
	 * @return
	 * @throws Exception
	 */
	public List<FeedThread> convertEntityList(List<Long> idList) throws Exception;
}