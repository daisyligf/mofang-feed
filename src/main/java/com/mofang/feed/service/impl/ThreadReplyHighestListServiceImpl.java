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
			long startTime = TimeUtil.getLastSevenDayStartTime();
			long endTime = TimeUtil.getYesterdyEndTime();
			for(Long forumId : forumIdList){
				List<Long> threadIdList = threadDao.getThreadIdList(forumId, startTime, endTime, 7);
				
				if(threadIdList == null) {
					threadIdList = threadDao.getThreadIdList(forumId, 0, 0, 7);
				} else if(threadIdList.size() < 7) {
					int supplementSize = 7 - threadIdList.size();
					List<Long> supplementIdList = threadDao.getThreadIdList(forumId, startTime, 0, supplementSize);
					if(supplementIdList != null) {
						threadIdList.addAll(supplementIdList);
					}
				}
				
				if(threadIdList != null) {
					
					threadReplyHighestRedis.del(forumId);
					for(Long threadId : threadIdList){
						threadReplyHighestRedis.add(forumId, threadId);
					}
				}
			}
			long _endTime = System.currentTimeMillis();
			GlobalObject.INFO_LOG.info("at ThreadReplyHighestListServiceImpl.generate, 生成最高回复7条帖子--用时：" + (_endTime - _startTime) + "毫秒");
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
