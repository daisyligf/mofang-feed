package com.mofang.feed.service;

import com.mofang.feed.model.FeedModuleItem;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModuleItemService
{
	/**
	 * 添加模块主题
	 * @param model 模块主题实体
	 * @throws Exception
	 */
	public void add(FeedModuleItem model) throws Exception;
	
	/**
	 * 编辑模块主题
	 * @param model 模块主题实体
	 * @throws Exception
	 */
	public void edit(FeedModuleItem model) throws Exception;
	
	/**
	 * 删除模块主题
	 * @param model 模块主题实体
	 * @throws Exception
	 */
	public void delete(FeedModuleItem model) throws Exception;
	
	/**
	 * 获取模块主题信息
	 * @param itemId 模块主题ID
	 * @return
	 * @throws Exception
	 */
	public FeedModuleItem getInfo(long itemId) throws Exception;
	
	/**
	 * 更新模块主题排序值
	 * @param model 模块主题实体
	 * @throws Exception
	 */
	public void updateDisplayOrder(FeedModuleItem model) throws Exception;
	
	/**
	 * 获取主题列表
	 * @param moduleId 模块ID(等于0不区分模块)
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedModuleItem> getItemList(long moduleId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取模块主题列表
	 * @param moduleId 模块ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedModuleItem> getModuleThreadList(long moduleId, int pageNum, int pageSize) throws Exception;
}