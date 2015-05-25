package com.mofang.feed.service.impl;

import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedCommentRedisImpl;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.service.FeedCommentService;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.feed.solr.impl.FeedCommentSolrImpl;
import com.mofang.feed.util.MysqlPageNumber;
import com.mofang.feed.util.RedisPageNumber;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedCommentServiceImpl implements FeedCommentService
{
	private final static FeedCommentServiceImpl SERVICE = new FeedCommentServiceImpl();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	private FeedCommentDao commentDao = FeedCommentDaoImpl.getInstance();
	private FeedCommentSolr commentSolr = FeedCommentSolrImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	
	private FeedCommentServiceImpl()
	{}
	
	public static FeedCommentServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public long add(FeedComment model) throws Exception
	{
		try
		{
			long forumId = model.getForumId();
			long threadId = model.getThreadId();
			long postId = model.getPostId();
			long userId = model.getUserId();
			long postTime = model.getCreateTime();
			long commentId = commentRedis.makeUniqueId();
			model.setCommentId(commentId);
			/******************************redis操作******************************/
			///保存评论信息
			commentRedis.save(model);
			///将评论ID添加到楼层评论列表中
			commentRedis.addPostCommentList(postId, commentId, postTime);
			///楼层评论数 +1
			postRedis.incrComments(postId);
			///主题回复数 +1
			threadRedis.incrReplies(threadId);
			
			///更新主题最后回复用户ID和最后回复时间
			threadRedis.updateLastPost(threadId, userId, postTime);
			///更新版块主题列表中该主题的score
			threadRedis.addForumThreadList(forumId, threadId, postTime);
			
			/******************************数据库操作******************************/
			///保存评论信息
			commentDao.add(model);
			///楼层评论数 +1
			postDao.incrComments(postId);
			///主题回复数 +1
			threadDao.incrReplies(threadId);
			///更新主题最后回复用户ID和最后回复时间
			threadDao.updateLastPost(threadId, userId, postTime);
			
			/******************************Solr操作******************************/
			///保存到solr
			commentSolr.add(model);
			
			return commentId;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(FeedComment model) throws Exception
	{
		try
		{
			long postId = model.getPostId();
			long threadId = model.getThreadId();
			long commentId = model.getCommentId();
			/******************************redis操作******************************/
			///删除评论信息
			commentRedis.delete(commentId);
			///将评论ID 从楼层评论列表中删除
			commentRedis.deleteFromPostCommentList(postId, commentId);
			///楼层评论数 -1
			postRedis.decrComments(postId);
			///主题回复数 -1
			threadRedis.decrReplies(threadId);
			/******************************数据库操作******************************/
			///更新评论信息的状态值为0 (删除)
			commentDao.updateStatus(commentId, CommentStatus.DELETED);
			///楼层评论数 -1
			postDao.decrComments(postId);
			///主题回复数 -1
			threadDao.decrReplies(threadId);
			/******************************Solr操作******************************/
			model.setStatus(CommentStatus.DELETED);
			///更新索引中评论的状态值为0 (删除)
			commentSolr.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public void restore(FeedComment model) throws Exception
	{
		try
		{
			///设置评论状态为正常
			model.setStatus(CommentStatus.NORMAL);
			long postId = model.getPostId();
			long threadId = model.getThreadId();
			long commentId = model.getCommentId();
			long forumId = model.getForumId();
			long postTime = model.getCreateTime();
			/******************************redis操作******************************/
			///保存评论信息
			commentRedis.save(model);
			///将评论ID添加到楼层评论列表中
			commentRedis.addPostCommentList(postId, commentId, postTime);
			///楼层评论数 +1
			postRedis.incrComments(postId);
			///主题回复数 +1
			threadRedis.incrReplies(threadId);
			///更新版块主题列表中该主题的score
			threadRedis.addForumThreadList(forumId, threadId, postTime);
			/******************************数据库操作******************************/
			///更新评论信息状态值为1 (正常)
			commentDao.updateStatus(commentId, CommentStatus.NORMAL);
			///楼层评论数 +1
			postDao.incrComments(postId);
			///主题回复数 +1
			threadDao.incrReplies(threadId);
			/******************************Solr操作******************************/
			///更新索引中楼层的状态值为1 (正常)
			commentSolr.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.restore throw an error.", e);
			throw e;
		}
	}

	@Override
	public void remove(FeedComment model) throws Exception
	{
		try
		{
			long commentId = model.getPostId();
			/******************************数据库操作******************************/
			///将评论信息从数据库中删除
			commentDao.delete(commentId);
			/******************************Solr操作******************************/
			///将评论信息从索引中删除
			commentSolr.deleteById(commentId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.remove throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedComment getInfo(long commentId, DataSource source) throws Exception
	{
		try
		{
			if(source == DataSource.REDIS)
				return commentRedis.getInfo(commentId);
			else if(source == DataSource.MYSQL)
				return commentDao.getInfo(commentId);
			
			return null;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedComment> getCommentList(long postId, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = commentDao.getCommentCount(postId, status);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedComment> list = commentDao.getCommentList(postId, status, start, end);
			return new Page<FeedComment>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.getCommentList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedComment> getPostCommentList(long postId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = commentRedis.getPostCommentCount(postId);
			RedisPageNumber pageNumber = new RedisPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			Set<String> idSet = commentRedis.getPostCommentList(postId, start, end);
			return convertEntityList(total, idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.getPostCommentList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedComment> getUserCommentList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = commentDao.getUserCommentCount(userId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = commentDao.getUserCommentList(userId, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.getUserCommentList throw an error.", e);
			throw e;
		}
	}

	@Override
	public long getUserCommentCount(long userId) throws Exception
	{
		try
		{	
			return commentDao.getUserCommentCount(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.getUserCommentCount throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedComment> search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int size = pageNumber.getEnd();
			return commentSolr.search(forumId, forumName, author, keyword, status, start, size);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentServiceImpl.search throw an error.", e);
			throw e;
		}
	}
	
	private Page<FeedComment> convertEntityList(long total, Set<String> idSet) throws Exception
	{
		if(null == idSet || idSet.size() == 0)
			return null;
		
		List<FeedComment> list = commentRedis.convertEntityList(idSet);
		Page<FeedComment> page = new Page<FeedComment>(total, list);
		return page;
	}
	
	private Page<FeedComment> convertEntityList(long total, List<Long> idList) throws Exception
	{
		if(null == idList || idList.size() == 0)
			return null;
		
		List<FeedComment> list = commentRedis.convertEntityList(idList);
		Page<FeedComment> page = new Page<FeedComment>(total, list);
		return page;
	}
}