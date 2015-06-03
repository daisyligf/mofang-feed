package com.mofang.feed.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.FeedThreadRecommend;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.FeedThreadRecommendDao;
import com.mofang.feed.mysql.FeedUserFavoriteDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadRecommendDaoImpl;
import com.mofang.feed.mysql.impl.FeedUserFavoriteDaoImpl;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.feed.redis.impl.FeedCommentRedisImpl;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.redis.impl.WaterproofWallRedisImpl;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.feed.solr.FeedThreadSolr;
import com.mofang.feed.solr.impl.FeedCommentSolrImpl;
import com.mofang.feed.solr.impl.FeedPostSolrImpl;
import com.mofang.feed.solr.impl.FeedThreadSolrImpl;
import com.mofang.feed.util.MysqlPageNumber;
import com.mofang.feed.util.RedisPageNumber;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadServiceImpl implements FeedThreadService
{
	private final static FeedThreadServiceImpl SERVICE = new FeedThreadServiceImpl();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
	private WaterproofWallRedis waterproofWallRedis = WaterproofWallRedisImpl.getInstance();
	private FeedUserFavoriteDao favoriteDao = FeedUserFavoriteDaoImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	private FeedCommentDao commentDao = FeedCommentDaoImpl.getInstance();
	private FeedThreadSolr threadSolr = FeedThreadSolrImpl.getInstance();
	private FeedPostSolr postSolr = FeedPostSolrImpl.getInstance();
	private FeedCommentSolr commentSolr = FeedCommentSolrImpl.getInstance();
	private FeedThreadRecommendDao recommendDao = FeedThreadRecommendDaoImpl.getInstance();
	
	private FeedThreadServiceImpl()
	{}
	
	public static FeedThreadServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public long add(FeedThread model) throws Exception
	{
		try
		{
			long threadId = threadRedis.makeUniqueId();
			model.setThreadId(threadId);
			long forumId = model.getForumId();
			///获取版块信息
			FeedForum forumInfo = forumRedis.getInfo(forumId);
			boolean forumIsHidden = false;
			if(null != forumInfo)
				forumIsHidden = forumInfo.isHidden();
			
			long userId = model.getUserId();
			long createTime = model.getCreateTime();
			
			/******************************redis操作******************************/
			if(!forumIsHidden)   ///隐藏版块的主题不进入redis和solr(一般是cms的文章)
			{
				///保存主题信息
				threadRedis.save(model);
				///保存到版块对应的帖子列表
				threadRedis.addForumThreadList(forumId, threadId, createTime);
				///更新用户最后发帖时间
				waterproofWallRedis.updateUserLastPostTime(userId, createTime);
				///版块主题数+1
				forumRedis.incrThreads(forumId);
				///版块今日发帖数 +1
				forumRedis.incrTodayThreads(forumId);
			}
			
			/******************************数据库操作******************************/
			///保存主题信息
			threadDao.add(model);
			///版块主题数 +1
			forumDao.incrThreads(forumId);

			/******************************Solr操作******************************/
			if(!forumIsHidden)   ///隐藏版块的主题不进入redis和solr(一般是cms的文章)
			{
				///保存到solr
				threadSolr.add(model);
			}
			
			return threadId;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void edit(FeedThread model) throws Exception
	{
		try
		{
			///获取版块信息
			FeedForum forumInfo = forumRedis.getInfo(model.getForumId());
			boolean forumIsHidden = false;
			if(null != forumInfo)
				forumIsHidden = forumInfo.isHidden();
			
			model.setUpdateTime(System.currentTimeMillis());

			/******************************redis操作******************************/
			if(!forumIsHidden)   ///隐藏版块的主题不进入redis和solr(一般是cms的文章)
			{
				///保存主题信息
				threadRedis.save(model);
			}
			
			/******************************数据库操作******************************/
			///保存主题信息
			threadDao.update(model);
			
			/******************************Solr操作******************************/
			if(!forumIsHidden)   ///隐藏版块的主题不进入redis和solr(一般是cms的文章)
			{
				///保存到solr
				threadSolr.add(model);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.edit throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean exists(long threadId) throws Exception
	{
		try
		{
			return threadRedis.exists(threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.exists throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(FeedThread model) throws Exception
	{
		try
		{
			long threadId = model.getThreadId();
			long forumId = model.getForumId();
			/******************************redis操作******************************/
			///删除主题信息
			threadRedis.delete(threadId);
			///将主题ID从版块主题列表中删除
			threadRedis.deleteFromForumThreadList(forumId, threadId);
			///如果是置顶主题
			if(model.isTop())
			{
				///将主题ID从版块置顶主题列表中删除
				threadRedis.deleteFromForumTopThreadList(forumId, threadId);
			}
			///版块主题数 -1
			forumRedis.decrThreads(forumId);
			
			///以下结构:
			///主题楼层列表、楼层信息、楼层评论列表、评论信息
			///当主题从回收站移除时删除
			
			/******************************数据库操作******************************/
			///更新主题信息的状态值为0 (删除)
			threadDao.updateStatus(threadId, ThreadStatus.DELETED);
			///更新楼层表中该主题的楼层状态值为0 (删除)
			postDao.updateStatusByThreadId(threadId, PostStatus.DELETED);
			///更新评论表中该主题的评论状态值为0 (删除)
			commentDao.updateStatusByThreadId(threadId, CommentStatus.DELETED);
			///版块主题数 -1
			forumDao.decrThreads(forumId);
			
			/******************************solr操作******************************/
			model.setStatus(ThreadStatus.DELETED);
			///更新索引中主题的状态值为0 (删除)
			threadSolr.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public void restore(FeedThread model) throws Exception
	{
		try
		{
			///设置主题状态为正常
			model.setStatus(ThreadStatus.NORMAL);
			long threadId = model.getThreadId();
			long forumId = model.getForumId();
			long createTime = model.getCreateTime();
			long topTime = model.getTopTime();
			///获取版块信息
			FeedForum forumInfo = forumRedis.getInfo(forumId);
			boolean forumIsHidden = false;
			if(null != forumInfo)
				forumIsHidden = forumInfo.isHidden();
			
			/******************************redis操作******************************/
			if(!forumIsHidden)   ///隐藏版块的主题不进入redis和solr(一般是cms的文章)
			{
				///保存主题信息
				threadRedis.save(model);
				///保存到版块对应的帖子列表
				threadRedis.addForumThreadList(forumId, threadId, createTime);
				///如果是置顶帖
				if(model.isTop())
				{
					///保存到版块置顶主题列表
					threadRedis.addForumTopThreadList(forumId, threadId, topTime);
				}
				///版块主题数+1
				forumRedis.incrThreads(forumId);
				///版块今日发帖数 +1
				forumRedis.incrTodayThreads(forumId);
				///ps: 全局精华帖列表是无法恢复的
			}
			
			/******************************数据库操作******************************/
			///更新主题信息的状态值为1 (正常)
			threadDao.updateStatus(threadId, ThreadStatus.NORMAL);
			///更新楼层表中该主题的楼层状态值为1 (正常)
			postDao.updateStatusByThreadId(threadId, PostStatus.NORMAL);
			///更新评论表中该主题的评论状态值为1 (正常)
			commentDao.updateStatusByThreadId(threadId, CommentStatus.NORMAL);
			///版块主题数 +1
			forumDao.incrThreads(forumId);
			
			/******************************Solr操作******************************/
			if(!forumIsHidden)   ///隐藏版块的主题不进入redis和solr(一般是cms的文章)
			{
				///更新索引中主题的状态值为1 (正常)
				threadSolr.add(model);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.restore throw an error.", e);
			throw e;
		}
	}

	@Override
	public void remove(FeedThread model) throws Exception
	{
		try
		{
			long threadId = model.getThreadId();
			/******************************redis操作******************************/
			///获取主题楼层列表
			Set<String> postIds = postRedis.getThreadPostList(threadId, 0, -1);
			if(null != postIds && postIds.size() > 0)
			{
				long postId = 0L;
				for(String strPostId : postIds)
				{
					postId = Long.parseLong(strPostId);
					///删除楼层信息
					postRedis.delete(postId);
					
					///获取楼层评论列表
					Set<String> commentIds = commentRedis.getPostCommentList(postId, 0, -1);
					if(null != commentIds && commentIds.size() > 0)
					{
						long commentId = 0L;
						for(String strCommentId : commentIds)
						{
							commentId = Long.parseLong(strCommentId);
							///删除评论信息
							commentRedis.delete(commentId);
						}
					}
					///删除楼层评论列表
					commentRedis.deletePostCommentListByPostId(postId);
				}
			}
			///删除主题楼层列表
			postRedis.deleteThreadPostListByThreadId(threadId);
			///删除主题楼主楼层列表
			postRedis.deleteHostPostListByThreadId(threadId);
			
			/******************************数据库操作******************************/
			///将主题信息从数据库中删除
			threadDao.delete(threadId);
			///将楼层表中该主题的楼层从数据库中删除
			postDao.deleteByThreadId(threadId);
			///将评论表中该主题的评论从数据库中删除
			commentDao.deleteByThreadId(threadId);
			///将收藏表中用户ID和主题ID的对应关系删除
			favoriteDao.deleteByThreadId(threadId);
			///将主题点赞表中 主题ID和用户ID的对应关系删除
			recommendDao.deleteByThreadId(threadId);
			
			/******************************solr操作******************************/
			///将主题信息从索引中删除
			threadSolr.deleteById(threadId);
			///将该主题的楼层从索引中删除
			postSolr.deleteByThreadId(threadId);
			///将该主题的评论从索引中删除
			commentSolr.deleteByThreadId(threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.remove throw an error.", e);
			throw e;
		}
	}

	@Override
	public void setTop(FeedThread model) throws Exception
	{
		try
		{
			Date topTime = new Date();
			long threadId = model.getThreadId();
			long forumId = model.getForumId();
			/******************************redis操作******************************/
			///更新主题的是否为置顶和置顶时间字段
			threadRedis.updateTop(threadId, true, topTime.getTime());
			///将主题ID从版块主题列表中移除
			threadRedis.deleteFromForumThreadList(forumId, threadId);
			///将主题ID添加到版块置顶主题列表中
			threadRedis.addForumTopThreadList(forumId, threadId, topTime.getTime());
			///如果版块置顶主题列表的总数大于3，则将多余的主题ID从版块置顶主题中删除，并添加到版块主题列表中
			long topThreads = threadRedis.getForumTopThreadCount(forumId);
			if(topThreads > GlobalConfig.FORUM_TOP_THREADS_COUNT)
			{
				Set<String> setThreadIds = threadRedis.getForumTopThreadList(forumId, GlobalConfig.FORUM_TOP_THREADS_COUNT, -1);
				if(null != setThreadIds && setThreadIds.size() > 0)
				{
					FeedThread threadInfo = null;
					for(String strThreadId : setThreadIds)
					{
						threadInfo = threadRedis.getInfo(Long.parseLong(strThreadId));
						if(null == threadInfo)
							continue;
						
						///取消置顶
						cancelTop(threadInfo);
					}
				}
			}
			/******************************数据库操作******************************/
			///更新主题信息的是否为置顶和置顶时间字段
			threadDao.updateTop(threadId, true, topTime);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.setTop throw an error.", e);
			throw e;
		}
	}

	@Override
	public void cancelTop(FeedThread model) throws Exception
	{
		try
		{
			Date topTime = new Date();
			long threadId = model.getThreadId();
			long forumId = model.getForumId();
			long lastPostTime = model.getLastPostTime();
			/******************************redis操作******************************/
			///更新主题的是否为置顶和置顶时间字段
			threadRedis.updateTop(threadId, false, topTime.getTime());
			///将主题ID添加到版块主题列表中
			threadRedis.addForumThreadList(forumId, threadId, lastPostTime);
			///将主题ID从版块置顶主题列表中移除
			threadRedis.deleteFromForumTopThreadList(forumId, threadId);
			/******************************数据库操作******************************/
			///更新主题信息的是否为置顶和置顶时间字段
			threadDao.updateTop(threadId, false, topTime);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.cancelTop throw an error.", e);
			throw e;
		}
	}

	@Override
	public void setElite(long threadId, boolean isElite) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///更新主题的是否为精华字段
			threadRedis.updateElite(threadId, isElite);
			/******************************数据库操作******************************/
			///更新主题的是否为精华字段
			threadDao.updateElite(threadId, isElite);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.setElite throw an error.", e);
			throw e;
		}
	}

	@Override
	public void setClosed(long threadId, boolean isClosed) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///更新主题的是否关闭字段
			threadRedis.updateClosed(threadId, isClosed);
			/******************************数据库操作******************************/
			///更新主题的是否关闭字段
			threadDao.updateClosed(threadId, isClosed);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.setClosed throw an error.", e);
			throw e;
		}
	}

	@Override
	public long setRecommend(long userId, long threadId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///主题点赞数 +1
			long recommends = threadRedis.incrRecommends(threadId);
			///将点赞的用户ID 添加到 主题对应的点赞用户列表中,用于判断用户是否对该贴点赞
			threadRedis.addUserRecommendThreadList(userId, threadId);
			/******************************数据库操作******************************/
			///主题点赞数 +1
			threadDao.incrRecommends(threadId);
			///将点赞的用户和帖子的对应关系添加到数据库
			FeedThreadRecommend recommendInfo = new FeedThreadRecommend();
			recommendInfo.setUserId(userId);
			recommendInfo.setThreadId(threadId);
			recommendDao.add(recommendInfo);
			
			return recommends;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.setRecommend throw an error.", e);
			throw e;
		}
	}
	
	@Override
	public void cancelRecommend(long userId, long threadId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///主题点赞数 -1
			threadRedis.decrRecommends(threadId);
			///将点赞的用户ID 从帖子的点赞用户列表中移除
			threadRedis.deleteFromUserRecommendThreadList(userId, threadId);
			/******************************数据库操作******************************/
			///主题点赞数 -1
			threadDao.decrRecommends(threadId);
			///将点赞的用户ID 和帖子的对应关系从数据库中移除
			recommendDao.delete(userId, threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.cancelRecommend throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean existsRecommend(long userId, long threadId) throws Exception
	{
		try
		{
			return threadRedis.existsRecommendThread(userId, threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.hasRecommend throw an error.", e);
			throw e;
		}
	}

	@Override
	public Set<String> getUserRecommendThreadSet(long userId) throws Exception
	{
		try
		{
			return threadRedis.getUserRecommendThreadSet(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserRecommendThreadSet throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedThread getInfo(long threadId, DataSource source) throws Exception
	{
		try
		{
			if(source == DataSource.REDIS)
				return threadRedis.getInfo(threadId);
			else if(source == DataSource.MYSQL)
				return threadDao.getInfo(threadId);
			
			return null;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedThread getFullInfo(long threadId) throws Exception
	{
		try
		{
			return threadRedis.getFullInfo(threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getFullInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getThreadList(long forumId, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadDao.getThreadCount(forumId, status);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedThread> list = threadDao.getThreadList(forumId, status, start, end);
			return new Page<FeedThread>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getForumThreadList(long forumId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadRedis.getForumThreadCount(forumId);
			RedisPageNumber pageNumber = new RedisPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			Set<String> idSet = threadRedis.getForumThreadList(forumId, start, end);
			return convertEntityList(total, idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getForumThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getForumTopThreadList(long forumId, int pageSize) throws Exception
	{
		try
		{
			long total = threadRedis.getForumTopThreadCount(forumId);
			Set<String> idSet = threadRedis.getForumTopThreadList(forumId, 0, pageSize);
			if(null == idSet || idSet.size() == 0)
				return null;
			
			List<FeedThread> list = threadRedis.convertEntityList(idSet);
			return new Page<FeedThread>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getForumTopThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getForumEliteThreadList(long forumId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadDao.getForumEliteThreadCount(forumId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = threadDao.getForumEliteThreadList(forumId, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getForumEliteThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getUserThreadList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadDao.getUserThreadCount(userId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = threadDao.getUserThreadList(userId, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public long getUserThreadCount(long userId) throws Exception
	{
		try
		{
			return threadDao.getUserThreadCount(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserThreadCount throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getUserFavoriteThreadList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = favoriteDao.getUserFavoriteThreadCount(userId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = favoriteDao.getUserFavoriteThreadList(userId, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserFavoriteThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getUserEliteThreadList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadDao.getUserEliteThreadCount(userId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = threadDao.getUserEliteThreadList(userId, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserEliteThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public long getUserEliteThreadCount(long userId) throws Exception
	{
		try
		{
			return threadDao.getUserEliteThreadCount(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserEliteThreadCount throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> getGlobalEliteThreadList(int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadDao.getGlobalEliteThreadCount();
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = threadDao.getGlobalEliteThreadList(start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserQuestionThreadList throw an error.", e);
			throw e;
		}
	}
	
	private Page<FeedThread> convertEntityList(long total, Set<String> idSet) throws Exception
	{
		if(null == idSet || idSet.size() == 0)
			return null;
		
		List<FeedThread> list = threadRedis.convertEntityList(idSet);
		Page<FeedThread> page = new Page<FeedThread>(total, list);
		return page;
	}
	
	private Page<FeedThread> convertEntityList(long total, List<Long> idList) throws Exception
	{
		if(null == idList || idList.size() == 0)
			return null;
		
		List<FeedThread> list = threadRedis.convertEntityList(idList);
		Page<FeedThread> page = new Page<FeedThread>(total, list);
		return page;
	}

	@Override
	public Page<FeedThread> getForumEliteThreadList(Set<Long> forumIds, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadDao.getForumEliteThreadCount(forumIds);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = threadDao.getForumEliteThreadList(forumIds, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getForumEliteThreadList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedThread> search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int size = pageNumber.getEnd();
			return threadSolr.search(forumId, forumName, author, keyword, status, start, size);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.search throw an error.", e);
			throw e;
		}
	}

	/**
	 * 根据不同条件获取版块下的主题列表(用于web端)
	 * @param forumId 版块ID
	 * @param tagId 标签ID(等于0时为全部主题)
	 * @param isElite 是否过滤精华帖
	 * @param timeType 排序时间类型
	 * @param start 起始记录数
	 * @param end 截止记录数
	 * @return
	 * @throws Exception
	 */
	@Override
	public Page<FeedThread> getForumThreadListByCondition(long forumId, int tagId, boolean isElite, int timeType, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = threadDao.getForumThreadCountByCondition(forumId, tagId, isElite);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = threadDao.getForumThreadListByCondition(forumId, tagId, isElite, timeType, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getForumThreadListByCondition throw an error.", e);
			throw e;
		}
	}
}