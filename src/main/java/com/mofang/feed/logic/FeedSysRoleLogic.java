package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedSysRole;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysRoleLogic
{
	public ResultValue add(FeedSysRole model, long operatorId) throws Exception;
	
	public ResultValue edit(FeedSysRole model, long operatorId) throws Exception;
	
	public ResultValue delete(int roleId, long operatorId) throws Exception;
	
	public ResultValue getInfo(int roleId) throws Exception;
	
	public ResultValue getList() throws Exception;
}