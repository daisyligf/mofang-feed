package com.mofang.feed.component;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.TaskEvent;
import com.mofang.feed.model.external.Task;

/**
 * 
 * @author zhaodx
 *
 */
public class TaskComponent
{
	/**
	 * 发布主题
	 * @param userId 用户ID
	 */
	public static void addThread(long userId)
	{
		try
		{
			Task task = new Task(userId, TaskEvent.ADD_THREAD);
			exec(task);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at TaskComponent.addThread throw an error.", e);
		}
	}
	
	/**
	 * 评论
	 * @param userId 用户ID
	 */
	public static void reply(long userId)
	{
		try
		{
			Task task = new Task(userId, TaskEvent.REPLY);
			exec(task);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at TaskComponent.reply throw an error.", e);
		}
	}
	
	/**
	 * 主题点赞
	 * @param userId 用户ID
	 */
	public static void recommendThread(long userId)
	{
		try
		{
			Task task = new Task(userId, TaskEvent.RECOMMEND_THREAD);
			exec(task);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at TaskComponent.recommendThread throw an error.", e);
		}
	}
	
	/**
	 * 收集点赞
	 * @param userId 用户ID
	 */
	public static void collectRecommends(long userId)
	{
		try
		{
			Task task = new Task(userId, TaskEvent.COLLECT_RECOMMENDS);
			exec(task);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at TaskComponent.collectRecommends throw an error.", e);
		}
	}
	
	/**
	 * 主题被设置为精华
	 * @param userId 用户ID
	 */
	public static void eliteThread(long userId)
	{
		try
		{
			Task task = new Task(userId, TaskEvent.ELITE_THREAD);
			exec(task);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at TaskComponent.eliteThread throw an error.", e);
		}
	}
	
	/**
	 * 执行任务
	 * @param task 任务实体
	 */
	private static void exec(final Task task)
	{
		Runnable taskExec = new Runnable()
		{
			@Override
			public void run() 
			{
				HttpComponent.execTask(task);
			}
		};
		GlobalObject.ASYN_HTTP_EXECUTOR.execute(taskExec);
	}
}