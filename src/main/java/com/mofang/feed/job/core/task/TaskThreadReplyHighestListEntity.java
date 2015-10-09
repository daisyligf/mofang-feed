package com.mofang.feed.job.core.task;

import java.util.concurrent.TimeUnit;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.job.core.TaskEntity;
import com.mofang.feed.service.impl.ThreadReplyHighestListServiceImpl;
import com.mofang.feed.util.TimeUtil;

/***
 * 每天凌晨 板块下 生成回复最高的7条帖子
 */
public class TaskThreadReplyHighestListEntity extends TaskEntity {

	private class Task implements Runnable{

		@Override
		public void run() {
			try {
				GlobalObject.INFO_LOG.info("at TaskClearForumTodayThreadsEntity.Task, generate threadReplyHighestList task start...");
				ThreadReplyHighestListServiceImpl.getInstance().generate();
				GlobalObject.INFO_LOG.info("at TaskClearForumTodayThreadsEntity.Task.run, generate threadReplyHighestList task success.");
			} catch (Throwable e) {
				GlobalObject.ERROR_LOG
				.error("at TaskThreadReplyHighestListEntity.Task.run throw an error.",
						e);
			}
		}
	}
	
	public TaskThreadReplyHighestListEntity() {
		super.setTask(new Task());
		if(GlobalConfig.TIME_TASK_DELAY_TIME == 1)
			super.setInitialDelay(10000l);
		else
			super.setInitialDelay(TimeUtil.getInitDelay(24));
		super.setPeriod(24 * 60 * 60 * 1000l);
		super.setUnit(TimeUnit.MILLISECONDS);
	}
	
}
