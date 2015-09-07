package com.mofang.feed.service.impl;

import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;
import com.mofang.feed.model.external.FeedActivityUser;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.service.FeedActivityThreadService;

public class FeedActivityThreadServiceImpl implements FeedActivityThreadService {

	private static final FeedActivityThreadServiceImpl SERVICE = new FeedActivityThreadServiceImpl();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();

	private FeedActivityThreadServiceImpl() {
	}

	public static FeedActivityThreadServiceImpl getInstance() {
		return SERVICE;
	}

	@Override
	public Map<Long, FeedActivityUser> generateRewardUserList(long threadId,
			FeedActivityThreadRewardCondition condition) throws Exception {
		try {
				return postDao.getUserByCondition(threadId, condition);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG
					.error("at FeedActivityThreadServiceImpl.generateRewardUserList throw an error.",
							e);
			throw e;
		}

	}

}
