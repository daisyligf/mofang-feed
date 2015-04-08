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
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.solr.FeedThreadSolr;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadSolrImpl extends BaseSolr implements FeedThreadSolr
{
	private final static FeedThreadSolrImpl SOLR = new FeedThreadSolrImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	
	private FeedThreadSolrImpl()
	{}
	
	public static FeedThreadSolrImpl getInstance()
	{
		return SOLR;
	}

	@Override
	public void add(FeedThread model) throws Exception
	{
		IndexAdd add = new IndexAdd(model);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void batchAdd(List<FeedThread> list) throws Exception
	{
		IndexBatchAdd add = new IndexBatchAdd(list);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void deleteById(long threadId) throws Exception
	{
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(threadId));
		deleteByIds(ids);
	}

	@Override
	public void deleteByIds(List<String> threadIds) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_THREAD;
		IndexDeleteById delete = new IndexDeleteById(solrServer, threadIds);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public void deleteByForumId(long forumId) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_THREAD;
		String query = "forum_id:" + forumId;
		IndexDeleteByQuery delete = new IndexDeleteByQuery(solrServer, query);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public Page<FeedThread> search(long forumId, String forumName, String author, String keyword, int status, int start, int size) throws Exception
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
		
		query.setStart(start);
		query.setRows(size);
		query.setSort("time", ORDER.desc);
		SolrServer solrServer = GlobalObject.SOLR_SERVER_THREAD;
		QueryResponse response = solrServer.query(query);
		if(null == response)
			return null;
		
		SolrDocumentList docList = response.getResults();
		long total = docList.getNumFound();
		
		List<FeedThread> list = new ArrayList<FeedThread>();
		FeedThread threadInfo = null;
		Iterator<SolrDocument> iterator = docList.iterator();
		String strThreadId;
		while(iterator.hasNext())
		{
			SolrDocument doc = iterator.next();
			strThreadId = doc.getFieldValue("id").toString();
			if(!StringUtil.isLong(strThreadId))
				continue;
			
			threadInfo = threadRedis.getInfo(Long.parseLong(strThreadId));
			if(null == threadInfo)
				continue;
			
			list.add(threadInfo);
		}
		return new Page<FeedThread>(total, list);
	}
	
	class IndexAdd implements Runnable
	{
		private FeedThread threadInfo;
		
		public IndexAdd(FeedThread threadInfo)
		{
			this.threadInfo = threadInfo;
		}

		@Override
		public void run()
		{
			try
			{
				SolrInputDocument solrDoc = convertToDoc(threadInfo);
				GlobalObject.SOLR_SERVER_THREAD.add(solrDoc);
				GlobalObject.SOLR_SERVER_THREAD.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedThreadSolrImpl.IndexAdd.run throw an error", e);
			}
		}
	}
	
	class IndexBatchAdd implements Runnable
	{
		private List<FeedThread> threadList;
		
		public IndexBatchAdd(List<FeedThread> threadList)
		{
			this.threadList = threadList;
		}

		@Override
		public void run()
		{
			try
			{
				List<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
				for(FeedThread threadInfo : threadList)
				{
					SolrInputDocument solrDoc = convertToDoc(threadInfo);
					list.add(solrDoc);
				}
				GlobalObject.SOLR_SERVER_THREAD.add(list);
				GlobalObject.SOLR_SERVER_THREAD.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedThreadSolrImpl.IndexBatchAdd.run throw an error", e);
			}
		}
	}
	
	private SolrInputDocument convertToDoc(FeedThread threadInfo)
	{
		SolrInputDocument solrDoc = new SolrInputDocument();
		try
		{
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
		
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
			
			///构建content信息
			String content = threadInfo.getSubject();
			if(null != threadInfo.getPost())
				content += "|" + threadInfo.getPost().getContent();
			
			solrDoc.addField("id", threadInfo.getThreadId());
			solrDoc.addField("forum_id", forumId);
			solrDoc.addField("forum_name", forumName);
			solrDoc.addField("user_id", userId);
			solrDoc.addField("nickname", nickName);
			solrDoc.addField("status", threadInfo.getStatus());
			solrDoc.addField("time", threadInfo.getCreateTime());
			solrDoc.addField("content", content);
			return solrDoc;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThreadSolrImpl.convertToDoc throw an error", e);
			return null;
		}
	}
}