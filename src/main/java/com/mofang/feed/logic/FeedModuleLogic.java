package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedModule;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModuleLogic
{
	public ResultValue add(FeedModule model, long operatorId) throws Exception;
	
	public ResultValue edit(FeedModule model, long operatorId) throws Exception;
	
	public ResultValue delete(long moduleId, long operatorId) throws Exception;
	
	public ResultValue getInfo(long moduleId) throws Exception;
	
	public ResultValue getList() throws Exception;
}