package com.mofang.feed.data.load.impl;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.redis.FeedCommentRedis;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.feed.redis.impl.FeedCommentRedisImpl;
import com.mofang.feed.redis.impl.WaterproofWallRedisImpl;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.feed.solr.impl.FeedCommentSolrImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedCommentLoad implements FeedLoad
{
	private final static int STEP = 50000;
	private FeedCommentDao commentDao  = FeedCommentDaoImpl.getInstance();
	private FeedCommentRedis commentRedis = FeedCommentRedisImpl.getInstance();
	private WaterproofWallRedis waterproofWallRedis = WaterproofWallRedisImpl.getInstance();
	private FeedCommentSolr commentSolr = FeedCommentSolrImpl.getInstance();

	public void exec()
	{
		List<FeedComment> list = getData();
		if(null == list || list.size() == 0)
		{
			GlobalObject.ERROR_LOG.error("comment data is null or empty.");
			return;
		}
		
		int total = 1;
		List<FeedComment> solrList = new ArrayList<FeedComment>();
		for(FeedComment commentInfo : list)
		{
			handleRedis(commentInfo);
			///添加到solr列表中
			solrList.add(commentInfo);
			if(total % STEP == 0 || total == list.size())
			{
				handleSolr(solrList);
				solrList.clear();
			}
			total++;
		}
		list = null;
		System.gc();
	}
	
	private void handleRedis(FeedComment commentInfo)
	{
		try
		{
			long postId = commentInfo.getPostId();
			long commentId = commentInfo.getCommentId();
			long postTime = commentInfo.getCreateTime();
			long userId = commentInfo.getUserId();
			
			if(commentInfo.getStatus() == CommentStatus.NORMAL)
			{
				///保存评论信息
				commentRedis.save(commentInfo);
				///将评论ID添加到楼层评论列表中
				commentRedis.addPostCommentList(postId, commentId, postTime);
				///更新用户最后发帖时间
				waterproofWallRedis.updateUserLastPostTime(userId, postTime);
			}
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentLoad.handleRedis throw an error.", e);
		}
	}
	
	private void handleSolr(List<FeedComment> solrList)
	{
		try
		{
			if(solrList.size() == 0)
				return;
			
			final List<FeedComment> list = new ArrayList<FeedComment>();
			list.addAll(solrList);
			commentSolr.batchAdd(list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentLoad.handleSolr throw an error.", e);
		}
	}
	
	private List<FeedComment> getData()
	{
		try
		{
			return commentDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentLoad.getData throw an error.", e);
			return null;
		}
	}
}