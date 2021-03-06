package com.mofang.feed.job.core.task;

import java.util.concurrent.TimeUnit;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.job.core.TaskEntity;
import com.mofang.feed.service.impl.HomeRankServiceImpl;
import com.mofang.feed.util.TimeUtil;

/***
 * 每天凌晨刷新 热门游戏 排行榜 任务
 */
public class TaskRefreshHotForumRankEntity extends TaskEntity {
	
	private class Task implements Runnable {
		@Override
		public void run() {
			try {
				GlobalObject.INFO_LOG.info("at TaskRefreshHotForumRankEntity.Task, 刷新热门游戏排行榜开始...");
				//从数据库根据 type=all 获得板块列表
				
				// uv+发帖*10+回复*3+关注*5+赞*2 排序列表
				
				// 获取前5数据 更新排行榜
				
				//排序列表 存入 热门游戏redis结构

				//板块的url列表数据 存入 redis结构
				HomeRankServiceImpl.getInstance().refresh(ForumType.ALL);

			} catch (Throwable e) {
				GlobalObject.ERROR_LOG
						.error("at TaskRefreshHotForumRankEntity.Task.run throw an error.",
								e);
			}
		}

	}
	
	public TaskRefreshHotForumRankEntity() {
		super.setTask(new Task());
		if(GlobalConfig.TIME_TASK_DELAY_TIME == 1)
			super.setInitialDelay(10000l);
		else
			super.setInitialDelay(TimeUtil.getInitDelay(24));
		super.setPeriod(24 * 60 * 60 * 1000l);
		super.setUnit(TimeUnit.MILLISECONDS);
	}
}
