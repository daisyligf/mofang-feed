package com.mofang.feed.logic.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.logic.FeedForumLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;

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
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	
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
			boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.ADD_FORUM);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存版块信息
			long forumId = forumService.build(model);
			
			///构建返回结果
			JSONObject data = new JSONObject();
			data.put("fid", forumId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
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
			boolean hasPrivilege = userRoleService.hasPrivilege(model.getForumId(), operatorId, FeedPrivilege.EDIT_FORUM);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存版块信息
			forumInfo.setName(model.getName());
			forumInfo.setIcon(model.getIcon());
			forumInfo.setColor(model.getColor());
			forumService.edit(forumInfo);
			
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.DELETE_FORUM);
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
			data.put("parent_id", forumInfo.getParentId());
			data.put("name", forumInfo.getName());
			data.put("name_spell", forumInfo.getNameSpell());
			data.put("icon", forumInfo.getIcon());
			data.put("color", forumInfo.getColor());
			data.put("threads", forumInfo.getThreads());
			data.put("today_threads", forumInfo.getTodayThreads());
			data.put("yesterday_threads", forumInfo.getYestodayThreads());
			data.put("create_time", forumInfo.getCreateTime() / 1000);
			
			///老版本字段
			data.put("is_up", 0);
			data.put("tags", "");
			data.put("up_time", 0);
			data.put("display_order", 0);
			data.put("is_show", 0);
			
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
	public ResultValue getForumList(long parentId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayForums =new JSONArray();
			Page<FeedForum> page = forumService.getForumList(parentId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedForum> forums = page.getList();
				if(null != forums)
				{
					JSONObject jsonForum = null;
					for(FeedForum forumInfo : forums)
					{
						jsonForum = new JSONObject();data.put("fid", forumInfo.getForumId());
						jsonForum.put("parent_id", forumInfo.getParentId());
						jsonForum.put("name", forumInfo.getName());
						jsonForum.put("name_spell", forumInfo.getNameSpell());
						jsonForum.put("icon", forumInfo.getIcon());
						jsonForum.put("color", forumInfo.getColor());
						jsonForum.put("threads", forumInfo.getThreads());
						jsonForum.put("today_threads", forumInfo.getTodayThreads());
						jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
						jsonForum.put("create_time", forumInfo.getCreateTime() / 1000);
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
			throw new Exception("at FeedForumLogicImpl.getForumList throw an error.", e);
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
				jsonForum.put("parent_id", forumInfo.getParentId());
				jsonForum.put("name", forumInfo.getName());
				jsonForum.put("name_spell", forumInfo.getNameSpell());
				jsonForum.put("icon", forumInfo.getIcon());
				jsonForum.put("color", forumInfo.getColor());
				jsonForum.put("threads", forumInfo.getThreads());
				jsonForum.put("today_threads", forumInfo.getTodayThreads());
				jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
				jsonForum.put("create_time", forumInfo.getCreateTime() / 1000);
				
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
	public ResultValue saveRecommendForumList(Set<Long> forumIds) throws Exception
	{
		try
		{
			forumService.saveRecommendForumList(forumIds);
			ResultValue result = new ResultValue();
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.saveRecommendForumList throw an error.", e);
		}
	}

	@Override
	public ResultValue getRecommendForumList() throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedForum> list = forumService.getRecommendForumList();
			if(null != list && list.size() > 0)
			{
				JSONObject jsonForum = null;
				for(FeedForum forumInfo : list)
				{
					jsonForum = new JSONObject();
					jsonForum.put("fid", forumInfo.getForumId());
					jsonForum.put("name", forumInfo.getName());
					jsonForum.put("icon", forumInfo.getIcon());
					data.put(jsonForum);
				}
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.getRecommendForumList throw an error.", e);
		}
	}

	@Override
	public ResultValue getHotForumList(int size) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedForum> list = forumService.getRecommendForumList();
			if(null != list && list.size() > 0)
			{
				JSONObject jsonForum = null;
				for(FeedForum forumInfo : list)
				{
					jsonForum = new JSONObject();
					jsonForum.put("fid", forumInfo.getForumId());
					jsonForum.put("parent_id", forumInfo.getParentId());
					jsonForum.put("name", forumInfo.getName());
					jsonForum.put("name_spell", forumInfo.getNameSpell());
					jsonForum.put("icon", forumInfo.getIcon());
					jsonForum.put("color", forumInfo.getColor());
					jsonForum.put("threads", forumInfo.getThreads());
					jsonForum.put("today_threads", forumInfo.getTodayThreads());
					jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
					jsonForum.put("create_time", forumInfo.getCreateTime() / 1000);
					data.put(jsonForum);
				}
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.getHotForumList throw an error.", e);
		}
	}

	@Override
	public ResultValue getHotForumList(Set<Long> forumIds) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			List<FeedForum> list = null;
			if(null != forumIds && forumIds.size() > 0)
				list = forumService.getHotForumList(forumIds);
			else
				list = forumService.getHotForumList(200);
			
			Map<Character, JSONArray> map = new HashMap<Character, JSONArray>();
			if(null != list && list.size() > 0)
			{
				JSONObject jsonForum = null;
				for(FeedForum forumInfo : list)
				{
					jsonForum = new JSONObject();
					jsonForum.put("fid", forumInfo.getForumId());
					jsonForum.put("parent_id", forumInfo.getParentId());
					jsonForum.put("name", forumInfo.getName());
					jsonForum.put("name_spell", forumInfo.getNameSpell());
					jsonForum.put("icon", forumInfo.getIcon());
					jsonForum.put("color", forumInfo.getColor());
					jsonForum.put("threads", forumInfo.getThreads());
					jsonForum.put("today_threads", forumInfo.getTodayThreads());
					jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
					jsonForum.put("create_time", forumInfo.getCreateTime() / 1000);
					
					if(forumInfo.getNameSpell().length() > 0)
					{
						char firstChar = forumInfo.getNameSpell().charAt(0);
						JSONArray arrayItems = new JSONArray();
						if(map.containsKey(firstChar))
							arrayItems = map.get(firstChar);
						
						arrayItems.put(jsonForum);
						map.put(firstChar, arrayItems);
					}
				}
			}
			
			for(char key : map.keySet())
				data.put(String.valueOf(key), map.get(key));
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumLogicImpl.getHotForumList throw an error.", e);
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
						jsonForum.put("fid", forumInfo.getForumId());
						jsonForum.put("parent_id", forumInfo.getParentId());
						jsonForum.put("name", forumInfo.getName());
						jsonForum.put("name_spell", forumInfo.getNameSpell());
						jsonForum.put("icon", forumInfo.getIcon());
						jsonForum.put("color", forumInfo.getColor());
						jsonForum.put("threads", forumInfo.getThreads());
						jsonForum.put("today_threads", forumInfo.getTodayThreads());
						jsonForum.put("yesterday_threads", forumInfo.getYestodayThreads());
						jsonForum.put("create_time", forumInfo.getCreateTime() / 1000);
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