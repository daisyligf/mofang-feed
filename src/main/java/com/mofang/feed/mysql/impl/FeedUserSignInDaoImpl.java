package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedUserSignIn;
import com.mofang.feed.mysql.FeedUserSignInDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;

public class FeedUserSignInDaoImpl extends AbstractMysqlSupport<FeedUserSignIn>
		implements FeedUserSignInDao {

	private final static FeedUserSignInDaoImpl DAO = new FeedUserSignInDaoImpl();
	
	private FeedUserSignInDaoImpl() {
		try {
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		} catch (Exception e) {
		}
	}
	
	public static FeedUserSignInDaoImpl getInstance() {
		return DAO;
	}
	
	@Override
	public void add(FeedUserSignIn model) throws Exception {
		super.insert(model);
	}

}
