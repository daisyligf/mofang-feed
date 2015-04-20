package com.mofang.feed.solr.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.FeedPostRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.FeedPostRedisImpl;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostSolrImpl extends BaseSolr implements FeedPostSolr
{
	private final static FeedPostSolrImpl SOLR = new FeedPostSolrImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedPostRedis postRedis = FeedPostRedisImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	
	private FeedPostSolrImpl()
	{}
	
	public static FeedPostSolrImpl getInstance()
	{
		return SOLR;
	}

	@Override
	public void add(FeedPost model) throws Exception
	{
		IndexAdd add = new IndexAdd(model);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void batchAdd(List<FeedPost> list) throws Exception
	{
		IndexBatchAdd add = new IndexBatchAdd(list);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void deleteById(long postId) throws Exception
	{
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(postId));
		deleteByIds(ids);
	}

	@Override
	public void deleteByIds(List<String> postIds) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_POST;
		IndexDeleteById delete = new IndexDeleteById(solrServer, postIds);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByForumId(long forumId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_POST;
		String query = "forum_id:" + forumId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByThreadId(long threadId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_POST;
		String query = "thread_id:" + threadId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public Page<FeedPost> search(long forumId, String forumName, String author, String keyword, int status, int start, int size) throws Exception
	{
		SolrQuery query = new SolrQuery();
		StringBuilder strQuery = new StringBuilder();
		if(forumId > 0)
			strQuery.append(" AND forum_id:" + forumId);
		if(!StringUtil.isNullOrEmpty(forumName))
			strQuery.append(" AND forum_name:" + forumName);
		if(!StringUtil.isNullOrEmpty(author))
			strQuery.append(" AND nickname:" + author);
		if(!StringUtil.isNullOrEmpty(keyword))
			strQuery.append(" AND content:" + keyword);
		strQuery.append(" AND status:" + status);
		
		if(strQuery.length() == 0)
			return null;
		String queryParam = strQuery.substring(4, strQuery.length());
		queryParam = "(" + queryParam + ")";
		
		query.setQuery(queryParam);
		
		query.setStart(start);
		query.setRows(size);
		query.setSort("time", ORDER.desc);
		SolrServer solrServer = GlobalObject.SOLR_SERVER_POST;
		QueryResponse response = solrServer.query(query);
		if(null == response)
			return null;
		
		SolrDocumentList docList = response.getResults();
		long total = docList.getNumFound();
		
		List<FeedPost> list = new ArrayList<FeedPost>();
		FeedPost postInfo = null;
		Iterator<SolrDocument> iterator = docList.iterator();
		String strPostId;
		while(iterator.hasNext())
		{
			SolrDocument doc = iterator.next();
			strPostId = doc.getFieldValue("id").toString();
			if(!StringUtil.isLong(strPostId))
				continue;
			
			if(status == PostStatus.NORMAL)
				postInfo = postRedis.getInfo(Long.parseLong(strPostId));
			else if(status == PostStatus.DELETED)
				postInfo = postDao.getInfo(Long.parseLong(strPostId));
			if(null == postInfo)
				continue;
			
			list.add(postInfo);
		}
		return new Page<FeedPost>(total, list);
	}

	class IndexAdd implements Runnable
	{
		private FeedPost postInfo;
		
		public IndexAdd(FeedPost postInfo)
		{
			this.postInfo = postInfo;
		}

		@Override
		public void run()
		{
			try
			{
				SolrInputDocument solrDoc = convertToDoc(postInfo);
				GlobalObject.SOLR_SERVER_POST.add(solrDoc);
				GlobalObject.SOLR_SERVER_POST.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedPostSolrImpl.IndexAdd.run throw an error", e);
			}
		}
	}
	
	class IndexBatchAdd implements Runnable
	{
		private List<FeedPost> postList;
		
		public IndexBatchAdd(List<FeedPost> postList)
		{
			this.postList = postList;
		}

		@Override
		public void run()
		{
			try
			{
				List<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
				for(FeedPost postInfo : postList)
				{
					SolrInputDocument solrDoc = convertToDoc(postInfo);
					list.add(solrDoc);
				}
				GlobalObject.SOLR_SERVER_POST.add(list);
				GlobalObject.SOLR_SERVER_POST.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedPostSolrImpl.IndexBatchAdd.run throw an error", e);
			}
		}
	}
	
	private SolrInputDocument convertToDoc(FeedPost postInfo)
	{
		SolrInputDocument solrDoc = new SolrInputDocument();
		try
		{
			long forumId = postInfo.getForumId();
			long userId = postInfo.getUserId();
		
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
			
			solrDoc.addField("id", postInfo.getPostId());
			solrDoc.addField("forum_id", forumId);
			solrDoc.addField("forum_name", forumName);
			solrDoc.addField("thread_id", postInfo.getThreadId());
			solrDoc.addField("user_id", userId);
			solrDoc.addField("nickname", nickName);
			solrDoc.addField("status", postInfo.getStatus());
			solrDoc.addField("time", postInfo.getCreateTime());
			solrDoc.addField("content", postInfo.getContent());
			return solrDoc;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPostSolrImpl.convertToDoc throw an error", e);
			return null;
		}
	}
}