package com.mofang.feed.mysql;

import java.util.Map;
import java.util.Set;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.external.OperatorHistoryInfo;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedOperateHistoryDao
{
	public void add(FeedOperateHistory model) throws Exception;
	
	/***
	 * 获取操作历史列表
	 * @param sourceIds      操作对象id
	 * @param privilegeType 权限类别
	 * @return
	 * @throws Exception
	 */
	public Map<Long, OperatorHistoryInfo> getMap(Set<Long> sourceIds, int privilegeType) throws Exception;
}