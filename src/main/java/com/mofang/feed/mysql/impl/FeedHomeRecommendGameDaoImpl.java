package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeRecommendGame;
import com.mofang.feed.mysql.FeedHomeRecommendGameDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;

public class FeedHomeRecommendGameDaoImpl extends
		AbstractMysqlSupport<FeedHomeRecommendGame> implements
		FeedHomeRecommendGameDao {

	private static final FeedHomeRecommendGameDaoImpl DAO = new FeedHomeRecommendGameDaoImpl();
	
	public static FeedHomeRecommendGameDaoImpl getInstance(){
		return DAO;
	}
	
	private FeedHomeRecommendGameDaoImpl(){
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	@Override
	public void edit(FeedHomeRecommendGame model) throws Exception {
		Operand where = new WhereOperand();
		Operand equal = new EqualOperand("display_order", model.getDisplayOrder());
		where.append(equal);
		boolean flag = super.updateByWhere(model, where);
		if(!flag){
			super.insert(model);
		}		
	}

	@Override
	public List<FeedHomeRecommendGame> getList() throws Exception {
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
