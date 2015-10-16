package com.mofang.feed.solr.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;

import com.mofang.feed.global.GlobalObject;

/**
 * 
 * @author zhaodx
 *
 */
public abstract class BaseSolr
{
	protected class IndexDeleteById implements Runnable
	{
		private SolrServer solrServer;
		private List<String> uniqueIds;
		
		public IndexDeleteById(SolrServer solrServer, List<String> uniqueIds)
		{
			this.solrServer = solrServer;
			this.uniqueIds = uniqueIds;
		}
		
		@Override
		public void run()
		{
			try
			{
				solrServer.deleteById(uniqueIds);
				solrServer.commit();
			}
			catch(Exception e)
			{
				GlobalObject.ERROR_LOG.error("at IndexDeleteById.run throw an error", e);
			}
		}
	}
	
	protected class IndexDeleteByQuery implements Runnable
	{
		private SolrServer solrServer;
		private String query;
		
		public IndexDeleteByQuery(SolrServer solrServer, String query)
		{
			this.solrServer = solrServer;
			this.query = query;
		}
		
		@Override
		public void run()
		{
			try
			{
				///删除全部索引: http://localhost:8080/solr/update/?stream.body=<delete><query>*:*</query></delete>&stream.contentType=text/xml;charset=utf-8&commit=true
				///删除指定ID的索引: http://localhost:8080/solr/update/?stream.body=<delete><id>1</id></delete>&stream.contentType=text/xml;charset=utf-8&commit=true
				solrServer.deleteByQuery(query);
				solrServer.commit();
			}
			catch(Exception e)
			{
				GlobalObject.ERROR_LOG.error("at IndexDeleteByQuery.run throw an error", e);
			}
		}
	}
	
	protected class IndexDeleteThreadQuery implements Runnable {
		
		private SolrServer solrServer;
		private List<String> queryList;
		
		public IndexDeleteThreadQuery(SolrServer solrServer, List<String> queryList) {
			this.solrServer = solrServer;
			this.queryList = queryList;
		}
		
		@Override
		public void run() {
			try {
				for(int idx = 0; idx < queryList.size(); idx ++) {
					String query = "thread_id:" + queryList.get(idx);
					solrServer.deleteByQuery(query);
				}
				solrServer.commit();
			} catch(Exception e) {
				GlobalObject.ERROR_LOG.error("at IndexDeleteThreadQuery.run throw an error", e);
			}

		}
		
	}
	
	protected class IndexDeletePostQuery implements Runnable {
		
		private SolrServer solrServer;
		private List<String> queryList;
		
		public IndexDeletePostQuery(SolrServer solrServer, List<String> queryList) {
			this.solrServer = solrServer;
			this.queryList = queryList;
		}
		
		@Override
		public void run() {
			try {
				for(int idx = 0; idx < queryList.size(); idx ++) {
					String query = "post_id:" + queryList.get(idx);
					solrServer.deleteByQuery(query);
				}
				solrServer.commit();
			} catch(Exception e) {
				GlobalObject.ERROR_LOG.error("at IndexDeleteThreadQuery.run throw an error", e);
			}

		}
		
	}
	
}