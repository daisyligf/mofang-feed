package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.feed.mysql.FeedHomeHotForumRankDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
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
	public void edit(FeedHomeHotForumRank model) throws Exception {
		long forumId = model.getForumId();
		FeedHomeHotForumRank oldModel = super.getByPrimaryKey(forumId);
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

}
