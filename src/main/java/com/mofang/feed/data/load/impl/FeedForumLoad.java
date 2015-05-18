package com.mofang.feed.data.load.impl;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.solr.FeedForumSolr;
import com.mofang.feed.solr.impl.FeedForumSolrImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumLoad implements FeedLoad
{
	private FeedForumDao forumDao  = FeedForumDaoImpl.getInstance();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedForumSolr forumSolr = FeedForumSolrImpl.getInstance();
	private List<FeedForum> solrList = new ArrayList<FeedForum>();

	public void exec()
	{
		List<FeedForum> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("forum data is null or empty.");
			return;
		}
		
		for(FeedForum forumInfo : list)
		{
			handleRedis(forumInfo);
		}
		
		///更新redis自增ID的值
		initUniqueId();
		
		//handleSolr();
		list = null;
		System.gc();
	}
	
	private void handleRedis(FeedForum forumInfo)
	{
		try
		{
			forumRedis.save(forumInfo);
			
			///保存到Solr(非顶级版块和非公会版块才进入solr)
			if(forumInfo.getParentId() > 0)
				solrList.add(forumInfo);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumLoad.handleRedis throw an error.", e);
		}
	}
	
	private void initUniqueId()
	{
		try
		{
			long maxId = forumDao.getMaxId();
			forumRedis.initUniqueId(maxId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumLoad.initUniqueId throw an error.", e);
		}
	}
	
	private void handleSolr()
	{
		try
		{
			if(solrList.size() == 0)
				return;
			
			forumSolr.batchAdd(solrList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumLoad.handleSolr throw an error.", e);
		}
	}
	
	private List<FeedForum> getData()
	{
		try
		{
			return forumDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumLoad.getData throw an error.", e);
			return null;
		}
	}
}