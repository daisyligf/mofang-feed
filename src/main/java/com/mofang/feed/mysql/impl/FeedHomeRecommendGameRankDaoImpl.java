package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeRecommendGameRank;
import com.mofang.feed.mysql.FeedHomeRecommendGameRankDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;

public class FeedHomeRecommendGameRankDaoImpl extends
		AbstractMysqlSupport<FeedHomeRecommendGameRank> implements
		FeedHomeRecommendGameRankDao {

	private static final FeedHomeRecommendGameRankDaoImpl DAO = new FeedHomeRecommendGameRankDaoImpl();
	
	private FeedHomeRecommendGameRankDaoImpl(){
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	public static FeedHomeRecommendGameRankDaoImpl getInstance(){
		return DAO;
	}
	
	@Override
	public void edit(FeedHomeRecommendGameRank model) throws Exception {
		long forumId = model.getForumId();
		FeedHomeRecommendGameRank oldModel = super.getByPrimaryKey(forumId);
		if(oldModel != null){
			super.deleteByPrimaryKey(forumId);
			if(!super.updateByPrimaryKey(model)){
				super.insert(model);
			}
		}else{
			Operand where = new WhereOperand();
			Operand equal = new EqualOperand("display_order", model.getDisplayOrder());
			where.append(equal);
			if(!super.updateByWhere(model, where)){
				super.insert(model);
			}
		}
	}

	@Override
	public List<FeedHomeRecommendGameRank> getList() throws Exception {
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
