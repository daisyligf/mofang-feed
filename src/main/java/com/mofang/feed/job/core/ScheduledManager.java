package com.mofang.feed.job.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.mofang.feed.job.core.TaskEntity;
import com.mofang.feed.job.core.task.TaskClearForumTodayThreadsEntity;
import com.mofang.feed.job.core.task.TaskRefreshHotForumRankEntity;
import com.mofang.feed.job.core.task.TaskRefreshRecommendGameRankEntity;
import com.mofang.feed.job.core.task.TaskThreadReplyHighestListEntity;
import com.mofang.feed.job.core.task.TaskUpdateForumYestodayThreadsEntity;

/**
 * 计划任务管理器
 * 
 * @author zhaodx
 * 
 */
public class ScheduledManager {
	private static final ScheduledManager MANAGER = new ScheduledManager();

	private ScheduledManager() {
	}

	public static ScheduledManager getInstance() {
		return MANAGER;
	}

	private ScheduledExecutorService executor;
	private final static List<TaskEntity> TASKS = new ArrayList<TaskEntity>();

	public void add(TaskEntity entity) {
		if (null == entity)
			return;

		TASKS.add(entity);
	}

	private void registerTask() {
		add(new TaskRefreshHotForumRankEntity());
		add(new TaskRefreshRecommendGameRankEntity());
		add(new TaskThreadReplyHighestListEntity());
		add(new TaskUpdateForumYestodayThreadsEntity());
		add(new TaskClearForumTodayThreadsEntity());
	}

	public void execute() {
		int maxTaskSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
		int taskSize = TASKS.size();
		if (taskSize > maxTaskSize) {
			taskSize = maxTaskSize;
		}
		registerTask();
		executor = Executors.newScheduledThreadPool(taskSize);
		for (TaskEntity entity : TASKS) {
			executor.scheduleAtFixedRate(entity.getTask(),
					entity.getInitialDelay(), entity.getPeriod(),
					entity.getUnit());
		}
	}
}