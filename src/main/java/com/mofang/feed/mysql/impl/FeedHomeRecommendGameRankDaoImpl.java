package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeRecommendGameRank;
import com.mofang.feed.mysql.FeedHomeRecommendGameRankDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
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
	public void add(FeedHomeRecommendGameRank model) throws Exception {
		super.insert(model);
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

	public void delete(long forumId) throws Exception {
		super.deleteByPrimaryKey(forumId);
	}

	@Override
	public void deleteAll() throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_home_recommend_game_rank");
		super.execute(strSql.toString());		
	}
	
}
