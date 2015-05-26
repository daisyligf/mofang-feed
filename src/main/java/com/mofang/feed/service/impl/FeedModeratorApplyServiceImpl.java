package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.ModeratorApplyCondition;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.mysql.FeedModeratorApplyDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedModeratorApplyDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.service.FeedModeratorApplyService;
import com.mofang.feed.util.MysqlPageNumber;

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
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	
	private FeedModeratorApplyServiceImpl()
	{}
	
	public static FeedModeratorApplyServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public ModeratorApplyCondition checkCondition(long userId, long forumId, boolean isAudit) throws Exception
	{
		ModeratorApplyCondition condition = new ModeratorApplyCondition();
		try
		{
			///关注本版块不得少于15天
			int followDays = HttpComponent.getFollowForumDays(userId, forumId);
			boolean followForumIsOK = followDays >= GlobalConfig.MODERATOR_APPLY_FOLLOWFORUMDAYS;
			
			///一个月内在本版块累计发帖不少于10贴
			long startTime = System.currentTimeMillis() -  (30L * 86400L * 1000L);
			long endTime = System.currentTimeMillis();
			long threads = threadDao.getUserThreadCount(userId, startTime, endTime);
			boolean threadsIsOK = threads >= GlobalConfig.MODERATOR_APPLY_NEWTHREADS;
			
			///至少有3个贴子被置顶或者加精
			long topEliteThreads = threadDao.getUserTopOrEliteThreadCount(userId);
			boolean topEliteCountIsOK = topEliteThreads >= GlobalConfig.MODERATOR_APPLY_TOPELITECOUNT;
			
			///两次申请间隔不少于10个自然日
			long lastApplyDate = 0L;
			
			///构建申请条件
			boolean timeIntervalIsOK = true;
			if(!isAudit)
			{
				FeedModeratorApply moderatorApplyInfo = applyDao.getLastApply(userId, forumId);
				if(null != moderatorApplyInfo)
				{
					lastApplyDate = moderatorApplyInfo.getCreateTime();
					long interval = System.currentTimeMillis() - lastApplyDate;
					timeIntervalIsOK = interval >= GlobalConfig.MODERATOR_APPLY_TIMEINTERVAL * 86400 * 1000;
				}
			}
			
			condition.setFollowForumIsOK(followForumIsOK);
			condition.setThreadsIsOK(threadsIsOK);
			condition.setTopEliteCountIsOK(topEliteCountIsOK);
			condition.setTimeIntervalIsOK(timeIntervalIsOK);
			return condition;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModeratorApplyServiceImpl.checkCondition throw an error.", e);
			throw e;
		}
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
	public FeedModeratorApply getInfo(int applyId) throws Exception
	{
		try
		{
			return applyDao.getInfo(applyId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModeratorApplyServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedModeratorApply> getList(int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = applyDao.getApplyCount();
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedModeratorApply> list = applyDao.getApplyList(start, end);
			return new Page<FeedModeratorApply>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModeratorApplyServiceImpl.getList throw an error.", e);
			throw e;
		}
	}
}