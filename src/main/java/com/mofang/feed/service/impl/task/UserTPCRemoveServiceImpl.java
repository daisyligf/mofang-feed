package com.mofang.feed.service.impl.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.external.Pair;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.UserTPCDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.mysql.impl.UserTPCDaoImpl;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedCommentRedisImpl;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.service.task.UserTPCRemoveService;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.feed.solr.FeedThreadSolr;
import com.mofang.feed.solr.impl.FeedCommentSolrImpl;
import com.mofang.feed.solr.impl.FeedPostSolrImpl;
import com.mofang.feed.solr.impl.FeedThreadSolrImpl;

/***
 * 超鸡巴麻烦
 * @author linjx
 */
public class UserTPCRemoveServiceImpl implements UserTPCRemoveService {
	
	private static final UserTPCRemoveService SERVICE = new UserTPCRemoveServiceImpl();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private FeedCommentDao commentDao = FeedCommentDaoImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	private FeedThreadSolr threadSolr = FeedThreadSolrImpl.getInstance();
	private FeedPostSolr postSolr = FeedPostSolrImpl.getInstance();
	private FeedCommentSolr commentSolr = FeedCommentSolrImpl.getInstance();
	private UserTPCDao userTPCDao = UserTPCDaoImpl.getInstance();
	
	private UserTPCRemoveServiceImpl(){}
	
	public static UserTPCRemoveService getInstance() {
		return SERVICE;
	}

	@Override
	public void delete(final long userId) throws Exception {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				try {
					long startTime = System.currentTimeMillis();
					deleteThread(userId);
					deletePost(userId);
					deleteComment(userId);
					long endTime = System.currentTimeMillis();
					GlobalObject.INFO_LOG.info("at UserTPCRemoveServiceImpl.delete.task. 删除userId:"+ userId +"的数据耗时:" + (endTime - startTime) + "毫秒");
				} catch (Exception e) {
					GlobalObject.ERROR_LOG.error("at UserTPCRemoveServiceImpl.delete.task throw an error.");
				}
				
			}
		};
		GlobalObject.ASYN_DAO_EXECUTOR.execute(run);
	}
	
	private void deleteThread(long userId) throws Exception {
		long startTime = System.currentTimeMillis();
		int totalCount = (int)threadDao.getUserThreadCount(userId);
		if(totalCount == 0) return;
		int loopCount = totalCount % 1000  == 0 ? totalCount / 1000 : totalCount / 1000 + 1;
		//如果用户的帖子数很多，那就分批删除
		if(loopCount > 0) {
			int start = 0;
			for(int idx = 0; idx < loopCount; idx ++) {
				int end = start + 999;
				if(end > totalCount) end = totalCount;
				
				List<Pair<Long, Long>> threadIdForumIdList = userTPCDao.getForumIdThreadIdPairList(userId, start, end);
				deleteThreadRedisAndSolr(userId, threadIdForumIdList);
				
				start += 1000;
			}
		} else {
			List<Pair<Long, Long>> threadIdForumIdList = userTPCDao.getForumIdThreadIdPairList(userId, 0, totalCount - 1);
			deleteThreadRedisAndSolr(userId, threadIdForumIdList);
		}
		
		//缓存删除干净了，删除数据库数据
		deleteThreadDao(userId);
		long endTime = System.currentTimeMillis();
		GlobalObject.INFO_LOG.info("at UserTPCRemoveServiceImpl.deleteThread. 删除userId:"+ userId +"的帖子数据耗时:" + (endTime - startTime) + "毫秒");
	}
	
	private void deleteThreadRedisAndSolr(long userId, List<Pair<Long, Long>> forumIdThreadIds) {
		try {
			if(forumIdThreadIds == null || forumIdThreadIds.size() ==0) return;
			
			List<String> solrDeleteThreadIs = new ArrayList<String>(forumIdThreadIds.size());
			for(int idx = 0; idx < forumIdThreadIds.size(); idx ++) {
				Pair<Long, Long> forumIdAndThreadIdPair = forumIdThreadIds.get(idx);
				long threadId = forumIdAndThreadIdPair.right;
				long forumId = forumIdAndThreadIdPair.left;
				//删除楼层
				Set<String> postIds = postRedis.getThreadPostList(threadId, 0, -1);
				if(postIds != null && postIds.size() > 0) {
					for(String strPostId : postIds)
						deleteThreadPostRedisAndSolr(Long.valueOf(strPostId));
				}
				
				
				///删除主题楼层列表
				postRedis.deleteThreadPostListByThreadId(threadId);
				///删除主题楼主楼层列表
				postRedis.deleteHostPostListByThreadId(threadId);
				
				//删除板块帖子列表下的帖子id
				threadRedis.deleteFromForumThreadList(forumId, threadId);
				threadRedis.deleteFromForumTopThreadList(forumId, threadId);
				threadRedis.deleteFromUserRecommendThreadList(userId, threadId);
				
				solrDeleteThreadIs.add(String.valueOf(threadId));
			}
			
			///将该主题的楼层从索引中删除
			postSolr.deleteByThreadIds(solrDeleteThreadIs);
			///将该主题的评论从索引中删除
			commentSolr.deleteByThreadIds(solrDeleteThreadIs);
			
			///将主题信息从索引中删除
			threadSolr.deleteByIds(solrDeleteThreadIs);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at UserTPCRemoveServiceImpl.deleteThreadRedisAndSolr throw an error.");
		}
	}
	
	private void deleteThreadPostRedisAndSolr(long postId) throws Exception {
		//删除楼层信息
		postRedis.delete(postId);
		
		//删除楼层评论
		Set<String> commentIds = commentRedis.getPostCommentList(postId, 0, -1);
		if(commentIds != null && commentIds.size() > 0) {
			for(String strCommentId : commentIds) 
				commentRedis.delete(Long.valueOf(strCommentId));
		}
		
		//删除楼层评论列表
		commentRedis.deletePostCommentListByPostId(postId);
	}
	
	private void deleteThreadDao(long userId) throws Exception{
		//删除楼层表中帖子的楼层
		userTPCDao.deleteThreadPostAll(userId);
		
		//删除评论表中帖子的评论
		userTPCDao.deleteThreadCommentAll(userId);
		
		//删除收藏表中帖子的对应关系
		userTPCDao.deleteThreadFavorateAll(userId);
		
		//删除点赞表中帖子的对应关系
		userTPCDao.deleteThreadRecommendAll(userId);
		
		//最后删除掉所有该用户的帖子
		userTPCDao.deleteThreadAll(userId);
	}

	private void deletePost(long userId) throws Exception {
		long startTime = System.currentTimeMillis();
		int totalCount = (int)postDao.getUserPostCount(userId);
		if(totalCount == 0) return;
		int loopCount = totalCount % 1000  == 0l ? totalCount / 1000 : totalCount / 1000 + 1;
		if(loopCount > 0) {
			int start = 0;
			for(int idx = 0; idx < loopCount; idx ++) {
				int end = start + 999;
				if(end > totalCount) end = totalCount;
				
				List<Pair<Long, Long>> threadIdPostIds = userTPCDao.getThreadIdPostIdPairList(userId, start, end);
				deletePostRedisAndSolr(userId, threadIdPostIds);
				
				start += 1000;
			}
		}else {
			List<Pair<Long, Long>> threadIdPostIds = userTPCDao.getThreadIdPostIdPairList(userId, 0, totalCount - 1);
			deletePostRedisAndSolr(userId, threadIdPostIds);
		}
		
		//缓存删除干净了，删除数据库数据
		deletePostDao(userId);
		long endTime = System.currentTimeMillis();
		GlobalObject.INFO_LOG.info("at UserTPCRemoveServiceImpl.deletePost. 删除userId:"+ userId +"的楼层数据耗时:" + (endTime - startTime) + "毫秒");
	}
	
	private void deletePostRedisAndSolr(long userId, List<Pair<Long, Long>> threadIdPostIds)  {
		try {
			if(threadIdPostIds == null || threadIdPostIds.size() == 0) return;
			List<String> solrDeletePostIds = new ArrayList<String>(threadIdPostIds.size());
			Map<Long, Integer> threadIdCountMap = new HashMap<Long, Integer>();
			for(int idx = 0; idx < threadIdPostIds.size(); idx ++) {
				Pair<Long, Long> pair = threadIdPostIds.get(idx);
				long threadId = pair.left;
				long postId = pair.right;
				solrDeletePostIds.add(String.valueOf(postId));
				
				///删除楼层信息
				postRedis.delete(postId);
				///将楼层ID 从主题楼层列表中删除
				postRedis.deleteFromThreadPostList(threadId, postId);
				///将楼层ID 从楼主楼层列表中删除
				postRedis.deleteFromHostPostList(threadId, postId);
				///将楼层ID从用户点赞楼层列表中删除
				postRedis.deleteFromUserRecommendPostList(userId, postId);
				
				//删除楼层评论
				Set<String> commentIds = commentRedis.getPostCommentList(postId, 0, -1);
				if(null != commentIds && commentIds.size() > 0) {
					for(String strCommentId : commentIds) 
						commentRedis.delete(Long.valueOf(strCommentId));
				}
				
				//删除楼层评论列表
				commentRedis.deletePostCommentListByPostId(postId);
				
				Integer threadIdCount = threadIdCountMap.get(threadId);
				if(threadIdCount == null) threadIdCountMap.put(threadId, 1);
				else threadIdCountMap.put(threadId, threadIdCount+1);
			}
			
			//更新redis主题回复数
			for(Map.Entry<Long, Integer> entry : threadIdCountMap.entrySet()) { threadRedis.decrReplies(entry.getKey(), entry.getValue()); }
			
			//将该楼层的评论从索引中删除
			commentSolr.deleteByPostIds(solrDeletePostIds);
			
			///将楼层信息从索引中批量删除
			postSolr.deleteByIds(solrDeletePostIds);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at UserTPCRemoveServiceImpl.deletePostRedisAndSolr throw an error.");
		}
	}
	
	private void deletePostDao(long userId) throws Exception {
		//删除楼层下的评论
		userTPCDao.deletePostCommentAll(userId);
		//删除楼层点赞用户关系
		userTPCDao.deletePostRecommendAll(userId);
		///批量更新主题回复数 -1
		//userTPCDao.updatePostThreadRepliesAll(userId);
		//删除用户下所有楼层
		userTPCDao.deletePostAll(userId);
	}
	
	private void deleteComment(long userId) throws Exception {
		long startTime = System.currentTimeMillis();
		int totalCount = (int)commentDao.getUserCommentCount(userId);
		if(totalCount == 0) return;
		int loopCount = totalCount % 1000  == 0l ? totalCount / 1000 : totalCount / 1000 + 1;
		if(loopCount > 0) {
			int start = 0;
			for(int idx = 0; idx < loopCount; idx ++) {
				int end = start + 999;
				if(end > totalCount) end = totalCount;
				
				List<Object[]> postIdCommentIds = userTPCDao.getPostIdCommentIdPairList(userId, start, end);
				deleteCommentRedisAndSolr(userId, postIdCommentIds);
				
				start += 1000;
			}
		} else {
			List<Object[]> postIdCommentIds = userTPCDao.getPostIdCommentIdPairList(userId, 0, totalCount - 1);
			deleteCommentRedisAndSolr(userId, postIdCommentIds);
		}
		
		//缓存删除干净了，删除数据库数据
		deleteCommentDao(userId);
		long endTime = System.currentTimeMillis();
		GlobalObject.INFO_LOG.info("at UserTPCRemoveServiceImpl.deleteComment. 删除userId:"+ userId +"的评论数据耗时:" + (endTime - startTime) + "毫秒");
	}
	
	private void deleteCommentRedisAndSolr(long userId, List<Object[]> postIdCommentIds) {
		try {
			if(postIdCommentIds == null || postIdCommentIds.size() == 0) return;
			List<String> solrDeleteCommentIds = new ArrayList<String>(postIdCommentIds.size());
			Map<Long, Integer> postIdCountMap = new HashMap<Long, Integer>();
			Map<Long, Integer> threadIdCountMap = new HashMap<Long, Integer>();
			for(int idx = 0; idx < postIdCommentIds.size(); idx ++) {
				Object[] objArr = postIdCommentIds.get(idx);
				long threadId = (Long)objArr[0];
				long postId = (Long)objArr[1];
				long commentId = (Long)objArr[2];
				
				///删除评论信息
				commentRedis.delete(commentId);
				///将评论ID 从楼层评论列表中删除
				commentRedis.deleteFromPostCommentList(postId, commentId);
				
				solrDeleteCommentIds.add(String.valueOf(commentId));
				
				Integer postIdCount = postIdCountMap.get(postId);
				Integer threadIdCount = threadIdCountMap.get(threadId);
				
				if(postIdCount == null) postIdCountMap.put(postId, 1);
				else postIdCountMap.put(postId, postIdCount + 1);
				
				if(threadIdCount == null) threadIdCountMap.put(threadId, 1);
				else threadIdCountMap.put(threadId, threadIdCount + 1);
			}
			
//			///楼层评论数 -1
//			postRedis.decrComments(postId);
//			///主题回复数 -1
//			threadRedis.decrReplies(threadId);
			for(Map.Entry<Long, Integer> entry : postIdCountMap.entrySet()) postRedis.decrComments(entry.getKey(), entry.getValue());;
			for(Map.Entry<Long, Integer> entry : threadIdCountMap.entrySet()) threadRedis.decrReplies(entry.getKey(), entry.getValue());
			
			///将评论信息从索引中删除
			commentSolr.deleteByIds(solrDeleteCommentIds);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at UserTPCRemoveServiceImpl.deleteCommentRedisAndSolr throw an error.");
		}
	}
	
	private void deleteCommentDao(long userId) throws Exception {
//		//批量更新楼层评论数-1
//		userTPCDao.updateCommentPostRepliesAll(userId);
//		//批量更新主题回复数-1
//		userTPCDao.updateCommentThreadRepliesAll(userId);
		//删除评论
		userTPCDao.deleteCommentAll(userId);
	}
	
	
}
