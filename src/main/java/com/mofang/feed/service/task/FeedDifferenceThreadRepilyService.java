package com.mofang.feed.service.task;

/***
 * 回复32个不同主题任务
 * @author linjx
 *
 */
public interface FeedDifferenceThreadRepilyService {

	public void checkAndcallTask(final long userId, final long threadId) throws Exception;
}
