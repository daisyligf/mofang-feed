package com.mofang.feed.job.core.task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.job.core.TaskEntity;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.util.TimeUtil;

public class TaskClearForumTodayThreadsEntity extends TaskEntity
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
				GlobalObject.INFO_LOG.info("at TaskClearForumTodayThreadsEntity.Task.run, clear forumTodayThreads task start...");
				
				///更新数据库
				forumDao.clearTodayThreads();
				
				///更新redis
				List<FeedForum> list = forumDao.getList(null);
				if(null == list || list.size() == 0)
					return;
				
				for(FeedForum forumInfo : list)
				{
					boolean exists = (null != forumRedis.getInfo(forumInfo.getForumId()));
					if(exists)
						forumRedis.clearTodayThreads(forumInfo.getForumId());
				}
				GlobalObject.INFO_LOG.info("at TaskClearForumTodayThreadsEntity.Task.run, clear forumTodayThreads task success.");
			}
			catch (Throwable e) 
			{
				GlobalObject.ERROR_LOG.error("at TaskClearForumTodayThreadsEntity.Task.run throw an error.", e);
			}
		}
	}
	
	public TaskClearForumTodayThreadsEntity()
	{
		super.setTask(new Task());
		if(GlobalConfig.TIME_TASK_DELAY_TIME == 1)
			super.setInitialDelay(10000l);
		else
			super.setInitialDelay(TimeUtil.getInitDelay(24));
		super.setPeriod(24 * 60 * 60 * 1000l);
		super.setUnit(TimeUnit.MILLISECONDS);
	}
}
