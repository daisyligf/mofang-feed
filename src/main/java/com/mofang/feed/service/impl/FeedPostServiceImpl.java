package com.mofang.feed.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.global.common.ReplyType;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedPostAndComment;
import com.mofang.feed.model.FeedPostRecommend;
import com.mofang.feed.model.FeedReply;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.Video;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedPostRecommendDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostRecommendDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedCommentRedisImpl;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.feed.solr.impl.FeedCommentSolrImpl;
import com.mofang.feed.solr.impl.FeedPostSolrImpl;
import com.mofang.feed.util.MysqlPageNumber;
import com.mofang.feed.util.RedisPageNumber;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostServiceImpl implements FeedPostService
{
	private final static FeedPostServiceImpl SERVICE = new FeedPostServiceImpl();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private FeedPostSolr postSolr = FeedPostSolrImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	private FeedCommentDao commentDao = FeedCommentDaoImpl.getInstance();
	private FeedCommentSolr commentSolr = FeedCommentSolrImpl.getInstance();
	private FeedPostRecommendDao recommendDao = FeedPostRecommendDaoImpl.getInstance();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	
	private FeedPostServiceImpl()
	{}
	
	public static FeedPostServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public long add(FeedPost model) throws Exception
	{
		try
		{
			long forumId = model.getForumId();
			long threadId = model.getThreadId();
			long userId = model.getUserId();
			long postTime = model.getCreateTime();
			long postId = postRedis.makeUniqueId();
			int position = postRedis.incrPosition(threadId);
			model.setPostId(postId);
			model.setPosition(position);
			if(model.getVideoId() > 0)
			{
				///获取视频信息
				Video video = HttpComponent.getVideoInfo(model.getVideoId());
				if(null != video)
				{
					model.setThumbnail(video.getThumbnail());
					model.setDuration(video.getDuration());
				}
			}
			
			///获取版块信息
			FeedForum forumInfo = forumRedis.getInfo(model.getForumId());
			boolean forumIsHidden = false;
			if(null != forumInfo)
				forumIsHidden = forumInfo.isHidden();
			
			/******************************redis操作******************************/
			///保存楼层信息
			postRedis.save(model);
			///将楼层ID添加到主题楼层列表中
			postRedis.addThreadPostList(threadId, postId, position);
			///将楼层ID添加到楼主楼层列表(需要判断是否为楼主)
			FeedThread threadInfo = threadRedis.getInfo(threadId);
			if(null != threadInfo)
			{
				long hostId = threadInfo.getUserId();
				if(hostId == userId)
				{
					postRedis.addHostPostList(threadId, postId, position);
				}
				
				///更新主题最后回复用户ID和最后回复时间
				threadRedis.updateLastPost(threadId, userId, postTime);
				///更新版块主题列表中该主题的score(只需要更新非置顶帖的score)
				if(!threadInfo.isTop())
					threadRedis.addForumThreadList(forumId, threadId, postTime);
			}
			///主题回复数 +1
			if(position > 1)
			{
				threadRedis.incrReplies(threadId);
				///版块今日发帖数 +1
				forumRedis.incrTodayThreads(forumId);
			}
			
			/******************************数据库操作******************************/
			///保存楼层信息
			postDao.add(model);
			///主题回复数 +1
			if(position > 1)
			{
				threadDao.incrReplies(threadId);
			}
			///更新主题最后回复用户ID和最后回复时间
			threadDao.updateLastPost(threadId, userId, postTime);
			
			/******************************Solr操作******************************/
			if(!forumIsHidden) ///隐藏版块的楼层不进入solr(一般是cms的文章)
			{
				///保存到solr
				postSolr.add(model);
			}
			
			return postId;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void edit(FeedPost model) throws Exception
	{
		try
		{
			if(model.getVideoId() > 0)
			{
				///获取视频信息
				Video video = HttpComponent.getVideoInfo(model.getVideoId());
				if(null != video)
				{
					model.setThumbnail(video.getThumbnail());
					model.setDuration(video.getDuration());
				}
			}
			///获取版块信息
			FeedForum forumInfo = forumRedis.getInfo(model.getForumId());
			boolean forumIsHidden = false;
			if(null != forumInfo)
				forumIsHidden = forumInfo.isHidden();
			
			/******************************redis操作******************************/
			///保存楼层信息
			postRedis.save(model);
			/******************************数据库操作******************************/
			///保存楼层信息
			postDao.update(model);
			/******************************Solr操作******************************/
			if(!forumIsHidden) ///隐藏版块的楼层不进入solr(一般是cms的文章)
			{
				///保存到solr
				postSolr.add(model);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.edit throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(FeedPost model) throws Exception
	{
		try
		{
			long postId = model.getPostId();
			long threadId = model.getThreadId();
			/******************************redis操作******************************/
			///删除楼层信息
			postRedis.delete(postId);
			///将楼层ID 从主题楼层列表中删除
			postRedis.deleteFromThreadPostList(threadId, postId);
			///将楼层ID 从楼主楼层列表中删除
			postRedis.deleteFromHostPostList(threadId, postId);
			///主题回复数 -1
			threadRedis.decrReplies(threadId);
			/******************************数据库操作******************************/
			///更新楼层信息的状态值为0 (删除)
			postDao.updateStatus(postId, PostStatus.DELETED);
			///更新评论表中该楼层的评论状态值为0 (删除)
			commentDao.updateStatusByPostId(postId, CommentStatus.DELETED);
			///主题回复数 -1
			threadDao.decrReplies(threadId);
			/******************************Solr操作******************************/
			model.setStatus(PostStatus.DELETED);
			///更新索引中楼层的状态值为0 (删除)
			postSolr.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public void restore(FeedPost model) throws Exception
	{
		try
		{
			///设置楼层状态为正常
			model.setStatus(PostStatus.NORMAL);
			long postId = model.getPostId();
			long threadId = model.getThreadId();
			int position = model.getPosition();
			long userId = model.getUserId();
			long forumId = model.getForumId();
			long postTime = model.getCreateTime();
			
			///获取版块信息
			FeedForum forumInfo = forumRedis.getInfo(model.getForumId());
			boolean forumIsHidden = false;
			if(null != forumInfo)
				forumIsHidden = forumInfo.isHidden();
			
			/******************************redis操作******************************/
			///保存楼层信息
			postRedis.save(model);
			///将楼层ID添加到主题楼层列表中
			postRedis.addThreadPostList(threadId, postId, position);
			///将楼层ID添加到楼主楼层列表(需要判断是否为楼主)
			FeedThread threadInfo = threadRedis.getInfo(threadId);
			if(null != threadInfo)
			{
				long hostId = threadInfo.getUserId();
				if(hostId == userId)
				{
					postRedis.addHostPostList(threadId, postId, position);
				}
				
				///主题回复数 +1
				if(position > 1)
					threadRedis.incrReplies(threadId);
				
				///更新版块主题列表中该主题的score(只需要更新非置顶帖的score)
				if(!threadInfo.isTop())
					threadRedis.addForumThreadList(forumId, threadId, postTime);
			}
			
			/******************************数据库操作******************************/
			///更新楼层信息状态值为1 (正常)
			postDao.updateStatus(postId, PostStatus.NORMAL);
			///更新评论表中该楼层的评论状态值为1 (正常)
			commentDao.updateStatusByPostId(postId, PostStatus.NORMAL);
			///主题回复数 +1
			if(position > 1)
				threadDao.incrReplies(threadId);
			/******************************Solr操作******************************/
			if(!forumIsHidden) ///隐藏版块的楼层不进入solr(一般是cms的文章)
			{
				///更新索引中楼层的状态值为1 (正常)
				postSolr.add(model);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.restore throw an error.", e);
			throw e;
		}
	}

	@Override
	public void remove(FeedPost model) throws Exception
	{
		try
		{
			long postId = model.getPostId();
			/******************************redis操作******************************/
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
			/******************************数据库操作******************************/
			///将楼层信息从数据库中删除
			postDao.delete(postId);
			///将评论表中该楼层的评论从数据库中删除
			commentDao.deleteByPostId(postId);
			///将楼层点赞表中 楼层ID和用户ID的对应关系删除
			recommendDao.deleteByPostId(postId);
			/******************************Solr操作******************************/
			///将楼层信息从索引中删除
			postSolr.deleteById(postId);
			///将该楼层的评论从索引中删除
			commentSolr.deleteByPostId(postId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.remove throw an error.", e);
			throw e;
		}
	}

	@Override
	public void setRecommend(long userId, long postId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///楼层点赞数 +1
			postRedis.incrRecommends(postId);
			///将点赞的用户ID 添加到 楼层对应的点赞用户列表中,用于判断用户是否对该楼层点赞
			postRedis.addUserRecommendPostList(userId, postId);
			/******************************数据库操作******************************/
			///楼层点赞数 +1
			postDao.incrRecommends(postId);
			///将点赞的用户和楼层的对应关系添加到数据库
			FeedPostRecommend recommendInfo = new FeedPostRecommend();
			recommendInfo.setUserId(userId);
			recommendInfo.setPostId(postId);
			recommendDao.add(recommendInfo);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.recommend throw an error.", e);
			throw e;
		}
	}

	@Override
	public void cancelRecommend(long userId, long postId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///楼层点赞数 -1
			postRedis.decrRecommends(postId);
			///将点赞的用户ID 从 楼层对应的点赞用户列表中删除
			postRedis.deleteFromUserRecommendPostList(userId, postId);
			/******************************数据库操作******************************/
			///楼层点赞数 -1
			postDao.decrRecommends(postId);
			///将点赞的用户和楼层的对应关系从数据库中删除
			recommendDao.delete(userId, postId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.cancelRecommend throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean existsRecommend(long userId, long postId) throws Exception
	{
		try
		{
			return postRedis.existsUserRecommendPost(userId, postId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.existsRecommend throw an error.", e);
			throw e;
		}
	}

	@Override
	public Set<String> getUserRecommendPostSet(long userId) throws Exception
	{
		try
		{
			return postRedis.getUserRecommendPostSet(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getUserRecommendPostSet throw an error.", e);
			throw e;
		}
	}

	@Override
	public int getRank(long threadId, long postId) throws Exception
	{
		try
		{
			return postRedis.getRank(threadId, postId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadServiceImpl.getRank throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedPost getInfo(long postId, DataSource source) throws Exception
	{
		try
		{
			if(source == DataSource.REDIS)
				return postRedis.getInfo(postId);
			else if(source == DataSource.MYSQL)
				return postDao.getInfo(postId);
			
			return null;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedPost getStartPost(long threadId) throws Exception
	{
		try
		{	
			return postRedis.getStartPost(threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getStartPost throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedPost> getPostList(long threadId, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = postDao.getPostCount(threadId, status);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedPost> list = postDao.getPostList(threadId, status, start, end);
			return new Page<FeedPost>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getPostList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedPost> getThreadPostList(long threadId, int pageNum, int pageSize) throws Exception
	{
		try
		{		
			///记录主题浏览数
			threadDao.incrPageView(threadId);
			threadRedis.incrPageView(threadId);
			
			long total = postRedis.getThreadPostCount(threadId);
			RedisPageNumber pageNumber = new RedisPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			Set<String> idSet = postRedis.getThreadPostList(threadId, start, end);
			return convertEntityList(total, idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getThreadPostList throw an error.", e);
			throw e;
		}
	}
	
	@Override
	public Page<FeedPost> getThreadPostList(long threadId, int pageNum,
			int pageSize, Set<Long> userIds, boolean include) throws Exception 
	{
		try {
			long total = postDao.getPostCount(threadId, 1, userIds, include);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = postDao.getPostList(threadId, 1, start, end, userIds, include);
			return convertEntityList(total, idList);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getThreadPostList by userIds throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedPost> getThreadPostList(long threadId, long postId, int pageSize) throws Exception
	{
		try
		{
			long total = postRedis.getThreadPostCount(threadId);
			Set<String> idSet = postRedis.getThreadPostList(threadId, postId, pageSize);
			return convertEntityList(total, idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getThreadPostList throw an error.", e);
			throw e;
		}
	}
	
	@Override
	public long getThreadPostCount(long threadId) throws Exception
	{
		try
		{
			return postRedis.getThreadPostCount(threadId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getThreadPostCount throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedPost> getHostPostList(long threadId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			///记录主题浏览数
			threadDao.incrPageView(threadId);
			threadRedis.incrPageView(threadId);
			
			long total = postRedis.getHostPostCount(threadId);
			RedisPageNumber pageNumber = new RedisPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			Set<String> idSet = postRedis.getHostPostList(threadId, start, end);
			return convertEntityList(total, idSet);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getHostPostList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedPost> getUserPostList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = postDao.getUserPostCount(userId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<Long> idList = postDao.getUserPostList(userId, start, end);
			return convertEntityList(total, idList);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getUserPostList throw an error.", e);
			throw e;
		}
	}

	@Override
	public long getUserPostCount(long userId) throws Exception
	{
		try
		{	
			return postDao.getUserPostCount(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getUserPostCount throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedPostAndComment> getUserReplyList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{	
			long total = postDao.getUserReplyCount(userId);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedReply> replies = postDao.getUserReplyList(userId, start, end);
			if(null == replies || replies.size() == 0)
				return null;
			
			List<FeedPostAndComment> list = new  ArrayList<FeedPostAndComment>();
			FeedPostAndComment postAndCommentInfo = null;
			FeedThread threadInfo = null;
			FeedForum forumInfo = null;
			for(FeedReply replyInfo : replies)
			{
				if(replyInfo.getType() == ReplyType.THREAD)   ///楼层
				{
					FeedPost postInfo = postRedis.getInfo(replyInfo.getSourceId());
					if(null == postInfo)
						continue;
					
					postAndCommentInfo = new FeedPostAndComment();
					postAndCommentInfo.setThreadId(postInfo.getThreadId());
					threadInfo = threadRedis.getInfo(postInfo.getThreadId());
					if(null != threadInfo)
						postAndCommentInfo.setSubject(threadInfo.getSubjectFilter());
					postAndCommentInfo.setForumId(postInfo.getForumId());
					forumInfo = forumRedis.getInfo(postInfo.getForumId());
					if(null != forumInfo)
						postAndCommentInfo.setForumName(forumInfo.getName());
					postAndCommentInfo.setPostId(postInfo.getPostId());
					int rank = postRedis.getRank(postInfo.getThreadId(), postInfo.getPostId());
					postAndCommentInfo.setPosition(rank);
					postAndCommentInfo.setReplyContent(postInfo.getContentFilter());
					postAndCommentInfo.setReplyPics(postInfo.getPictures());
					postAndCommentInfo.setReplyTime(postInfo.getCreateTime());
				}
				else if(replyInfo.getType() == ReplyType.POST)    ///评论
				{
					FeedComment commentInfo = commentRedis.getInfo(replyInfo.getSourceId());
					if(null == commentInfo)
						continue;
					
					postAndCommentInfo = new FeedPostAndComment();
					postAndCommentInfo.setThreadId(commentInfo.getThreadId());
					threadInfo = threadRedis.getInfo(commentInfo.getThreadId());
					if(null != threadInfo)
						postAndCommentInfo.setSubject(threadInfo.getSubjectFilter());
					postAndCommentInfo.setForumId(commentInfo.getForumId());
					forumInfo = forumRedis.getInfo(commentInfo.getForumId());
					if(null != forumInfo)
						postAndCommentInfo.setForumName(forumInfo.getName());
					postAndCommentInfo.setPostId(commentInfo.getPostId());
					int rank = postRedis.getRank(commentInfo.getThreadId(), commentInfo.getPostId());
					postAndCommentInfo.setPosition(rank);
					postAndCommentInfo.setReplyContent(commentInfo.getContentFilter());
					postAndCommentInfo.setReplyTime(commentInfo.getCreateTime());
				}
				list.add(postAndCommentInfo);
			}
			
			return new Page<FeedPostAndComment>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getUserReplyList throw an error.", e);
			throw e;
		}
	}

	@Override
	public long getUserReplyCount(long userId) throws Exception
	{
		try
		{	
			return postDao.getUserReplyCount(userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.getUserReplyCount throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedPost> search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int size = pageNumber.getEnd();
			return postSolr.search(forumId, forumName, author, keyword, status, start, size);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostServiceImpl.search throw an error.", e);
			throw e;
		}
	}

	private Page<FeedPost> convertEntityList(long total, Set<String> idSet) throws Exception
	{
		if(null == idSet || idSet.size() == 0)
			return null;
		
		List<FeedPost> list = postRedis.convertEntityList(idSet);
		Page<FeedPost> page = new Page<FeedPost>(total, list);
		return page;
	}
	
	private Page<FeedPost> convertEntityList(long total, List<Long> idList) throws Exception
	{
		if(null == idList || idList.size() == 0)
			return null;
		
		List<FeedPost> list = postRedis.convertEntityList(idList);
		Page<FeedPost> page = new Page<FeedPost>(total, list);
		return page;
	}

}