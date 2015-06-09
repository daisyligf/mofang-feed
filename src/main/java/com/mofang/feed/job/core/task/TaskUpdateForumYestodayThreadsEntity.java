package com.mofang.feed.job.core.task;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.job.core.TaskEntity;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.util.TimeUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class TaskUpdateForumYestodayThreadsEntity extends TaskEntity
{
	private class Task implements Runnable
	{
		private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
		private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
		
		@Override
		public void run() 
		{
			try 
			{
				///获取版块昨日帖子数
				long startTime = TimeUtil.getYesterdyStartTime();
				long endTime = TimeUtil.getYesterdyEndTime();
				Map<Long, Integer> map = FeedThreadDaoImpl.getInstance().getForumYestodayThreadsMap(startTime, endTime);
				if(null != map)
				{
					int threads = 0;
					for(long forumId : map.keySet())
					{
						threads = map.get(forumId);
						forumDao.updateYestodayThreads(forumId, threads);
						forumRedis.updateYestodayThreads(forumId, threads);
					}
				}
			}
			catch (Exception e) 
			{
				GlobalObject.ERROR_LOG.error("at TaskUpdateForumYestodayThreadsEntity.Task.run throw an error.", e);
			}
		}
	}
	
	public TaskUpdateForumYestodayThreadsEntity()
	{
		super.setTask(new Task());
		super.setInitialDelay(TimeUtil.getInitDelay(24));
		super.setPeriod(24 * 60 * 60 * 1000l);
		super.setUnit(TimeUnit.MILLISECONDS);
	}
}