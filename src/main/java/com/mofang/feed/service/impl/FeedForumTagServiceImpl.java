package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForumTag;
import com.mofang.feed.mysql.FeedForumTagDao;
import com.mofang.feed.mysql.impl.FeedForumTagDaoImpl;
import com.mofang.feed.service.FeedForumTagService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumTagServiceImpl implements FeedForumTagService
{
	private final static FeedForumTagServiceImpl SERVICE = new FeedForumTagServiceImpl();
	private FeedForumTagDao forumTagDao = FeedForumTagDaoImpl.getInstance();
	
	private FeedForumTagServiceImpl()
	{}
	
	public static FeedForumTagServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(FeedForumTag model) throws Exception
	{
		try
		{
			if(!forumTagDao.exists(model.getForumId(), model.getTagId()))
				forumTagDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumTagServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void addBatch(long forumId, List<Integer> tagList) throws Exception
	{
		try
		{
			FeedForumTag model = null;
			for(Integer tagId : tagList)
			{
				model = new FeedForumTag();
				model.setForumId(forumId);
				model.setTagId(tagId);
				add(model);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumTagServiceImpl.addBatch throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long forumId, long tagId) throws Exception
	{
		try
		{
			forumTagDao.delete(forumId, tagId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumTagServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public void deleteByTagId(int tagId) throws Exception
	{
		try
		{
			forumTagDao.deleteByTagId(tagId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumTagServiceImpl.deleteByTagId throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<Integer> getTagIdListByForumId(long forumId) throws Exception
	{
		try
		{
			return forumTagDao.getTagIdListByForumId(forumId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumTagServiceImpl.getTagIdListByForumId throw an error.", e);
			throw e;
		}
	}
}