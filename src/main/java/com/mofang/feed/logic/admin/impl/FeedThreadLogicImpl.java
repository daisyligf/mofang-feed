package com.mofang.feed.logic.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.SysMessageNotifyComponent;
import com.mofang.feed.component.TaskComponent;
import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.global.common.OperateBehavior;
import com.mofang.feed.global.common.OperateSourceType;
import com.mofang.feed.logic.admin.FeedThreadLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.OperatorHistoryInfo;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedOperateHistoryService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadLogicImpl implements FeedThreadLogic
{
	private final static FeedThreadLogicImpl LOGIC = new FeedThreadLogicImpl();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedOperateHistoryService operateService = FeedOperateHistoryServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedThreadLogicImpl()
	{}
	
	public static FeedThreadLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue delete(long threadId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			///权限检查
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除主题
			threadService.delete(threadInfo);
			
			/******************************系统通知******************************/
			///发送删帖通知
			SysMessageNotifyComponent.deleteThread(userId, threadInfo.getSubject(), reason);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.DELETE_THREAD);
			operateInfo.setSourceType(OperateSourceType.THREAD);
			operateInfo.setSourceId(threadId);
			operateInfo.setOperateBehavior(OperateBehavior.DELETE_THREAD);
			operateInfo.setOperateReason(reason);
			operateInfo.setOperatorId(operatorId);
			operateService.add(operateInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue restore(long threadId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.MYSQL);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
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
			
			///还原主题
			threadService.restore(threadInfo);
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.restore throw an error.", e);
		}
	}

	@Override
	public ResultValue remove(long threadId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.MYSQL);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
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
			
			///删除主题(从回收站移除)
			threadService.remove(threadInfo);
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.remove throw an error.", e);
		}
	}

	@Override
	public ResultValue setTop(long threadId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			///无效操作检查
			boolean isTop = threadInfo.isTop();
			if(isTop)
			{
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
			String subject = threadInfo.getSubject();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///设置主题置顶
			threadService.setTop(threadInfo);
			
			/******************************系统通知******************************/
			SysMessageNotifyComponent.setTopThread(operatorId, userId, threadId, subject, reason);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.TOP_THREAD);
			operateInfo.setSourceType(OperateSourceType.THREAD);
			operateInfo.setSourceId(threadId);
			operateInfo.setOperateBehavior(OperateBehavior.TOP_THREAD);
			operateInfo.setOperateReason(reason);
			operateInfo.setOperatorId(operatorId);
			operateService.add(operateInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.setTop throw an error.", e);
		}
	}

	@Override
	public ResultValue cancelTop(long threadId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			///无效操作检查
			boolean isTop = threadInfo.isTop();
			if(!isTop)
			{
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
			String subject = threadInfo.getSubject();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///取消主题置顶
			threadService.cancelTop(threadInfo);
			
			/******************************系统通知******************************/
			SysMessageNotifyComponent.cancelTopThread(operatorId, userId, subject, reason);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.TOP_THREAD);
			operateInfo.setSourceType(OperateSourceType.THREAD);
			operateInfo.setSourceId(threadId);
			operateInfo.setOperateBehavior(OperateBehavior.CANCEL_TOP_THREAD);
			operateInfo.setOperateReason(reason);
			operateInfo.setOperatorId(operatorId);
			operateService.add(operateInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.cancelTop throw an error.", e);
		}
	}

	@Override
	public ResultValue setElite(long threadId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			///无效操作检查
			boolean isElite = threadInfo.isElite();
			if(isElite)
			{
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
			String subject = threadInfo.getSubject();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///设置主题精华
			threadService.setElite(threadId, true);
			
			/******************************执行任务******************************/
			TaskComponent.eliteThread(userId);
			
			/******************************系统通知******************************/
			SysMessageNotifyComponent.setEliteThread(operatorId, userId, subject, reason);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.ELITE_THREAD);
			operateInfo.setSourceType(OperateSourceType.THREAD);
			operateInfo.setSourceId(threadId);
			operateInfo.setOperateBehavior(OperateBehavior.ELITE_THREAD);
			operateInfo.setOperateReason(reason);
			operateInfo.setOperatorId(operatorId);
			operateService.add(operateInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.setElite throw an error.", e);
		}
	}

	@Override
	public ResultValue cancelElite(long threadId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			///无效操作检查
			boolean isElite = threadInfo.isElite();
			if(!isElite)
			{
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///取消主题精华
			threadService.setElite(threadId, false);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.ELITE_THREAD);
			operateInfo.setSourceType(OperateSourceType.THREAD);
			operateInfo.setSourceId(threadId);
			operateInfo.setOperateBehavior(OperateBehavior.CANCEL_ELITE_THREAD);
			operateInfo.setOperateReason(reason);
			operateInfo.setOperatorId(operatorId);
			operateService.add(operateInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.cancelElite throw an error.", e);
		}
	}

	@Override
	public ResultValue close(long threadId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			///无效操作检查
			boolean isClosed = threadInfo.isClosed();
			if(isClosed)
			{
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///设置主题关闭
			threadService.setClosed(threadId, true);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.CLOSE_THREAD);
			operateInfo.setSourceType(OperateSourceType.THREAD);
			operateInfo.setSourceId(threadId);
			operateInfo.setOperateBehavior(OperateBehavior.CLOSE_THREAD);
			operateInfo.setOperateReason(reason);
			operateInfo.setOperatorId(operatorId);
			operateService.add(operateInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.close throw an error.", e);
		}
	}

	@Override
	public ResultValue open(long threadId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			///无效操作检查
			boolean isClosed = threadInfo.isClosed();
			if(!isClosed)
			{
				result.setCode(ReturnCode.INVALID_OPERATION);
				result.setMessage(ReturnMessage.INVALID_OPERATION);
				return result;
			}
			
			long forumId = threadInfo.getForumId();
			long userId = threadInfo.getUserId();
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///设置主题打开
			threadService.setClosed(threadId, false);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.CLOSE_THREAD);
			operateInfo.setSourceType(OperateSourceType.THREAD);
			operateInfo.setSourceId(threadId);
			operateInfo.setOperateBehavior(OperateBehavior.OPEN_THREAD);
			operateInfo.setOperateReason(reason);
			operateInfo.setOperatorId(operatorId);
			operateService.add(operateInfo);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.open throw an error.", e);
		}
	}

	@Override
	public ResultValue getInfo(long threadId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONObject jsonForum = null;
			JSONObject jsonUser = null;
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null != threadInfo)
			{
				data.put("tid", threadInfo.getThreadId());       ///主题ID
				data.put("subject", threadInfo.getSubjectFilter());       ///主题标题
				data.put("replies", threadInfo.getReplies());         ///主题回复数(楼层+评论)
				data.put("page_view", threadInfo.getPageView());        ///主题浏览数
				data.put("create_time", threadInfo.getCreateTime());        ///主题发布时间
				data.put("last_post_time", threadInfo.getLastPostTime());        ///主题最后回复时间
				data.put("recommends", threadInfo.getRecommends());        ///主题点赞数
				data.put("status", threadInfo.getStatus());         ///主题状态
				data.put("is_elite", threadInfo.isElite());			///是否为精华帖
				data.put("is_top", threadInfo.isTop());			///是否为置顶帖
				data.put("is_closed", threadInfo.isClosed());		///主题是否关闭(锁定)
				
				jsonForum = new JSONObject();
				jsonForum.put("fid", threadInfo.getForumId());
				
				jsonUser = new JSONObject();
				jsonUser.put("user_id", threadInfo.getUserId());
				data.put("forum", jsonForum);
				data.put("user", jsonUser);
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue getThreadList(long forumId, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			long total = 0;
			JSONArray arrayThreads =new JSONArray();
			Page<FeedThread> page = threadService.getThreadList(forumId, status, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedThread> threads = page.getList();
				if(null != threads)
				{
					
					Map<Long, OperatorHistoryInfo> historyMap = null;
					if(status == 0) {
						Set<Long> threadIds = new HashSet<Long>(threads.size());
						for(int idx = 0; idx < threads.size(); idx ++) {
							threadIds.add(threads.get(idx).getThreadId());
						}
						historyMap = operateService.getMap(threadIds, FeedPrivilege.DELETE_THREAD);
					}
					
					JSONObject jsonThread = null;
					JSONObject jsonForum = null;
					JSONObject jsonUser = null;
					FeedForum forumInfo = null;
					User userInfo = null;
					for(FeedThread threadInfo : threads)
					{
						jsonThread = new JSONObject();
						jsonThread.put("tid", threadInfo.getThreadId());       ///主题ID
						jsonThread.put("subject", threadInfo.getSubjectFilter());       ///主题标题
						jsonThread.put("replies", threadInfo.getReplies());         ///主题回复数(楼层+评论)
						jsonThread.put("page_view", threadInfo.getPageView());        ///主题浏览数
						jsonThread.put("create_time", threadInfo.getCreateTime());        ///主题发布时间
						jsonThread.put("last_post_time", threadInfo.getLastPostTime());        ///主题最后回复时间
						jsonThread.put("recommends", threadInfo.getRecommends());        ///主题点赞数
						jsonThread.put("status", threadInfo.getStatus());         ///主题状态
						jsonThread.put("is_elite", threadInfo.isElite());			///是否为精华帖
						jsonThread.put("is_top", threadInfo.isTop());			///是否为置顶帖
						jsonThread.put("is_closed", threadInfo.isClosed());		///主题是否关闭(锁定)
						
						if(null != historyMap && historyMap.size() != 0) {
							OperatorHistoryInfo historyInfo = historyMap.get(threadInfo.getThreadId());
							if(null != historyInfo) {
								jsonThread.put("oprerator_name", historyInfo.operatorName);
								jsonThread.put("operate_time", historyInfo.operateTime);
							}
						}
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", threadInfo.getForumId());
						forumInfo = forumService.getInfo(threadInfo.getForumId());
						if(null != forumInfo)
							jsonForum.put("name", forumInfo.getName());
						
						jsonUser = new JSONObject();
						jsonUser.put("user_id", threadInfo.getUserId());
						///获取发布主题的用户信息
						userInfo = UserComponent.getInfoFromCache(threadInfo.getUserId());
						if(null == userInfo)
							uids.add(threadInfo.getUserId());
						else
							jsonUser.put("nickname", userInfo.getNickName());
						
						jsonThread.put("forum", jsonForum);
						jsonThread.put("user", jsonUser);
						arrayThreads.put(jsonThread);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayThreads.length(); i++)
							{
								jsonThread = arrayThreads.getJSONObject(i);
								jsonUser = jsonThread.optJSONObject("user");
								String nickName = jsonUser.optString("nickname", "");
								long userId = jsonUser.optLong("user_id", 0L);
								
								///填充发帖用户信息
								if(StringUtil.isNullOrEmpty(nickName))
								{
									if(userMap.containsKey(userId))
									{
										userInfo = userMap.get(userId);
										jsonUser.put("nickname", userInfo.getNickName());
									}
								}
							}
						}
					}
				}
			}
			data.put("total", total);
			data.put("list", arrayThreads);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			Page<FeedThread> page = threadService.search(forumId, forumName, author, keyword, status, pageNum, pageSize);
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayThreads = new JSONArray();
			if(null != page)
			{
				total = page.getTotal();
				List<FeedThread> threads = page.getList();
				if(null != threads)
				{
					JSONObject jsonThread = null;
					JSONObject jsonForum = null;
					JSONObject jsonUser = null;
					FeedForum forumInfo = null;
					User userInfo = null;
					for(FeedThread threadInfo : threads)
					{
						jsonThread = new JSONObject();
						jsonThread = new JSONObject();
						jsonThread.put("tid", threadInfo.getThreadId());       ///主题ID
						jsonThread.put("subject", threadInfo.getSubjectFilter());       ///主题标题
						jsonThread.put("replies", threadInfo.getReplies());         ///主题回复数(楼层+评论)
						jsonThread.put("pageview", threadInfo.getPageView());        ///主题浏览数
						jsonThread.put("create_time", threadInfo.getCreateTime());        ///主题发布时间
						jsonThread.put("last_post_time", threadInfo.getLastPostTime());        ///主题最后回复时间
						jsonThread.put("recommends", threadInfo.getRecommends());        ///主题点赞数
						jsonThread.put("status", threadInfo.getStatus());         ///主题状态
						jsonThread.put("is_elite", threadInfo.isElite());			///是否为精华帖
						jsonThread.put("is_top", threadInfo.isTop());			///是否为置顶帖
						jsonThread.put("is_closed", threadInfo.isClosed());		///主题是否关闭(锁定)
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", threadInfo.getForumId());
						forumInfo = forumService.getInfo(threadInfo.getForumId());
						if(null != forumInfo)
							jsonForum.put("name", forumInfo.getName());
						
						jsonUser = new JSONObject();
						jsonUser.put("user_id", threadInfo.getUserId());
						///获取发布主题的用户信息
						userInfo = UserComponent.getInfoFromCache(threadInfo.getUserId());
						if(null == userInfo)
							uids.add(threadInfo.getUserId());
						else
							jsonUser.put("nickname", userInfo.getNickName());
						
						jsonThread.put("forum", jsonForum);
						jsonThread.put("user", jsonUser);
						arrayThreads.put(jsonThread);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayThreads.length(); i++)
							{
								jsonThread = arrayThreads.getJSONObject(i);
								jsonUser = jsonThread.optJSONObject("user");
								String nickName = jsonUser.optString("nickname", "");
								long userId = jsonUser.optLong("user_id", 0L);
								
								///填充发帖用户信息
								if(StringUtil.isNullOrEmpty(nickName))
								{
									if(userMap.containsKey(userId))
									{
										userInfo = userMap.get(userId);
										jsonUser.put("nickname", userInfo.getNickName());
									}
								}
							}
						}
					}
				}
			}
			
			data.put("total", total);
			data.put("list", arrayThreads);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.search throw an error.", e);
		}
	}

}