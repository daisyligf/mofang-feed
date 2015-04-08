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
	 * 将版块添加到推荐吧列表中(web端使用)
	 * @param forumId 版块ID
	 * @param position 推荐位置
	 * @throws Exception
	 */
	public void addRecommendForumList(long forumId, int position) throws Exception;
	
	/**
	 * 将版块从推荐吧列表中删除(web端使用)
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void deleteFromRecommendForumList(long forumId) throws Exception;
	
	/**
	 * 获取推荐吧版块列表(web端使用)
	 * @return
	 * @throws Exception
	 */
	public Set<String> getRecommendForumList() throws Exception;
	
	/**
	 * 清空推荐吧列表(web端使用)
	 */
	public void clearRecommendForumList() throws Exception;
	
	/**
	 * 将版块添加到热吧列表中(web端使用)
	 * @param forumId 版块ID
	 * @param threads 昨日发帖数
	 * @throws Exception
	 */
	public void addHotForumList(long forumId, int threads) throws Exception;
	
	/**
	 * 将版块从热吧列表中删除(web端使用)
	 * @param forumId 版块ID
	 * @throws Exception
	 */
	public void deleteFromHotForumList(long forumId) throws Exception;
	
	/**
	 * 获取热吧版块列表(web端使用)
	 * @param size 记录数
	 * @return
	 * @throws Exception
	 */
	public Set<String> getHotForumList(int size) throws Exception;
	
	/**
	 * 清空热吧列表(web端使用)
	 * @throws Exception
	 */
	public void clearHotForumList() throws Exception;
	
	/**
	 * 将Set转换成实体列表
	 * @param set id集合
	 * @return
	 * @throws Exception
	 */
	public List<FeedForum> convertEntityList(Set<String> set) throws Exception;
}