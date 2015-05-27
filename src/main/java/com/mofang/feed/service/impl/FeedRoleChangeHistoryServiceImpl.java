package com.mofang.feed.service.impl;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedRoleChangeHistory;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.model.external.User;
import com.mofang.feed.mysql.FeedRoleChangeHistoryDao;
import com.mofang.feed.mysql.impl.FeedRoleChangeHistoryDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.FeedSysRoleRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.FeedSysRoleRedisImpl;
import com.mofang.feed.service.FeedRoleChangeHistoryService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedRoleChangeHistoryServiceImpl implements FeedRoleChangeHistoryService
{
	private final static FeedRoleChangeHistoryServiceImpl SERVICE = new FeedRoleChangeHistoryServiceImpl();
	private FeedRoleChangeHistoryDao historyDao = FeedRoleChangeHistoryDaoImpl.getInstance();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedSysRoleRedis roleRedis = FeedSysRoleRedisImpl.getInstance();
	
	private FeedRoleChangeHistoryServiceImpl()
	{}
	
	public static FeedRoleChangeHistoryServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(final FeedRoleChangeHistory model) throws Exception
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
					
					if(model.getOriginalRoleId() > 0)
					{
						FeedSysRole roleInfo = roleRedis.getInfo(model.getOriginalRoleId());
						if(null != roleInfo)
							model.setOriginalRoleName(roleInfo.getRoleName());
					}
					
					if(model.getCurrentRoleId() > 0)
					{
						FeedSysRole roleInfo = roleRedis.getInfo(model.getCurrentRoleId());
						if(null != roleInfo)
							model.setCurrentRoleName(roleInfo.getRoleName());
					}
					
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
					GlobalObject.ERROR_LOG.error("at FeedRoleChangeHistoryServiceImpl.add throw an error.", e);
				}
			}
		};
		GlobalObject.ASYN_DAO_EXECUTOR.execute(task);
	}
}