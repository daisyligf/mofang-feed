package com.mofang.feed.logic.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.global.common.ModuleItemStatus;
import com.mofang.feed.global.common.SquareType;
import com.mofang.feed.global.common.ThreadTag;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.feed.logic.FeedModuleItemLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedModuleItem;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedModuleItemService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedModuleItemServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.feed.util.MiniTools;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModuleItemLogicImpl implements FeedModuleItemLogic
{
	private final static FeedModuleItemLogicImpl LOGIC = new FeedModuleItemLogicImpl();
	private FeedModuleItemService itemService = FeedModuleItemServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	
	private FeedModuleItemLogicImpl()
	{}
	
	public static FeedModuleItemLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedModuleItem model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			long threadId = model.getThreadId();
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			model.setTitle(threadInfo.getSubject());
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.ADD_MODULE_ITEM);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///保存版块主题信息
			itemService.add(model);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue edit(FeedModuleItem model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///模块主题有效性检查
			FeedModuleItem itemInfo = itemService.getInfo(model.getItemId());
			if(null == itemInfo)
			{
				result.setCode(ReturnCode.MODULE_ITEM_NOT_EXISTS);
				result.setMessage(ReturnMessage.MODULE_ITEM_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.EDIT_MODULE_ITEM);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///编辑版块主题信息
			itemInfo.setTitle(model.getTitle());
			itemInfo.setSubTitle(model.getSubTitle());
			itemInfo.setPicUrl(model.getPicUrl());
			itemInfo.setOnlineTime(model.getOnlineTime());
			itemInfo.setUpdateTime(System.currentTimeMillis());
			itemService.edit(itemInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.edit throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long itemId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.DELETE_MODULE_ITEM);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///检查模块主题是否存在
			FeedModuleItem itemInfo = itemService.getInfo(itemId);
			if(null == itemInfo)
			{
				result.setCode(ReturnCode.MODULE_ITEM_NOT_EXISTS);
				result.setMessage(ReturnMessage.MODULE_ITEM_NOT_EXISTS);
				return result;
			}
			
			///删除模块主题信息
			itemService.delete(itemInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue updateDisplayOrder(long itemId, int displayOrder, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			
			///模块主题有效性检查
			FeedModuleItem itemInfo = itemService.getInfo(itemId);
			if(null == itemInfo)
			{
				result.setCode(ReturnCode.MODULE_ITEM_NOT_EXISTS);
				result.setMessage(ReturnMessage.MODULE_ITEM_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			///boolean hasPrivilege = userRoleService.hasPrivilege(0L, operatorId, FeedPrivilege.UPDATE_MODULE_ITEM_DISPLAYORDER);
			boolean hasPrivilege = false;
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			itemInfo.setDisplayOrder(displayOrder);
			if(itemInfo.getStatus() == ModuleItemStatus.ONLINE)
				itemService.updateDisplayOrder(itemInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.updateDisplayOrder throw an error.", e);
		}
	}

	@Override
	public ResultValue getInfo(long itemId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			FeedModuleItem itemInfo = itemService.getInfo(itemId);
			if(null == itemInfo)
			{
				result.setCode(ReturnCode.MODULE_ITEM_NOT_EXISTS);
				result.setMessage(ReturnMessage.MODULE_ITEM_NOT_EXISTS);
				return result;
			}
			
			JSONObject data = new JSONObject();
			data.put("id", itemInfo.getItemId());
			data.put("vid", itemInfo.getModuleId());
			data.put("tid", itemInfo.getThreadId());
			data.put("display_order", itemInfo.getDisplayOrder());
			data.put("status", itemInfo.getStatus());
			data.put("subject_old", "");
			data.put("subject", itemInfo.getTitle());
			data.put("subtitle", itemInfo.getSubTitle());
			data.put("pic_url", itemInfo.getPicUrl());
			data.put("online_time", itemInfo.getOnlineTime() / 1000);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue getItemList(long moduleId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			long total = 0L;
			JSONObject data = new JSONObject();
			JSONArray arrayItems = new JSONArray();
			Page<FeedModuleItem> page = itemService.getItemList(moduleId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedModuleItem> list = page.getList();
				if(null != list && list.size() > 0)
				{
					JSONObject jsonItem = null;
					for(FeedModuleItem itemInfo : list)
					{
						jsonItem = new JSONObject();
						jsonItem.put("id", itemInfo.getItemId());
						jsonItem.put("vid", itemInfo.getModuleId());
						jsonItem.put("tid", itemInfo.getThreadId());
						jsonItem.put("display_order", itemInfo.getDisplayOrder());
						jsonItem.put("status", itemInfo.getStatus());
						jsonItem.put("subject_old", "");
						jsonItem.put("subject", itemInfo.getTitle());
						jsonItem.put("subtitle", itemInfo.getSubTitle());
						jsonItem.put("pic_url", itemInfo.getPicUrl());
						jsonItem.put("online_time", itemInfo.getOnlineTime() / 1000);
						arrayItems.put(jsonItem);
					}
				}
			}
			data.put("total", total);
			data.put("list", arrayItems);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.getModuleThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getSquareElite(int pageNum, int pageSize, String version) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayItems = new JSONArray();
			JSONArray arrayRolls = new JSONArray();
			JSONArray arrayButtons = new JSONArray();
			long total = 0L;
			long moduleId = GlobalConfig.SQUARE_ELITE_MODULE_ID;
			long rollId = GlobalConfig.SQUARE_ELITE_ROLL_ID;
			
			Page<FeedModuleItem> page = null;
			page = itemService.getModuleThreadList(moduleId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedModuleItem> list = page.getList();
				arrayItems = getSquareThreadList(list, SquareType.ELITE, version);
			}
			
			page = itemService.getModuleThreadList(rollId, 1, 10);
			if(null != page)
			{
				List<FeedModuleItem> list = page.getList();
				arrayRolls = getSquareThreadList(list, SquareType.ELITE, version);
			}
			
			arrayButtons = getSquareButtonList(version);
			
			data.put("total", total);
			data.put("thread", arrayItems);
			data.put("lunbo_thread", arrayRolls);
			data.put("button_info", arrayButtons);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.getSquareElite throw an error.", e);
		}
	}

	@Override
	public ResultValue getSquareImage(int pageNum, int pageSize, String version) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayItems = new JSONArray();
			long total = 0L;
			long moduleId = GlobalConfig.SQUARE_IMAGE_MODULE_ID;
			
			Page<FeedModuleItem> page = null;
			page = itemService.getModuleThreadList(moduleId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedModuleItem> list = page.getList();
				if(null != list && list.size() > 0)
				{
					JSONObject jsonItem = null;
					FeedThread threadInfo = null;
					FeedPost postInfo = null;
					for(FeedModuleItem itemInfo : list)
					{
						jsonItem = new JSONObject();
						threadInfo = itemInfo.getThread();
						if(null == threadInfo)
							continue;
						
						jsonItem.put("tid", threadInfo.getThreadId());
						jsonItem.put("utime", threadInfo.getCreateTime() / 1000);
						jsonItem.put("postcnt", threadInfo.getReplies());
						jsonItem.put("recommends", threadInfo.getRecommends());
						jsonItem.put("type", threadInfo.getType());
						jsonItem.put("shares", threadInfo.getShareTimes());
						jsonItem.put("views", threadInfo.getPageView());
						
						String title = itemInfo.getTitle();
						if(StringUtil.isNullOrEmpty(title))
							title = threadInfo.getSubject();
						jsonItem.put("title", title);
						jsonItem.put("subtitle", itemInfo.getSubTitle());
						jsonItem.put("is_hot", 0);
						jsonItem.put("video", "");
						jsonItem.put("first_pic_height", 120);
						jsonItem.put("first_pic_width", 120);
						jsonItem.put("isrecommend", "0");
						jsonItem.put("content", "");
						jsonItem.put("html_message", "");
						
						postInfo = threadInfo.getPost();
						if(null != postInfo)
							jsonItem.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
						
						arrayItems.put(jsonItem);
					}
				}
			}
			data.put("total", total);
			data.put("thread", arrayItems);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.getSquareElite throw an error.", e);
		}
	}

	@Override
	public ResultValue getSquareVideo(int pageNum, int pageSize, String version) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayItems = new JSONArray();
			JSONArray arrayRolls = new JSONArray();
			long total = 0L;
			long moduleId = GlobalConfig.SQUARE_VIDEO_MODULE_ID;
			long rollId = GlobalConfig.SQUARE_VIDEO_ROLL_ID;
			
			Page<FeedModuleItem> page = null;
			page = itemService.getModuleThreadList(moduleId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedModuleItem> list = page.getList();
				arrayItems = getSquareThreadList(list, SquareType.VIDEO, version);
			}
			
			page = itemService.getModuleThreadList(rollId, 1, 10);
			if(null != page)
			{
				List<FeedModuleItem> list = page.getList();
				arrayRolls = getSquareThreadList(list, SquareType.VIDEO, version);
			}
			
			data.put("total", total);
			data.put("thread", arrayItems);
			data.put("lunbo_thread", arrayRolls);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.getSquareVideo throw an error.", e);
		}
	}
	
	private JSONArray getSquareThreadList(List<FeedModuleItem> list, SquareType type, String version) throws Exception
	{
		try
		{
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			
			String linkUrlBase = GlobalConfig.FEED_DETAIL_URL;
			if(type == SquareType.VIDEO)
				linkUrlBase = GlobalConfig.FEED_VIDEO_URL;
			
			JSONArray arrayItems = new JSONArray();
			JSONObject jsonItem = null;
			FeedThread threadInfo = null;
			FeedPost postInfo = null;
			FeedForum forumInfo = null;
			User userInfo = null;
			for(FeedModuleItem itemInfo : list)
			{
				jsonItem = new JSONObject();
				threadInfo = itemInfo.getThread();
				if(null == threadInfo)
					continue;
				
				jsonItem.put("fid", threadInfo.getForumId());
				jsonItem.put("tid", threadInfo.getThreadId());
				jsonItem.put("uid", threadInfo.getUserId());
				jsonItem.put("utime", threadInfo.getCreateTime() / 1000);
				jsonItem.put("last_post_time", threadInfo.getLastPostTime() / 1000);
				
				jsonItem.put("type", threadInfo.getType());
				String title = itemInfo.getTitle();
				if(StringUtil.isNullOrEmpty(title))
					title = threadInfo.getSubject();
				jsonItem.put("title", title);
				jsonItem.put("last_poster_id", threadInfo.getLastPostUid());
				jsonItem.put("forum_url", GlobalConfig.FORUM_DETAIL_URL + "?fid=" + threadInfo.getForumId());
				
				///构建tag信息(老版本中主题的状态是用标签来实现的, 新版中已经改用字段, 但接口需要支持老版本)
				String tags = "";
				if(threadInfo.isElite())
					tags += "," + ThreadTag.ELITE;
				if(threadInfo.isVideo())
					tags += "," + ThreadTag.VIDEO;
				if(threadInfo.isMark())
					tags += "," + ThreadTag.MARK;
				if(threadInfo.getType() == ThreadType.QUESTION)
					tags += "," + ThreadTag.QUESTION;
				if(tags.length() > 0)
					tags = tags.substring(1);
				jsonItem.put("tags", tags);
				jsonItem.put("postcnt", threadInfo.getReplies());
				jsonItem.put("recommends", threadInfo.getRecommends());
				jsonItem.put("is_closed", threadInfo.isClosed() ? 1 : 0);
				jsonItem.put("is_hot", 0);
				jsonItem.put("page_view", threadInfo.getPageView());
				jsonItem.put("category", threadInfo.isTop() ? 1 : 0);
				jsonItem.put("views", threadInfo.getPageView());
				jsonItem.put("subtitle", itemInfo.getSubTitle());
				
				String linkUrl = threadInfo.getLinkUrl();
				if(StringUtil.isNullOrEmpty(linkUrl))
					linkUrl = linkUrlBase + "?tid" + threadInfo.getThreadId() + "&type=0";
				jsonItem.put("url", linkUrl);
				
				postInfo = threadInfo.getPost();
				String content = "";
				String htmlContent = "";
				if(null != postInfo)
				{
					content = postInfo.getContentFilter();
					htmlContent = postInfo.getHtmlContentFilter();
					jsonItem.put("content", content);
					jsonItem.put("html_content", htmlContent);
				}
				
				///获取海报图
				String picUrl = itemInfo.getPicUrl();
				if(StringUtil.isNullOrEmpty(picUrl))
				{
					String pics = postInfo.getPictures();
					if(!StringUtil.isNullOrEmpty(pics))
					{
						String[] arrPic = pics.split(",");
						picUrl = arrPic[0];
					}
				}
				if(!picUrl.startsWith("http://"))
					picUrl = GlobalConfig.IMAGE_PREFIX_URL + picUrl;
				
				// 版本号1.5.0之后的都是pic数组,之前是字符串
				int intVer = getVerInt(version);
				if (intVer >= 15000)
					jsonItem.put("pic", MiniTools.StringToJSONArray(picUrl));
				else
					jsonItem.put("pic", picUrl);
				
				
				forumInfo = forumService.getInfo(threadInfo.getForumId());
				String forumName = "";
				if(null != forumInfo)
				{
					forumName = forumInfo.getName();
					jsonItem.put("forum_name", forumName);
				}
				
				///获取发布主题的用户信息
				userInfo = UserComponent.getInfoFromCache(threadInfo.getUserId());
				if(null == userInfo)
					uids.add(threadInfo.getUserId());
				else
				{
					jsonItem.put("nickname", userInfo.getNickName());
					jsonItem.put("avatar", userInfo.getAvatar());
					JSONObject jsonUser = new JSONObject();
					jsonUser.put("level", userInfo.getLevel());
					jsonUser.put("exp", userInfo.getExp());
					jsonUser.put("coin", userInfo.getCoin());
					jsonUser.put("diamond", userInfo.getDiamond());
					jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
					jsonUser.put("gained_exp", userInfo.getGainedExp());
					jsonUser.put("badge", userInfo.getBadges());
					///判断是否为版主
					int roleId = userRoleService.getRoleId(threadInfo.getForumId(), threadInfo.getUserId());
					jsonUser.put("is_moderator", roleId > 0);
					jsonItem.put("user", jsonUser);
				}	
				
				///获取最后回复用户信息
				userInfo = UserComponent.getInfoFromCache(threadInfo.getLastPostUid());
				if(null == userInfo)
					uids.add(threadInfo.getUserId());
				else
					jsonItem.put("last_poster_name", userInfo.getNickName());
				
				arrayItems.put(jsonItem);
			}
			
			///填充用户信息
			if(uids.size() > 0)
			{
				Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
				if(null != userMap)
				{
					for(int i=0; i<arrayItems.length(); i++)
					{
						jsonItem = arrayItems.getJSONObject(i);
						String nickName = jsonItem.optString("nickname", "");
						String lastPosterName = jsonItem.optString("last_poster_name", "");
						long userId = jsonItem.optLong("uid", 0L);
						long lastPosterId = jsonItem.optLong("last_poster_id", 0L);
						
						///填充发帖用户信息
						if(StringUtil.isNullOrEmpty(nickName))
						{
							if(userMap.containsKey(userId))
							{
								userInfo = userMap.get(userId);
								jsonItem.put("nickname", userInfo.getNickName());
								jsonItem.put("avatar", userInfo.getAvatar());
								JSONObject jsonUser = new JSONObject();
								jsonUser.put("level", userInfo.getLevel());
								jsonUser.put("exp", userInfo.getExp());
								jsonUser.put("coin", userInfo.getCoin());
								jsonUser.put("diamond", userInfo.getDiamond());
								jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
								jsonUser.put("gained_exp", userInfo.getGainedExp());
								jsonUser.put("badge", userInfo.getBadges());
								///判断是否为版主
								int roleId = userRoleService.getRoleId(threadInfo.getForumId(), threadInfo.getUserId());
								jsonUser.put("is_moderator", roleId > 0);
								jsonItem.put("user", jsonUser);
							}
						}
						///填充最后回复用户信息
						if(StringUtil.isNullOrEmpty(lastPosterName))
						{
							if(userMap.containsKey(lastPosterId))
							{
								jsonItem.put("last_poster_name", userInfo.getNickName());
							}
						}
					}
				}
			}
			
			return arrayItems;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.getSquareThreadList throw an error.", e);
		}
	}

	private JSONArray getSquareButtonList(String version) throws Exception
	{
		try
		{
			int intVer = getVerInt(version);
			String buttons = GlobalConfig.SQUARE_BUTTONS_NEW;
			if(intVer < 13500)
				buttons = GlobalConfig.SQUARE_BUTTONS_OLD;
			
			JSONArray arrayForums = new JSONArray();
			JSONObject jsonForum = null;
			FeedForum forumInfo = null;
			String[] arrButtons = buttons.split(",");
			for(String strForumId : arrButtons)
			{
				forumInfo = forumService.getInfo(Long.parseLong(strForumId));
				if(null == forumInfo)
					continue;
				
				jsonForum = new JSONObject();
				jsonForum.put("title", forumInfo.getName());
				jsonForum.put("pic", forumInfo.getIcon());
				
				String url =  GlobalConfig.AREA_DETAIL_URL + "?id=" + strForumId;
				if(intVer < 13000)
					url = GlobalConfig.FORUM_DETAIL_URL + "?fid=" + strForumId;
				
				jsonForum.put("url", url);
				arrayForums.put(jsonForum);
			}
			return arrayForums;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModuleItemLogicImpl.getSquareButtonList throw an error.", e);
		}
	}
	
	private int getVerInt(String ver)
	{
		if(com.mofang.framework.util.StringUtil.isNullOrEmpty(ver))
			return 0;
		
		String[] arr = ver.split("_");
		if(null == arr || arr.length < 2)
			return 0;
		
		String verNum = arr[1];
		verNum = verNum.replace(".", "");
		int intVer = 0;
		if(!com.mofang.framework.util.StringUtil.isInteger(verNum))
			return 0;
		
		intVer = Integer.parseInt(verNum);
		return intVer;
	}
}