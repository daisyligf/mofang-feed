package com.mofang.feed.logic.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.logic.admin.FeedForumLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.FollowForumCount;
import com.mofang.feed.model.external.Game;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedForumTagService;
import com.mofang.feed.service.FeedTagService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedForumTagServiceImpl;
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
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedForumTagService forumTagService = FeedForumTagServiceImpl.getInstance();
	private FeedTagService tagService = FeedTagServiceImpl.getInstance();
	
	private FeedForumLogicImpl()
	{}
	
	public static FeedForumLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedForum model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///根据gameId获取游戏信息
			if(model.getType() != ForumType.OFFICAL)
			{
				Game gameInfo = HttpComponent.getGameInfo(model.getGameId());
				if(null != gameInfo)
				{
					String icon = gameInfo.getIcon();
					model.setIcon(icon);
				}
			}
			
			///保存版块信息
			long forumId = forumService.build(model);
			
			///保存版块标签
			forumTagService.addBatch(forumId, model.getTags());
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue edit(FeedForum model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///版块有效性检查
			FeedForum forumInfo = forumService.getInfo(model.getForumId());
			if(null == forumInfo)
			{
				result.setCode(ReturnCode.FORUM_NOT_EXISTS);
				result.setMessage(ReturnMessage.FORUM_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///根据gameId获取游戏信息
			if(model.getType() != ForumType.OFFICAL)
			{
				if(forumInfo.getGameId() != model.getGameId())
				{
					Game gameInfo = HttpComponent.getGameInfo(model.getGameId());
					if(null != gameInfo)
					{
						String icon = gameInfo.getIcon();
						forumInfo.setIcon(icon);
					}
				}
			}
			
			///保存版块信息
			forumInfo.setName(model.getName());
			forumInfo.setColor(model.getColor());
			forumInfo.setType(model.getType());
			forumInfo.setGameId(model.getGameId());
			forumService.edit(forumInfo);
			
			///保存版块标签
			forumTagService.addBatch(model.getForumId(), model.getTags());
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.edit throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long forumId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除版块信息
			forumService.delete(forumId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue getInfo(long forumId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			FeedForum forumInfo = forumService.getInfo(forumId);
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
			data.put("today_threads", forumInfo.getTodayThreads());
			data.put("create_time", forumInfo.getCreateTime());
			data.put("game_id", forumInfo.getGameId());
			
			///获取关注数
			Set<Long> forumIds = new HashSet<Long>();
			forumIds.add(forumId);
			Map<Long, FollowForumCount> followMap = HttpComponent.getForumFollowCount(forumIds);
			int totalFollows = 0;
			int yestodayFollows = 0;
			if(null != followMap && followMap.containsKey(forumId))
			{
				totalFollows = followMap.get(forumId).getTotalFollows();
				yestodayFollows = followMap.get(forumId).getYestodyFollows();
			}
			data.put("follows", totalFollows);
			data.put("yestoday_follows", yestodayFollows);
			
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
	public ResultValue getForumList(int type, int pageNum, int pageSize) throws Exception
	{
		try
		{
			///存储版块ID, 用于批量获取版块关注数据
			Set<Long> forumIds = new HashSet<Long>();
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayForums =new JSONArray();
			Page<FeedForum> page = forumService.getForumList(type, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedForum> forums = page.getList();
				if(null != forums)
				{
					JSONObject jsonForum = null;
					for(FeedForum forumInfo : forums)
					{
						forumIds.add(forumInfo.getForumId());
						jsonForum = new JSONObject();
						jsonForum.put("fid", forumInfo.getForumId());
						jsonForum.put("name", forumInfo.getName());
						jsonForum.put("name_spell", forumInfo.getNameSpell());
						jsonForum.put("icon", forumInfo.getIcon());
						jsonForum.put("type", forumInfo.getType());
						jsonForum.put("threads", forumInfo.getThreads());
						jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
						jsonForum.put("create_time", forumInfo.getCreateTime());
						arrayForums.put(jsonForum);
					}
					
					///填充版块关注数据
					Map<Long, FollowForumCount> followMap = HttpComponent.getForumFollowCount(forumIds);
					if(null != followMap)
					{
						for(int i=0; i<arrayForums.length(); i++)
						{
							jsonForum = arrayForums.getJSONObject(i);
							long forumId = jsonForum.optLong("fid", 0L);
							
							int totalFollows = 0;
							int yestodayFollows = 0;
							if(followMap.containsKey(forumId))
							{
								totalFollows = followMap.get(forumId).getTotalFollows();
								yestodayFollows = followMap.get(forumId).getYestodyFollows();
							}
							jsonForum.put("follows", totalFollows);
							jsonForum.put("yestoday_follows", yestodayFollows);
						}
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
			throw new Exception("at FeedForumLogicImpl.getForumList throw an error.", e);
		}
	}

	@Override
	public ResultValue search(String forumName, int pageNum, int pageSize) throws Exception
	{
		try
		{
			///存储版块ID, 用于批量获取版块关注数据
			Set<Long> forumIds = new HashSet<Long>();
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
						forumIds.add(forumInfo.getForumId());
						jsonForum = new JSONObject();
						jsonForum.put("fid", forumInfo.getForumId());
						jsonForum.put("name", forumInfo.getName());
						jsonForum.put("name_spell", forumInfo.getNameSpell());
						jsonForum.put("icon", forumInfo.getIcon());
						jsonForum.put("type", forumInfo.getType());
						jsonForum.put("threads", forumInfo.getThreads());
						jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
						jsonForum.put("follows", forumInfo.getFollows());
						jsonForum.put("yestoday_follows", forumInfo.getYestodayFollows());
						jsonForum.put("create_time", forumInfo.getCreateTime());
						arrayForums.put(jsonForum);
					}
					///填充版块关注数据
					Map<Long, FollowForumCount> followMap = HttpComponent.getForumFollowCount(forumIds);
					if(null != followMap)
					{
						for(int i=0; i<arrayForums.length(); i++)
						{
							jsonForum = arrayForums.getJSONObject(i);
							long forumId = jsonForum.optLong("fid", 0L);
							
							int totalFollows = 0;
							int yestodayFollows = 0;
							if(followMap.containsKey(forumId))
							{
								totalFollows = followMap.get(forumId).getTotalFollows();
								yestodayFollows = followMap.get(forumId).getYestodyFollows();
							}
							jsonForum.put("follows", totalFollows);
							jsonForum.put("yestoday_follows", yestodayFollows);
						}
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
