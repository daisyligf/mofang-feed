package com.mofang.feed.service.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.redis.FeedHomeKeyWordRedis;
import com.mofang.feed.redis.impl.FeedHomeKeyWorldRedisImpl;
import com.mofang.feed.service.FeedHomeKeyWordService;

/***
 * 
 * @author linjx
 *
 */
public class FeedHomeKeyWordServiceImpl implements FeedHomeKeyWordService {

	private static FeedHomeKeyWordServiceImpl SERVICE = new FeedHomeKeyWordServiceImpl();
	private FeedHomeKeyWordRedis keyWordReids = FeedHomeKeyWorldRedisImpl
			.getInstance();

	private FeedHomeKeyWordServiceImpl() {
	}

	public static FeedHomeKeyWordServiceImpl getInstance() {
		return SERVICE;
	}

	@Override
	public void setKeyWord(String word) throws Exception {
		try {
			keyWordReids.setKeyWord(word);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at FeedHomeKeyWordServiceImpl.setKeyWord throw an error.",
					e);
			throw e;
		}
	}

	@Override
	public String getKeyWord() throws Exception {
		try {
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at FeedHomeKeyWordServiceImpl.getKeyWord throw an error.",
					e);
			throw e;
		}
		return null;
	}

}
