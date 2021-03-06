package com.mofang.feed.logic.app.impl;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.app.FeedForumLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.Page;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedTagService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedTagServiceImpl;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumLogicImpl implements FeedForumLogic
{
	private final static FeedForumLogicImpl LOGIC = new FeedForumLogicImpl();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private FeedTagService tagService = FeedTagServiceImpl.getInstance();
	
	private FeedForumLogicImpl()
	{}
	
	public static FeedForumLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue getInfo(long forumId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			FeedForum forumInfo = forumService.getInfoWithTags(forumId);
			if(null == forumInfo)
			{
				result.setCode(ReturnCode.FORUM_NOT_EXISTS);
				result.setMessage(ReturnMessage.FORUM_NOT_EXISTS);
				return result;
			}
			
			JSONObject data = new JSONObject();
			data.put("fid", forumInfo.getForumId());
			data.put("name", forumInfo.getName());
			data.put("name_spell", forumInfo.getNameSpell());
			data.put("icon", forumInfo.getIcon());
			data.put("type", forumInfo.getType());
			data.put("threads", forumInfo.getThreads());
			data.put("yesterday_threads", forumInfo.getYestodayThreads());
			data.put("create_time", forumInfo.getCreateTime());
			
			Set<Integer> tagSet = forumInfo.getTags();
			JSONArray arrayTags = new JSONArray();
			if(null != tagSet)
			{
				JSONObject jsonTag = null;
				String tagName;
				for(int tagId : tagSet)
				{
					tagName = tagService.getTagName(tagId);
					if(StringUtil.isNullOrEmpty(tagName))
						continue;
					
					jsonTag = new JSONObject();
					jsonTag.put("tag_id", tagId);
					jsonTag.put("tag_name", tagName);
					arrayTags.put(jsonTag);
				}
			}
			data.put("tags", arrayTags);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue getForumList(Set<Long> forumIds) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			JSONObject jsonForum = null;
			FeedForum forumInfo = null;
			for(long forumId : forumIds)
			{
				forumInfo = forumService.getInfo(forumId);
				if(null == forumInfo)
					continue;
				
				jsonForum = new JSONObject();
				jsonForum.put("fid", forumInfo.getForumId());
				jsonForum.put("name", forumInfo.getName());
				jsonForum.put("name_spell", forumInfo.getNameSpell());
				jsonForum.put("icon", forumInfo.getIcon());
				jsonForum.put("type", forumInfo.getType());
				jsonForum.put("threads", forumInfo.getThreads() + forumInfo.getReplies());
				jsonForum.put("today_threads", forumInfo.getTodayThreads());
				jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
				jsonForum.put("create_time", forumInfo.getCreateTime());
				data.put(jsonForum);
			}
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue search(String forumName, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0L;
			JSONArray arrayForums = new JSONArray();
			Page<FeedForum> page = forumService.search(forumName, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedForum> list = page.getList();
				if(null != list && list.size() > 0)
				{
					JSONObject jsonForum = null;
					for(FeedForum forumInfo : list)
					{
						jsonForum = new JSONObject();
						long forumId = forumInfo.getForumId();
						jsonForum.put("fid", forumId);
						jsonForum.put("name", forumInfo.getName());
						jsonForum.put("name_spell", forumInfo.getNameSpell());
						jsonForum.put("icon", forumInfo.getIcon());
						jsonForum.put("type", forumInfo.getType());
						jsonForum.put("threads", forumInfo.getThreads() + forumInfo.getReplies());
						jsonForum.put("today_threads", forumInfo.getTodayThreads());
						jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
						jsonForum.put("create_time", forumInfo.getCreateTime());
						arrayForums.put(jsonForum);
					}
				}
			}
			
			data.put("total", total);
			data.put("forums", arrayForums);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.search throw an error.", e);
		}
	}

	@Override
	public ResultValue getForumRecomendList(Set<Long> gameIds) throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			
			List<FeedForum> list = forumService.getForumRecomendList(gameIds);
			if(list != null) {
				JSONObject item = null;
				for(FeedForum forum : list) {
					item = new JSONObject();
					item.put("id", forum.getForumId());
					item.put("icon", forum.getIcon());
					item.put("name", forum.getName());
					item.put("threads_num", forum.getThreads());
					item.put("follow_num", forum.getFollows());     //关注总数
					item.put("is_see", 0);
					data.put(item);
				}
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedForumLogicImpl.forumRecommends throw an error.");
		}
	}

	@Override
	public ResultValue getForumListByAppstore(Set<Long> forumIds) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			
			List<FeedForum> list = forumService.getForumListByAppstore(forumIds);
			if(list != null)
			{
				JSONObject item = null;
				for(FeedForum forum : list)
				{
					item = new JSONObject();
					item.put("id", forum.getForumId());
					item.put("icon", forum.getIcon());
					item.put("name", forum.getName());
					item.put("threads_num", forum.getThreads());
					item.put("follow_num", forum.getFollows());     //关注总数
					item.put("is_see", 0);
					data.put(item);
				}
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} 
		catch (Exception e) 
		{
			throw new Exception("at FeedForumLogicImpl.getForumListByAppstore throw an error.");
		}
	}
}