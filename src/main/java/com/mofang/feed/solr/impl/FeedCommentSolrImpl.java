package com.mofang.feed.solr.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.external.User;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.solr.FeedCommentSolr;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedCommentSolrImpl extends BaseSolr implements FeedCommentSolr
{
	private final static FeedCommentSolrImpl SOLR = new FeedCommentSolrImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	
	private FeedCommentSolrImpl()
	{}
	
	public static FeedCommentSolrImpl getInstance()
	{
		return SOLR;
	}

	@Override
	public void add(FeedComment model) throws Exception
	{
		IndexAdd add = new IndexAdd(model);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void batchAdd(List<FeedComment> list) throws Exception
	{
		IndexBatchAdd add = new IndexBatchAdd(list);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void deleteById(long commentId) throws Exception
	{
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(commentId));
		deleteByIds(ids);
	}

	@Override
	public void deleteByIds(List<String> commentIds) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		IndexDeleteById delete = new IndexDeleteById(solrServer, commentIds);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByForumId(long forumId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		String query = "forum_id:" + forumId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByThreadId(long threadId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		String query = "thread_id:" + threadId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByPostId(long postId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_COMMENT;
		String query = "post_id:" + postId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	class IndexAdd implements Runnable
	{
		private FeedComment commentInfo;
		
		public IndexAdd(FeedComment commentInfo)
		{
			this.commentInfo = commentInfo;
		}

		@Override
		public void run()
		{
			try
			{
				SolrInputDocument solrDoc = convertToDoc(commentInfo);
				GlobalObject.SOLR_SERVER_COMMENT.add(solrDoc);
				GlobalObject.SOLR_SERVER_COMMENT.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedCommentSolrImpl.IndexAdd.run throw an error", e);
			}
		}
	}
	
	class IndexBatchAdd implements Runnable
	{
		private List<FeedComment> commentList;
		
		public IndexBatchAdd(List<FeedComment> commentList)
		{
			this.commentList = commentList;
		}

		@Override
		public void run()
		{
			try
			{
				List<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
				for(FeedComment commentInfo : commentList)
				{
					SolrInputDocument solrDoc = convertToDoc(commentInfo);
					list.add(solrDoc);
				}
				GlobalObject.SOLR_SERVER_COMMENT.add(list);
				GlobalObject.SOLR_SERVER_COMMENT.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedCommentSolrImpl.IndexBatchAdd.run throw an error", e);
			}
		}
	}
	
	private SolrInputDocument convertToDoc(FeedComment commentInfo)
	{
		SolrInputDocument solrDoc = new SolrInputDocument();
		try
		{
			long forumId = commentInfo.getForumId();
			long userId = commentInfo.getUserId();
		
			///获取版块信息
			String forumName = "";
			FeedForum forumInfo = forumRedis.getInfo(forumId);
			if(null != forumInfo)
				forumName = forumInfo.getName();
			
			///获取用户信息
			String nickName = "";
			User userInfo = UserComponent.getInfo(userId);
			if(null != userInfo)
				nickName = userInfo.getNickName();
			
			solrDoc.addField("id", commentInfo.getCommentId());
			solrDoc.addField("forum_id", forumId);
			solrDoc.addField("forum_name", forumName);
			solrDoc.addField("thread_id", commentInfo.getThreadId());
			solrDoc.addField("post_id", commentInfo.getPostId());
			solrDoc.addField("user_id", userId);
			solrDoc.addField("nickname", nickName);
			solrDoc.addField("status", commentInfo.getStatus());
			solrDoc.addField("time", commentInfo.getCreateTime());
			solrDoc.addField("content", commentInfo.getContent());
			return solrDoc;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedCommentSolrImpl.convertToDoc throw an error", e);
			return null;
		}
	}
}