package com.mofang.feed.job.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.mofang.feed.job.core.TaskEntity;

/**
 * 计划任务管理器
 * @author zhaodx
 *
 */
public class ScheduledManager
{
	private ScheduledExecutorService executor;
	private final static List<TaskEntity> TASKS = new ArrayList<TaskEntity>();
	
	public void add(TaskEntity entity)
	{
		if(null == entity)
			return;
		
		TASKS.add(entity);
	}
	
	public void execute()
	{
		executor = Executors.newScheduledThreadPool(TASKS.size());
		for(TaskEntity entity : TASKS)
		{
			executor.scheduleAtFixedRate(entity.getTask(), entity.getInitialDelay(), entity.getPeriod(), entity.getUnit());
		}
	}
}