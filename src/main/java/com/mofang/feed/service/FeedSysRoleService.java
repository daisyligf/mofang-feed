package com.mofang.feed.service;

import com.mofang.feed.model.FeedSysRole;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysRoleService
{
	public void add(FeedSysRole model) throws Exception;
	
	public void update(FeedSysRole model) throws Exception;
	
	public void delete(int roleId) throws Exception;
	
	public FeedSysRole getInfo(int roleId) throws Exception;
}