package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.mysql.FeedHomeHotForumDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;

public class FeedHomeHotForumDaoImpl extends
		AbstractMysqlSupport<FeedHomeHotForum> implements FeedHomeHotForumDao {

	private static final FeedHomeHotForumDaoImpl DAO = new FeedHomeHotForumDaoImpl();
	
	public static FeedHomeHotForumDaoImpl getInstance(){
		return DAO;
	}
	
	private FeedHomeHotForumDaoImpl(){
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	@Override
	public void add(FeedHomeHotForum model) throws Exception {
		super.insert(model);
	}
	

	@Override
	public List<FeedHomeHotForum> getList() throws Exception {
		Operand none = new Operand() {
			@Override
			protected String toExpression() {
				return " ";
			}
		};
		OrderByEntry entry = new OrderByEntry("display_order", SortType.Asc);
		Operand orderby = new OrderByOperand(entry);
		none.append(orderby);
		return super.getList(none);
	}

	@Override
	public void deleteAll() throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_home_hot_forum_list");
		super.execute(strSql.toString());
	}

	@Override
	public void delete(long forumId) throws Exception {
		super.deleteByPrimaryKey(forumId);
	}

}
