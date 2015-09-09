package com.mofang.feed.mysql.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.external.OperatorHistoryInfo;
import com.mofang.feed.mysql.FeedOperateHistoryDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedOperateHistoryDaoImpl extends AbstractMysqlSupport<FeedOperateHistory> implements FeedOperateHistoryDao
{
	private final static FeedOperateHistoryDaoImpl DAO = new FeedOperateHistoryDaoImpl();
	
	private FeedOperateHistoryDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedOperateHistoryDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedOperateHistory model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public Map<Long, OperatorHistoryInfo> getMap(Set<Long> sourceIds,
			int privilegeType) throws Exception {
		String strSourceIds = "";
		for (long strForumId : sourceIds)
			strSourceIds += strForumId + ",";
		if (strSourceIds.length() > 0)
			strSourceIds = strSourceIds.substring(0, strSourceIds.length() - 1);
		
		StringBuilder strSql = new StringBuilder();
		strSql.append("select source_id, operator_id, operator_name, create_time from feed_operate_history where ");
		strSql.append("source_id in (" + strSourceIds + ") ");
		strSql.append("and privilege_id = " + privilegeType);
		
		ResultData data = super.executeQuery(strSql.toString());
		if(null == data)
			return null;
		
		List<RowData> rows = data.getQueryResult();
		if(null == rows || rows.size() == 0)
			return null;

		Map<Long, OperatorHistoryInfo> resultMap = new HashMap<Long, OperatorHistoryInfo>(rows.size());
		for(int idx = 0; idx < rows.size(); idx ++) {
			RowData row = rows.get(idx);
			OperatorHistoryInfo hi = new OperatorHistoryInfo();
			hi.sourceId = row.getLong(0);
			hi.operatorUserId = row.getLong(1);
			hi.operatorName = row.getString(2);
			hi.operateTime = row.getLong(3);
			resultMap.put(hi.sourceId, hi);
		}
		return resultMap;
	}
	
	
}