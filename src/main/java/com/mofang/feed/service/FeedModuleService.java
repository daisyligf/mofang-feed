package com.mofang.feed.service;

import java.util.List;

import com.mofang.feed.model.FeedModule;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModuleService
{
	/**
	 * 添加模块
	 * @param model 模块实体
	 * @throws Exception
	 */
	public void add(FeedModule model) throws Exception;
	
	/**
	 * 编辑模块
	 * @param model 模块实体
	 * @throws Exception
	 */
	public void edit(FeedModule model) throws Exception;
	
	/**
	 * 删除模块
	 * @param moduleId 模块ID
	 * @throws Exception
	 */
	public void delete(long moduleId) throws Exception;
	
	/**
	 * 获取模块信息
	 * @param moduleId 模块ID
	 * @return
	 * @throws Exception
	 */
	public FeedModule getInfo(long moduleId) throws Exception;
	
	/**
	 * 获取模块列表
	 * @return
	 * @throws Exception
	 */
	public List<FeedModule> getList() throws Exception;
}