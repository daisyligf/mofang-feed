package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.feed.mysql.FeedHomeHotForumRankDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;

public class FeedHomeHotForumRankDaoImpl extends AbstractMysqlSupport<FeedHomeHotForumRank> implements
		FeedHomeHotForumRankDao {

	private static final FeedHomeHotForumRankDaoImpl DAO = new FeedHomeHotForumRankDaoImpl();
	
	private FeedHomeHotForumRankDaoImpl(){
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	public static FeedHomeHotForumRankDaoImpl getInstance(){
		return DAO;
	}
	
	@Override
	public void add(FeedHomeHotForumRank model) throws Exception {
		super.insert(model);
	}

	@Override
	public List<FeedHomeHotForumRank> getList() throws Exception {
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
	public void delete(long forumId) throws Exception {
		super.deleteByPrimaryKey(forumId);
	}

	@Override
	public void deleteAll() throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_home_hot_forum_rank");
		super.execute(strSql.toString());
	}

}
