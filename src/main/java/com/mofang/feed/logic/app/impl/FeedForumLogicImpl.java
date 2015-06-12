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
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedTagService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedTagServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
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
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
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
			data.put("follows", forumInfo.getFollows());
			data.put("yesterday_follows", forumInfo.getYestodayFollows());
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
			JSONArray arrayThreads = null;
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
				jsonForum.put("color", forumInfo.getColor());
				jsonForum.put("threads", forumInfo.getThreads());
				jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
				jsonForum.put("follows", forumInfo.getFollows());
				jsonForum.put("yestoday_follows", forumInfo.getYestodayFollows());
				jsonForum.put("create_time", forumInfo.getCreateTime());
				
				///获取版块精华帖
				Page<FeedThread> page = threadService.getForumEliteThreadList(forumId, 1, 10);
				arrayThreads = new JSONArray();
				if(null != page)
				{
					List<FeedThread> list = page.getList();
					if(null != list && list.size() > 0)
					{
						for(FeedThread threadInfo : list)
							arrayThreads.put(threadInfo.getSubjectFilter());
					}
				}
				jsonForum.put("thread", arrayThreads);
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
						jsonForum.put("threads", forumInfo.getThreads());
						jsonForum.put("today_threads", forumInfo.getTodayThreads());
						jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
						jsonForum.put("follows", forumInfo.getFollows());
						jsonForum.put("yestoday_follows", forumInfo.getYestodayFollows());
						jsonForum.put("create_time", forumInfo.getCreateTime());
						arrayForums.put(jsonForum);
					}
				}
			}
			
			data.put("total", total);
			data.put("list", arrayForums);
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
}