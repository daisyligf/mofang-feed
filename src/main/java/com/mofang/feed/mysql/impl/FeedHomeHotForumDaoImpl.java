package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.mysql.FeedHomeHotForumDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
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
	public void edit(FeedHomeHotForum model) throws Exception {
		long forumId = model.getForumId();
		FeedHomeHotForum oldModel = super.getByPrimaryKey(forumId);
		if(oldModel != null){
			super.deleteByPrimaryKey(forumId);
			if(!super.updateByPrimaryKey(model)){
				super.insert(model);
			}
		}else{
			Operand where = new WhereOperand();
			Operand equal = new EqualOperand("display_order", model.getDisplayOrder());
			where.append(equal);
			super.deleteByWhere(where);
			super.insert(model);
		}
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

}
