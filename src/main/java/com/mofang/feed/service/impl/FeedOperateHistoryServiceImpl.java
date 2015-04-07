package com.mofang.feed.service.impl;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.external.User;
import com.mofang.feed.mysql.FeedOperateHistoryDao;
import com.mofang.feed.mysql.impl.FeedOperateHistoryDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.service.FeedOperateHistoryService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedOperateHistoryServiceImpl implements FeedOperateHistoryService
{
	private final static FeedOperateHistoryServiceImpl SERVICE = new FeedOperateHistoryServiceImpl();
	private FeedOperateHistoryDao historyDao = FeedOperateHistoryDaoImpl.getInstance();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	
	private FeedOperateHistoryServiceImpl()
	{}
	
	public static FeedOperateHistoryServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(final FeedOperateHistory model) throws Exception
	{
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					long userId = model.getUserId();
					User userInfo = UserComponent.getInfo(userId);
					if(null != userInfo)
						model.setNickName(userInfo.getNickName());
					
					long forumId = model.getForumId();
					FeedForum forumInfo = forumRedis.getInfo(forumId);
					if(null != forumInfo)
						model.setForumName(forumInfo.getName());
					
					long operatorId = model.getOperatorId();
					userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						model.setOperatorName(userInfo.getNickName());
					
					historyDao.add(model);
				}
				catch(Exception e)
				{
					GlobalObject.ERROR_LOG.error("at FeedOperateHistoryServiceImpl.add throw an error.", e);
				}
			}
		};
		GlobalObject.ASYN_EXECUTOR.execute(task);
	}
}