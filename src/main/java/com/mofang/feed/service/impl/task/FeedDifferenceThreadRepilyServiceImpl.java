package com.mofang.feed.service.impl.task;

import com.mofang.feed.component.TaskComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.service.task.FeedDifferenceThreadRepilyService;

public class FeedDifferenceThreadRepilyServiceImpl implements
		FeedDifferenceThreadRepilyService {
	
	public static final FeedDifferenceThreadRepilyService SERVICE = new FeedDifferenceThreadRepilyServiceImpl();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private static final int SUBJECT_COUNT = 32;
	
	private FeedDifferenceThreadRepilyServiceImpl(){}
	
	public static FeedDifferenceThreadRepilyService getInstance() {
		return SERVICE;
	}

	@Override
	public void checkAndcallTask(final long userId) throws Exception {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
					int count = postDao.getRepilyCountOfDiffThread(userId);
					if(count == SUBJECT_COUNT) {
						//调用任务接口
						TaskComponent.reply32DiffThread(userId);
					}
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
