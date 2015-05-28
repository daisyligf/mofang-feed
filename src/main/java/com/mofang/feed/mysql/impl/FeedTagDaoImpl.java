package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedTag;
import com.mofang.feed.mysql.FeedTagDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.NoneOperand;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

public class FeedTagDaoImpl extends AbstractMysqlSupport<FeedTag> implements
		FeedTagDao {
	
	private static final FeedTagDaoImpl DAO = new FeedTagDaoImpl();
	
	private FeedTagDaoImpl(){
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	public static FeedTagDaoImpl getInstance(){
		return DAO;
	}

	@Override
	public int getMaxId() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select max(tag_id) from feed_tag ");
		ResultData result = super.executeQuery(strSql.toString());
		if(null == result)
			return 0;
		
		List<RowData> rows = result.getQueryResult();
		if(null == rows || rows.size() == 0)
			return 0;
		
		return rows.get(0).getInteger(0);
	}

	@Override
	public List<FeedTag> getList() throws Exception {
		return super.getList(new NoneOperand());
	}

	@Override
	public void delete(List<Integer> tagIdList) throws Exception {
		for(int tagId : tagIdList){
			super.deleteByPrimaryKey(tagId);
		}
	}

	@Override
	public void add(FeedTag tag) throws Exception {
		super.insert(tag);
	}

}
