package com.mofang.feed.redis;

import java.util.List;
import java.util.Set;

import com.mofang.feed.model.FeedForum;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumRedis
{
	/**
	 * 生成主键ID
	 * @return
	 * @throws Exception
	 */
	public long makeUniqueId() throws Exception;
	
	/**
	 * 初始化主键ID
	 * @throws Exception
	 */
	public void initUniqueId(long forumId) throws Exception;
	
	/**
	 * 保存版块信息
	 * @param model 版块实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedForum model) throws Exception;
	
	/**
	 * 删除版块
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public void delete(long forumId) throws Exception;
	
	/**
	 * 获取版块信息
	 * @param forumId 版块ID
	 * @return
	 * @throws Exception
	 */
	public FeedForum getInfo(long forumId) throws Exception;
	
	/**
	 * 递增版块主题数
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void incrThreads(long forumId) throws Exception;
	
	/**
	 * 递减版块主题数
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void decrThreads(long forumId) throws Exception;
	
	/**
	 * 递增版块关注数
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void incrFollows(long forumId) throws Exception;
	
	/**
	 * 递减版块关注数
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void decrFollows(long forumId) throws Exception;
	
	/**
	 * 递增今日发帖数
	 * @param forumId 版块ID
	 * @param threads 主题数
	 * @throws Exception
	 */
	public void incrTodayThreads(long forumId) throws Exception;
	
	/**
	 * 清空今日发帖数
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void clearTodayThreads(long forumId) throws Exception;
	
	/**
	 * 递增今日回复数
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void incrReplies(long forumId) throws Exception;
	
	/**
	 * 更新昨日帖子数
	 * @param forumId 版块ID
	 * @param threads 帖子数
	 * @throws Exception
	 */
	public void updateYestodayThreads(long forumId, int threads) throws Exception;
	
	public void updateIcon(long forumId, String icon) throws Exception;
	
	/**
	 * 将Set转换成实体列表
	 * @param set id集合
	 * @return
	 * @throws Exception
	 */
	public List<FeedForum> convertEntityList(Set<String> set) throws Exception;
}