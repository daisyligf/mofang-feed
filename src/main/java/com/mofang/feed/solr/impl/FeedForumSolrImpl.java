package com.mofang.feed.solr.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.Page;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.solr.FeedForumSolr;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumSolrImpl extends BaseSolr implements FeedForumSolr
{
	private final static FeedForumSolrImpl SOLR = new FeedForumSolrImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	
	private FeedForumSolrImpl()
	{}
	
	public static FeedForumSolrImpl getInstance()
	{
		return SOLR;
	}

	@Override
	public void add(FeedForum model) throws Exception
	{
		IndexAdd add = new IndexAdd(model);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void batchAdd(List<FeedForum> forumList) throws Exception
	{
		IndexBatchAdd add = new IndexBatchAdd(forumList);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(add);
	}

	@Override
	public void deleteById(long forumId) throws Exception
	{
		List<String> ids = new ArrayList<String>();
		ids.add(String.valueOf(forumId));
		deleteByIds(ids);
	}

	@Override
	public void deleteByIds(List<String> forumIds) throws Exception
	{
		SolrServer solrServer = GlobalObject.SOLR_SERVER_FORUM;
		IndexDeleteById delete = new IndexDeleteById(solrServer, forumIds);
		GlobalObject.SOLR_INDEX_EXECUTOR.execute(delete);
	}

	@Override
	public Page<FeedForum> search(String forumName, int start, int size) throws Exception
	{
		SolrQuery query = new SolrQuery();
		query.setQuery("name:" + forumName);
		query.setStart(start);
		query.setRows(size);
		SolrServer solrServer = GlobalObject.SOLR_SERVER_FORUM;
		QueryResponse response = solrServer.query(query);
		if(null == response)
			return null;
		
		SolrDocumentList docList = response.getResults();
		long total = docList.getNumFound();
		
		List<FeedForum> list = new ArrayList<FeedForum>();
		FeedForum forumInfo = null;
		Iterator<SolrDocument> iterator = docList.iterator();
		String strForumId;
		while(iterator.hasNext())
		{
			SolrDocument doc = iterator.next();
			strForumId = doc.getFieldValue("id").toString();
			if(!StringUtil.isLong(strForumId))
				continue;
			
			forumInfo = forumRedis.getInfo(Long.parseLong(strForumId));
			if(null == forumInfo)
				continue;
			
			list.add(forumInfo);
		}
		return new Page<FeedForum>(total, list);
	}

	class IndexAdd implements Runnable
	{
		private FeedForum forumInfo;
		
		public IndexAdd(FeedForum forumInfo)
		{
			this.forumInfo = forumInfo;
		}

		@Override
		public void run()
		{
			try
			{
				SolrInputDocument solrDoc = convertToDoc(forumInfo);
				GlobalObject.SOLR_SERVER_FORUM.add(solrDoc);
				GlobalObject.SOLR_SERVER_FORUM.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedForumSolrImpl.IndexAdd.run throw an error", e);
			}
		}
	}

	class IndexBatchAdd implements Runnable
	{
		private List<FeedForum> forumList;
		
		public IndexBatchAdd(List<FeedForum> forumList)
		{
			this.forumList = forumList;
		}

		@Override
		public void run()
		{
			try
			{
				List<SolrInputDocument> list = new ArrayList<SolrInputDocument>();
				for(FeedForum forumInfo : forumList)
				{
					SolrInputDocument solrDoc = convertToDoc(forumInfo);
					if(null == solrDoc)
						continue;
					
					list.add(solrDoc);
				}
				GlobalObject.SOLR_SERVER_FORUM.add(list);
				GlobalObject.SOLR_SERVER_FORUM.commit();
			}
			catch(Exception e)	
			{
				GlobalObject.ERROR_LOG.error("at FeedForumSolrImpl.IndexAdd.run throw an error", e);
			}
		}
	}
	
	private SolrInputDocument convertToDoc(FeedForum forumInfo)
	{
		SolrInputDocument solrDoc = new SolrInputDocument();
		try
		{
			solrDoc.addField("id", forumInfo.getForumId());
			solrDoc.addField("name", forumInfo.getName());
			return solrDoc;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumSolrImpl.IndexBatchAdd.convertToDoc throw an error", e);
			return null;
		}
	}
}