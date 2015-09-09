package com.mofang.feed.service;

import java.util.Map;
import java.util.Set;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.external.OperatorHistoryInfo;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedOperateHistoryService
{
	public void add(FeedOperateHistory model) throws Exception;
	
	public Map<Long, OperatorHistoryInfo> getMap(Set<Long> sourceIds, int privilegeType) throws Exception;
}