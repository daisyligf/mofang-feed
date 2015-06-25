package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeTitle;
import com.mofang.feed.mysql.FeedHomeTitleDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;

public class FeedHomeTitleDaoImpl extends AbstractMysqlSupport<FeedHomeTitle>
		implements FeedHomeTitleDao {
	
	private final static FeedHomeTitleDaoImpl DAO = new FeedHomeTitleDaoImpl();

	public FeedHomeTitleDaoImpl() {
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	public static FeedHomeTitleDaoImpl getInstance(){
		return DAO;
	}
	
	@Override
	public void add(FeedHomeTitle model) throws Exception {
		super.insert(model);
	}

	@Override
	public List<FeedHomeTitle> getList() throws Exception {
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
	public void delete(long threadId) throws Exception {
		super.deleteByPrimaryKey(threadId);
	}

	@Override
	public void deleteAll() throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_home_title");
		super.execute(strSql.toString());		
	}
	
	

}
