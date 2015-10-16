package com.mofang.feed.service.task;

/***
 * 回复奖励任务
 * @author linjx
 *
 */
public interface FeedThreadRepliesRewardService {

	public void checkAndReword(long threadId) throws Exception;
}
