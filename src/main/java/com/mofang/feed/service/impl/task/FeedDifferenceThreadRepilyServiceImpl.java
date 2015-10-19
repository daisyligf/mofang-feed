package com.mofang.feed.service.impl.task;

import java.util.Set;
import com.mofang.feed.component.TaskComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.redis.UserReplyDiffThreadIdRedis;
import com.mofang.feed.redis.impl.UserReplyDiffThreadIdRedisImpl;
import com.mofang.feed.service.task.FeedDifferenceThreadRepilyService;

public class FeedDifferenceThreadRepilyServiceImpl implements
		FeedDifferenceThreadRepilyService {
	
	public static final FeedDifferenceThreadRepilyService SERVICE = new FeedDifferenceThreadRepilyServiceImpl();
	private static final int SUBJECT_COUNT = 32;
	private static final UserReplyDiffThreadIdRedis replyDiffThreadIdRedis = UserReplyDiffThreadIdRedisImpl.getInstance();
	
	private FeedDifferenceThreadRepilyServiceImpl(){}
	
	public static FeedDifferenceThreadRepilyService getInstance() {
		return SERVICE;
	}

	@Override
	public void checkAndcallTask(final long userId, final long threadId) throws Exception {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					Set<String> strThreadIds = replyDiffThreadIdRedis.getDiffThreadIds(userId);
					if(strThreadIds == null) {
						replyDiffThreadIdRedis.addDiffThreadIdAndExpire(userId, threadId);
						return;
					}
					
					int currentCount = strThreadIds.size();
					if(currentCount == SUBJECT_COUNT) {
						TaskComponent.reply32DiffThread(userId);
						replyDiffThreadIdRedis.addDiffThreadId(userId, threadId);
					}else if(currentCount < SUBJECT_COUNT) 
						replyDiffThreadIdRedis.addDiffThreadId(userId, threadId);
					
				} catch (Exception e) {
					GlobalObject.ERROR_LOG
					.error("at FeedDifferenceThreadRepilyServiceImpl.checkAndReword.task.run throw an error.",
							e);
				}
			}
		};
		GlobalObject.ASYN_DAO_EXECUTOR.execute(task);
	}

}
