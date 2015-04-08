package com.mofang.feed.redis;

import java.util.List;
import java.util.Set;

import com.mofang.feed.model.FeedModuleItem;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModuleItemRedis
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
	public void initUniqueId(long itemId) throws Exception;
	
	/**
	 * 保存推送主题信息
	 * @param model 推送主题实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedModuleItem model) throws Exception;
	
	/**
	 * 删除推送主题
	 * @param itemId 推送主题ID
	 * @return
	 * @throws Exception
	 */
	public void delete(long itemId) throws Exception;
	
	/**
	 * 获取推送主题信息
	 * @param itemId 推送主题ID
	 * @return
	 * @throws Exception
	 */
	public FeedModuleItem getInfo(long itemId) throws Exception;
	
	/**
	 * 更新推送主题排序值
	 * @param itemId 推送主题ID
	 * @param displayOrder 排序值
	 * @return
	 * @throws Exception
	 */
	public void updateDisplayOrder(long itemId, int displayOrder) throws Exception;
	
	/**
	 * 将主题ID添加到广场模块推送主题列表
	 * @param moduleId 广场模块ID
	 * @param itemId 推送主题ID
	 * @param score 推送主题排序值(display_order + 推送时间)
	 * @return
	 * @throws Exception
	 */
	public void addModuleThreadList(long moduleId, long itemId, long score) throws Exception;
	
	/**
	 * 将主题ID从广场模块推送主题列表中删除
	 * @param moduleId 广场模块ID
	 * @param itemId 推送主题ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromModuleThreadList(long moduleId, long itemId) throws Exception;
	
	/**
	 * 获取广场模块推送主题列表
	 * @param moduleId 广场模块ID
	 * @param start 记录起始位置
	 * @param end 记录截止位置
	 * @return
	 * @throws Exception
	 */
	public Set<String> getModuleThreadList(long moduleId, int start, int end) throws Exception;
	
	/**
	 * 获取广场模块推送主题总数
	 * @param moduleId 广场模块ID
	 * @return
	 * @throws Exception
	 */
	public long getModuleThreadCount(long moduleId) throws Exception;
	
	/**
	 * 删除广场模块推送主题列表
	 * @param moduleId 广场模块ID
	 * @return
	 * @throws Exception
	 */
	public void deleteModuleThreadListByModuleId(long moduleId) throws Exception;
	
	/**
	 * 将Set转换成实体列表
	 * @param set id集合
	 * @return
	 * @throws Exception
	 */
	public List<FeedModuleItem> convertEntityList(Set<String> set) throws Exception;
}