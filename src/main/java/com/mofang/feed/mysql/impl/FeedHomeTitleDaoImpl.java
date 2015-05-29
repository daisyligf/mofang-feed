package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeTitle;
import com.mofang.feed.mysql.FeedHomeTitleDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
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
	public void edit(FeedHomeTitle model) throws Exception {
		long threadId = model.getThreadId();
		FeedHomeTitle oldModel = super.getByPrimaryKey(threadId);
		if(oldModel != null){
			super.deleteByPrimaryKey(threadId);
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

}
