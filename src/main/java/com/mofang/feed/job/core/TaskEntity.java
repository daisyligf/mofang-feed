package com.mofang.feed.job.core;

import java.util.concurrent.TimeUnit;

/**
 * 计划任务实体类
 * 
 * @author zhaodx
 * 
 */
public class TaskEntity {
	private Runnable task;
	private long initialDelay;
	private long period;
	private TimeUnit unit;

	public Runnable getTask() {
		return task;
	}

	public void setTask(Runnable task) {
		this.task = task;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
}