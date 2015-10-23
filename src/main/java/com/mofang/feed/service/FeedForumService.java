package com.mofang.feed.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedForumService
{
	/**
	 * 创建版块
	 * @param model 版块实体对象
	 * @return 版块ID
	 * @throws Exception
	 */
	public long build(FeedForum model) throws Exception;
	
	public void edit(FeedForum model) throws Exception;
	
	public void delete(long forumId) throws Exception;
	
	public FeedForum getInfo(long forumId) throws Exception;
	
	public FeedForum getInfoWithTags(long forumId) throws Exception;
	
	public Page<FeedForum> getForumList(int type, int pageNum, int pageSize) throws Exception;
	
	public List<FeedForum> getForumList(Set<Long> forumIds) throws Exception;
	
	public Page<FeedForum> search(String forumName, int pageNum, int pageSize) throws Exception;
	
	public Map<String, String> getUrlMap(long forumId) throws Exception;
	
	public List<FeedForum> getForumRecomendList(Set<Long> gameIds) throws Exception;
	
	public List<FeedForum> getForumListByAppstore(Set<Long> forumIds) throws Exception;
}