package com.mofang.feed.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.feed.solr.FeedForumSolr;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.feed.solr.FeedThreadSolr;
import com.mofang.feed.solr.impl.FeedCommentSolrImpl;
import com.mofang.feed.solr.impl.FeedForumSolrImpl;
import com.mofang.feed.solr.impl.FeedPostSolrImpl;
import com.mofang.feed.solr.impl.FeedThreadSolrImpl;
import com.mofang.feed.util.MysqlPageNumber;
import com.mofang.framework.util.ChineseSpellUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumServiceImpl implements FeedForumService
{
	private final static FeedForumServiceImpl SERVICE = new FeedForumServiceImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private FeedCommentDao commentDao = FeedCommentDaoImpl.getInstance();
	private FeedForumSolr forumSolr = FeedForumSolrImpl.getInstance();
	private FeedThreadSolr threadSolr = FeedThreadSolrImpl.getInstance();
	private FeedPostSolr postSolr = FeedPostSolrImpl.getInstance();
	private FeedCommentSolr commentSolr = FeedCommentSolrImpl.getInstance();
	
	private FeedForumServiceImpl()
	{}
	
	public static FeedForumServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public long build(FeedForum model) throws Exception
	{
		try
		{
			long forumId = forumRedis.makeUniqueId();
			String nameSpell = ChineseSpellUtil.GetChineseSpell(model.getName());
			model.setForumId(forumId);
			model.setNameSpell(nameSpell);
			
			/******************************redis操作******************************/
			///保存版块信息
			forumRedis.save(model);
			
			/******************************数据库操作******************************/
			///保存版块信息
			forumDao.add(model);
			
			/******************************Solr操作******************************/
			///保存到solr(顶级版块&工会版块&隐藏版块不进入Solr)
			if(model.getParentId() > 0 && model.getType() != ForumType.GUILD && !model.isHidden())
				forumSolr.add(model);
			
			return forumId;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.build throw an error.", e);
			throw e;
		}
	}

	@Override
	public void edit(FeedForum model) throws Exception
	{
		try
		{
			String nameSpell = ChineseSpellUtil.GetChineseSpell(model.getName());
			model.setNameSpell(nameSpell);
			
			/******************************redis操作******************************/
			///保存版块信息
			forumRedis.save(model);
			
			/******************************数据库操作******************************/
			///保存版块信息
			forumDao.update(model);
			
			/******************************Solr操作******************************/
			///保存到solr(顶级版块&工会版块&隐藏版块不进入Solr)
			if(model.getParentId() > 0 && model.getType() != ForumType.GUILD && !model.isHidden())
				forumSolr.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.edit throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long forumId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///删除版块信息
			forumRedis.delete(forumId);
			///将版块从推荐吧列表中删除
			forumRedis.deleteFromRecommendForumList(forumId);
			///将版块从热吧排行榜中删除
			forumRedis.deleteFromHotForumList(forumId);
			
			///将所属该版块的主题信息(thread_info)删除
			Set<String> threadSet = threadRedis.getForumThreadList(forumId, 0, -1);
			if(null != threadSet && threadSet.size() > 0)
			{
				for(String threadId : threadSet)
					threadRedis.delete(Long.parseLong(threadId));
			}
			///删除版块主题列表
			threadRedis.deleteForumThreadListByForumId(forumId);
			///删除版块置顶主题列表
			threadRedis.deleteForumTopThreadListByForumId(forumId);
			
			/******************************数据库操作******************************/
			///删除版块信息
			forumDao.delete(forumId);
			///将所属该版块的主题的状态值设为0(已删除)
			threadDao.updateStatusByForumId(forumId, ThreadStatus.DELETED);
			///将所属该版块的楼层的状态值设为0(已删除)
			postDao.updateStatusByForumId(forumId, PostStatus.DELETED);
			///将所属该版块的评论的状态值设为0(已删除)
			commentDao.updateStatusByForumId(forumId, CommentStatus.DELETED);
			
			/******************************Solr操作******************************/
			///删除版块索引
			forumSolr.deleteById(forumId);
			///删除版块下所有主题索引
			threadSolr.deleteByForumId(forumId);
			///删除版块下所有楼层索引
			postSolr.deleteByForumId(forumId);
			///删除版块下所有评论索引
			commentSolr.deleteByForumId(forumId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedForum getInfo(long forumId) throws Exception
	{
		try
		{
			return forumRedis.getInfo(forumId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedForum> getForumList(long parentId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = forumDao.getForumCount(parentId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedForum> list = forumDao.getForumList(parentId, start, end);
			return new Page<FeedForum>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedForum> getForumList(Set<Long> forumIds) throws Exception
	{
		try
		{
			List<FeedForum> list = new ArrayList<FeedForum>();
			FeedForum forumInfo = null;
			for(long forumId : forumIds)
			{
				forumInfo = forumRedis.getInfo(forumId);
				if(null == forumInfo)
					continue;
				
				list.add(forumInfo);
			}
			return list;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public void saveRecommendForumList(Set<Long> forumIds) throws Exception
	{
		try
		{
			///清空推荐列表
			forumRedis.clearRecommendForumList();
			///将版块ID添加到推荐列表中
			int position = forumIds.size();
			for(long forumId : forumIds)
				forumRedis.addRecommendForumList(forumId, position);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.saveRecommendForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedForum> getRecommendForumList() throws Exception
	{
		try
		{
			Set<String> idSet = forumRedis.getRecommendForumList();
			return forumRedis.convertEntityList(idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getRecommendForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedForum> getHotForumList(int size) throws Exception
	{
		try
		{
			Set<String> idSet = forumRedis.getHotForumList(size);
			return forumRedis.convertEntityList(idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getHotForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedForum> getHotForumList(Set<Long> forumIds) throws Exception
	{
		try
		{
			List<FeedForum> list = new ArrayList<FeedForum>();
			FeedForum forumInfo = null;
			for(long forumId : forumIds)
			{
				forumInfo = forumRedis.getInfo(forumId);
				if(null == forumInfo)
					continue;
				
				list.add(forumInfo);
			}
			return list;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getHotForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedForum> search(String forumName, int pageNum, int pageSize) throws Exception
	{
		try
		{
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int size = pageNumber.getEnd();
			return forumSolr.search(forumName, start, size);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.search throw an error.", e);
			throw e;
		}
	}
}