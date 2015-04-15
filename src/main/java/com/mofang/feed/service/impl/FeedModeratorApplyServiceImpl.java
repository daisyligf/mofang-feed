package com.mofang.feed.service.impl;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.mysql.FeedModeratorApplyDao;
import com.mofang.feed.mysql.impl.FeedModeratorApplyDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.service.FeedModeratorApplyService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModeratorApplyServiceImpl implements FeedModeratorApplyService
{
	private final static FeedModeratorApplyServiceImpl SERVICE = new FeedModeratorApplyServiceImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedModeratorApplyDao applyDao = FeedModeratorApplyDaoImpl.getInstance();
	
	private FeedModeratorApplyServiceImpl()
	{}
	
	public static FeedModeratorApplyServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(final FeedModeratorApply model) throws Exception
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
					{
						model.setNickName(userInfo.getNickName());
						model.setRegisterTime(userInfo.getRegisterTime());
					}
					
					long forumId = model.getForumId();
					FeedForum forumInfo = forumRedis.getInfo(forumId);
					if(null != forumInfo)
						model.setForumName(forumInfo.getName());
					
					applyDao.add(model);
				}
				catch(Exception e)
				{
					GlobalObject.ERROR_LOG.error("at FeedModeratorApplyServiceImpl.add throw an error.", e);
				}
			}
		};
		GlobalObject.ASYN_EXECUTOR.execute(task);
	}

	@Override
	public void audit(int applyId, int status) throws Exception
	{
		try
		{
			applyDao.updateStatus(applyId, status);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModeratorApplyServiceImpl.updateStatus throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedModeratorApply> getList() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
}