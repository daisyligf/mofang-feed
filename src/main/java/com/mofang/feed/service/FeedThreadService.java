package com.mofang.feed.service;

import java.util.List;
import java.util.Set;

import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedThreadService
{
	/**
	 * 添加主题
	 * @param model 主题实体
	 * @return
	 * @throws Exception
	 */
	public long add(FeedThread model) throws Exception;
	
	/**
	 * 编辑主题
	 * @param model 主题实体
	 * @throws Exception
	 */
	public void edit(FeedThread model) throws Exception;
	
	/**
	 * 判断主题是否存在
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public boolean exists(long threadId) throws Exception;
	
	/**
	 * 将主题放入回收站
	 * @param model 主题实体
	 * @param operatorId 操作人ID
	 * @throws Exception
	 */
	public void delete(FeedThread model) throws Exception;
	
	/**
	 * 从回收站中还原主题
	 * @param model 主题实体(从数据库中获取)
	 * @throws Exception
	 */
	public void restore(FeedThread model) throws Exception;
	
	/**
	 * 从回收站中移除主题
	 * @param model 主题实体(从数据库中获取)
	 * @throws Exception
	 */
	public void remove(FeedThread model) throws Exception;
	
	/**
	 * 设置主题置顶
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public void setTop(FeedThread model) throws Exception;
	
	/**
	 * 取消主题置顶
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public void cancelTop(FeedThread model) throws Exception;
	
	/**
	 * 设置/取消精华
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public void setElite(long threadId, boolean isElite) throws Exception;
	
	/**
	 * 设置/取消标红
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public void setMark(long threadId, boolean isMark) throws Exception;
	
	/**
	 * 打开/关闭主题
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public void setClosed(long threadId, boolean isClosed) throws Exception;
	
	/**
	 * 上升下移主题
	 * @param threadId 主题ID
	 * @param updown 上升/下移
	 * @throws Exception
	 */
	public void updown(long threadId, int updown) throws Exception;
	
	/**
	 * 移动主题
	 * @param model 主题实体
	 * @param destForumId 目标版块ID
	 * @throws Exception
	 */
	public void move(FeedThread model, long destForumId) throws Exception;
	
	/**
	 * 主题点赞
	 * @param userId 用户ID
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public long setRecommend(long userId, long threadId) throws Exception;
	
	/**
	 * 取消主题点赞
	 * @param userId 用户ID
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public void cancelRecommend(long userId, long threadId) throws Exception;
	
	/**
	 * 用户是否对指定的主题点赞
	 * @param userId 用户ID
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public boolean existsRecommend(long userId, long threadId) throws Exception;
	
	/**
	 * 获取用户点赞的主题ID集合
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public Set<String> getUserRecommendThreadSet(long userId) throws Exception;
	
	/**
	 * 分享主题
	 * @param threadId 主题ID
	 * @throws Exception
	 */
	public void share(long threadId) throws Exception;
	
	/**
	 * 获取主题信息
	 * @param threadId 主题ID
	 * @param source 数据来源
	 * @return
	 * @throws Exception
	 */
	public FeedThread getInfo(long threadId, DataSource source) throws Exception;
	
	/**
	 * 获取主题信息(包含1楼)
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public FeedThread getFullInfo(long threadId) throws Exception;
	
	/**
	 * 获取主题列表
	 * @param forumId 版块ID(等于0时则不区分版块)
	 * @param status 主题状态
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getThreadList(long forumId, int status, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取版块主题列表
	 * @param forumId 版块ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getForumThreadList(long forumId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取版块置顶主题列表
	 * @param forumId 版块ID
	 * @param pageSize 记录数
	 * @return
	 * @throws Exception
	 */
	public List<FeedThread> getForumTopThreadList(long forumId, int pageSize) throws Exception;
	
	/**
	 * 获取版块精华主题列表
	 * @param forumId 版块ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getForumEliteThreadList(long forumId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取版块视频主题列表
	 * @param forumId 版块ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getForumVideoThreadList(long forumId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取版块热门视频主题列表
	 * @param forumId 版块ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getForumHotVideoThreadList(long forumId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取版块提问主题列表
	 * @param forumId 版块ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getForumQuestionThreadList(long forumId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取版块标红主题列表
	 * @param forumId 版块ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getForumMarkThreadList(long forumId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户主题列表(我的帖子)
	 * @param userId 用户ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getUserThreadList(long userId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户主题总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserThreadCount(long userId) throws Exception;
	
	/**
	 * 获取用户收藏主题列表
	 * @param userId 用户ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getUserFavoriteThreadList(long userId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户精华主题列表
	 * @param userId 用户ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getUserEliteThreadList(long userId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户精华主题总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserEliteThreadCount(long userId) throws Exception;
	
	/**
	 * 获取用户提问主题列表
	 * @param userId 用户ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getUserQuestionThreadList(long userId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取全局精华主题列表(web端)
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getGlobalEliteThreadList(int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取多个版块的精华主题列表
	 * @param forumIds 版块集合
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> getForumEliteThreadList(Set<Long> forumIds, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 搜索主题
	 * @param forumId 版块ID
	 * @param forumName 版块名称
	 * @param author 主题作者
	 * @param keyword 关键字(标题+内容)
	 * @param status 主题状态
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedThread> search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}