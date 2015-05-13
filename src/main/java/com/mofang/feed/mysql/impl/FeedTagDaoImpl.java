package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedTag;
import com.mofang.feed.mysql.FeedTagDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.NoneOperand;

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
