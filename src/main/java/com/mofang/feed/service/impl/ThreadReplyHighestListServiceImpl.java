package com.mofang.feed.service.impl;

import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.ThreadReplyHighestListRedis;
import com.mofang.feed.redis.impl.ThreadReplyHighestListRedisImpl;
import com.mofang.feed.service.ThreadReplyHighestListService;
import com.mofang.feed.util.TimeUtil;

/***
 * 
 * @author linjx
 *
 */
public class ThreadReplyHighestListServiceImpl implements
		ThreadReplyHighestListService {

	private static final ThreadReplyHighestListServiceImpl SERVICE = new ThreadReplyHighestListServiceImpl();
	private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private ThreadReplyHighestListRedis threadReplyHighestRedis = ThreadReplyHighestListRedisImpl.getInstance();
	
	private ThreadReplyHighestListServiceImpl(){}
	
	public static ThreadReplyHighestListServiceImpl getInstance(){
		return SERVICE;
	}
	
	@Override
	public void generate() throws Exception {
		try {
			long _startTime = System.currentTimeMillis();
			List<Long> forumIdList = forumDao.getForumIdList();
			if(forumIdList == null){
				return;
			}
			long startTime = TimeUtil.getYesterdyStartTime();
			long endTime = TimeUtil.getYesterdyEndTime();
			for(Long forumId : forumIdList){
				List<Long> threadIdList = threadDao.getThreadIdList(forumId, startTime, endTime);
				if(threadIdList == null)
					continue;
				for(Long threadId : threadIdList){
					threadReplyHighestRedis.add(forumId, threadId);
				}
			}
			long _endTime = System.currentTimeMillis();
			GlobalObject.ERROR_LOG.info("at ThreadReplyHighestListServiceImpl.generate, 刷新用时：" + (_endTime - _startTime) + "毫秒");
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at ThreadReplyHighestListServiceImpl.generate throw an error.", e);
			throw e;
		}
	}

	@Override
	public Set<String> getThreadIds(long forumId) throws Exception {
		try {
			return threadReplyHighestRedis.getThreadIdList(forumId);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at ThreadReplyHighestListServiceImpl.getThreadIds throw an error.", e);
			throw e;
		}
	}

}
