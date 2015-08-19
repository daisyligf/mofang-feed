package com.mofang.feed.mysql.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeRecommendGame;
import com.mofang.feed.mysql.FeedHomeRecommendGameDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

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
	public void add(FeedHomeRecommendGame model) throws Exception {
		super.insert(model);
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

	@Override
	public void delete(long forumId) throws Exception {
		super.deleteByPrimaryKey(forumId);
	}

	@Override
	public void deleteAll() throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("delete from feed_home_recommend_game_list");
		super.execute(strSql.toString());
	}

	@Override
	public Set<Long> getForumIdSet() throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select forum_id from feed_home_recommend_game_list");
		ResultData data = super.executeQuery(strSql.toString());
		if (data == null)
			return null;
		List<RowData> rows = data.getQueryResult();
		if (rows == null || rows.size() == 0)
			return null;
		Set<Long> set = new HashSet<Long>(rows.size());
		for(RowData row : rows) {
			set.add(row.getLong(0));
		}
		return set;
	}

	@Override
	public void updateGiftUrl(long forumId, String giftUrl) throws Exception {
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_home_recommend_game_list set gift_url = '" + giftUrl + "' where forum_id=" + forumId);
		super.execute(strSql.toString());
	}

}
