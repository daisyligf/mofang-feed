package com.mofang.feed.service.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.StatForumViewHistory;
import com.mofang.feed.mysql.StatForumViewHistoryDao;
import com.mofang.feed.mysql.impl.StatForumViewHistoryDaoImpl;
import com.mofang.feed.service.StatForumViewHistoryService;

public class StatForumViewHistoryServiceImpl implements
		StatForumViewHistoryService {

	private static final StatForumViewHistoryServiceImpl SERVICE = new StatForumViewHistoryServiceImpl();
	private StatForumViewHistoryDao viewDao = StatForumViewHistoryDaoImpl.getInstance();
	
	private StatForumViewHistoryServiceImpl(){}
	
	public static StatForumViewHistoryServiceImpl getInstance(){
		return SERVICE;
	}
	
	@Override
	public void addUV(long forumId, long userId) throws Exception {
		try {
			StatForumViewHistory viewHistory = new StatForumViewHistory();
			viewHistory.setForumId(forumId);
			viewHistory.setUserId(userId);
			viewHistory.setCreateTime(System.currentTimeMillis());
			viewDao.add(viewHistory);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at StatForumViewHistoryServiceImpl.addUV throw an error.", e);
			throw e;
		}
	}

}
