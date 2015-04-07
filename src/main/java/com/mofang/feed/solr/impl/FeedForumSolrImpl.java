package com.mofang.feed.solr.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.solr.FeedForumSolr;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumSolrImpl extends BaseSolr implements FeedForumSolr
{
	private final static FeedForumSolrImpl SOLR = new FeedForumSolrImpl();
	
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