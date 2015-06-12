package com.mofang.feed.logic.web.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.component.SysMessageNotifyComponent;
import com.mofang.feed.component.TaskComponent;
import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.global.common.OperateBehavior;
import com.mofang.feed.global.common.OperateSourceType;
import com.mofang.feed.global.common.RecommendType;
import com.mofang.feed.logic.web.FeedThreadLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.FeedRecommendNotify;
import com.mofang.feed.model.external.SensitiveWord;
import com.mofang.feed.model.external.User;
import com.mofang.feed.record.StatForumViewHistoryRecorder;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.feed.redis.impl.WaterproofWallRedisImpl;
import com.mofang.feed.service.FeedBlackListService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedOperateHistoryService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.ThreadReplyHighestListService;
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.feed.service.impl.ThreadReplyHighestListServiceImpl;
import com.mofang.feed.util.HtmlTagFilter;
import com.mofang.feed.util.MiniTools;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedThreadLogicImpl implements FeedThreadLogic
{
	private final static FeedThreadLogicImpl LOGIC = new FeedThreadLogicImpl();
	private WaterproofWallRedis waterproofWallRedis = WaterproofWallRedisImpl.getInstance();
	private FeedBlackListService blackListService = FeedBlackListServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	private FeedOperateHistoryService operateService = FeedOperateHistoryServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private ThreadReplyHighestListService replyHighestThreadListService = ThreadReplyHighestListServiceImpl.getInstance();
	
	private FeedThreadLogicImpl()
	{}
	
	public static FeedThreadLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedThread model) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			String subject = model.getSubject();
			String content = model.getPost().getContent();
			String htmlContent = model.getPost().getHtmlContent();
			long userId = model.getUserId();
			long forumId = model.getForumId();
			
			///过滤所有HTML标签
			subject = HtmlTagFilter.filterHtmlTag(subject);
			///过滤标题敏感词
			SensitiveWord subjectWord = HttpComponent.sensitiveFilter(subject);
			String subjectFilter = "";
			String subjectMark = "";
			if(null != subjectWord)
			{
				subjectFilter = subjectWord.getOut();
				subjectMark = subjectWord.getOutMark();
			}
			
			if(StringUtil.isNullOrEmpty(subjectFilter))
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
			
			///过滤内容所有HTML标签
			content = HtmlTagFilter.filterHtmlTag(content);
			///过滤内容敏感词
			SensitiveWord contentWord = HttpComponent.sensitiveFilter(content);
			String contentFilter = "";
			String contentMark = "";
			if(null != contentWord)
			{
				contentFilter = contentWord.getOut();
				contentMark = contentWord.getOutMark();
			}
			
			///保留内容指定HTML标签
			htmlContent = HtmlTagFilter.filterOptionHtmlTag(htmlContent);
			///过滤内容敏感词
			SensitiveWord htmlContentWord = HttpComponent.sensitiveFilter(htmlContent);
			String htmlContentFilter = "";
			String htmlContentMark = "";
			if(null != htmlContentWord)
			{
				htmlContentFilter = htmlContentWord.getOut();
				htmlContentMark = htmlContentWord.getOutMark();
			}
			
			if(StringUtil.isNullOrEmpty(contentFilter) || StringUtil.isNullOrEmpty(htmlContentFilter))
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
			
			///禁言检查
			boolean isBlackListUser = blackListService.exists(forumId, userId);
			if(isBlackListUser)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///灌水检查
			boolean isSpam = waterproofWallRedis.isSpam(userId);
			if(isSpam)
			{
				result.setCode(ReturnCode.ADD_FREQUENCY_FAST);
				result.setMessage(ReturnMessage.ADD_FREQUENCY_FAST);
				return result;
			}
			
			///完善Thread实体对象
			model.setSubjectFilter(subjectFilter);
			model.setSubjectMark(subjectMark);
			model.setLastPostUid(model.getUserId());
			model.setLastPostTime(model.getCreateTime());
			
			///保存主题信息
			long threadId = threadService.add(model);
			if(0L == threadId)
			{
				result.setCode(ReturnCode.SERVER_ERROR);
				result.setMessage(ReturnMessage.SERVER_ERROR);
				return result;
			}
			
			///完善Post实体对象
			FeedPost postInfo = model.getPost();
			postInfo.setThreadId(threadId);
			postInfo.setContentFilter(contentFilter);
			postInfo.setContentMark(contentMark);
			postInfo.setHtmlContentFilter(htmlContentFilter);
			postInfo.setHtmlContentMark(htmlContentMark);
			
			///保存楼层信息
			postService.add(postInfo);
			
			/******************************执行任务******************************/
			TaskComponent.addThread(userId);
			
			///返回结果
			JSONObject data = new JSONObject();
			data.put("tid", threadId);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue edit(FeedThread model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			String subject = model.getSubject();
			String content = model.getPost().getContent();
			String htmlContent = model.getPost().getHtmlContent();
			long threadId = model.getThreadId();
			
			///主题有效性检查
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			///权限检查
			boolean hasPrivilege = userRoleService.hasPrivilege(threadInfo.getForumId(), operatorId, FeedPrivilege.EDIT_THREAD);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///过滤所有HTML标签
			subject = HtmlTagFilter.filterHtmlTag(subject);
			///过滤标题敏感词
			SensitiveWord subjectWord = HttpComponent.sensitiveFilter(subject);
			String subjectFilter = "";
			String subjectMark = "";
			if(null != subjectWord)
			{
				subjectFilter = subjectWord.getOut();
				subjectMark = subjectWord.getOutMark();
			}
			
			if(StringUtil.isNullOrEmpty(subjectFilter))
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
			
			///过滤内容所有HTML标签
			content = HtmlTagFilter.filterHtmlTag(content);
			///过滤内容敏感词
			SensitiveWord contentWord = HttpComponent.sensitiveFilter(content);
			String contentFilter = "";
			String contentMark = "";
			if(null != contentWord)
			{
				contentFilter = contentWord.getOut();
				contentMark = contentWord.getOutMark();
			}
			
			///保留内容指定HTML标签
			htmlContent = HtmlTagFilter.filterOptionHtmlTag(htmlContent);
			///过滤内容敏感词
			SensitiveWord htmlContentWord = HttpComponent.sensitiveFilter(htmlContent);
			String htmlContentFilter = "";
			String htmlContentMark = "";
			if(null != htmlContentWord)
			{
				htmlContentFilter = htmlContentWord.getOut();
				htmlContentMark = htmlContentWord.getOutMark();
			}
			
			if(StringUtil.isNullOrEmpty(contentFilter) || StringUtil.isNullOrEmpty(htmlContentFilter))
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
			
			///完善Thread实体对象
			threadInfo.setSubject(subject);
			threadInfo.setSubjectFilter(subjectFilter);
			threadInfo.setSubjectMark(subjectMark);
			threadInfo.setTagId(model.getTagId());
			
			///保存主题信息
			threadService.edit(threadInfo);
			
			///构造Post实体对象
			FeedPost postInfo = postService.getStartPost(threadId);
			if(null != postInfo)
			{
				postInfo.setContent(content);
				postInfo.setContentFilter(contentFilter);
				postInfo.setContentMark(contentMark);
				postInfo.setHtmlContent(htmlContent);
				postInfo.setHtmlContentFilter(htmlContentFilter);
				postInfo.setHtmlContentMark(htmlContentMark);
				postInfo.setPictures(model.getPost().getPictures());
				postInfo.setVideoId(model.getPost().getVideoId());
				
				///更新楼层信息
				postService.edit(postInfo);
			}
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.edit throw an error.", e);
		}
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
			if(operatorId != userId)  ///如果不是作者
			{
				boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.DELETE_THREAD);
				if(!hasPrivilege)
				{
					result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
					result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
					return result;
				}
			}
			///删除主题
			threadService.delete(threadInfo);
			
			/******************************系统通知******************************/
			///不是自己删帖才需要发通知
			if(operatorId != userId)
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.TOP_THREAD);
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.TOP_THREAD);
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.ELITE_THREAD);
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.ELITE_THREAD);
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.CLOSE_THREAD);
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.CLOSE_THREAD);
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
	public ResultValue recommend(long userId, long threadId) throws Exception
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
			
			boolean exists = threadService.existsRecommend(userId, threadId);
			if(!exists)
			{
				///设置主题点赞
				long recommends = threadService.setRecommend(userId, threadId);
				
				/******************************点赞通知******************************/
				FeedRecommendNotify notify = new FeedRecommendNotify();
				notify.setUserId(threadInfo.getUserId());
				notify.setThreadId(threadId);
				notify.setSubject(threadInfo.getSubjectFilter());
				notify.setRecommendType(RecommendType.THREAD);
				notify.setRecommendUserId(userId);
				notify.setForumId(threadInfo.getForumId());
				FeedForum forumInfo = forumService.getInfo(threadInfo.getForumId());
				if(null != forumInfo)
					notify.setForumName(forumInfo.getName());
				HttpComponent.pushFeedRecommendNotify(notify);
				
				/******************************执行任务******************************/
				TaskComponent.recommendThread(userId);
				
				///判断是否为今日的帖子并且点赞数=11
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
				long createTime = threadInfo.getCreateTime();
				long startTime = dateFormat.parse(dateFormat.format(new Date())).getTime();
				long now = System.currentTimeMillis();
				if(recommends == GlobalConfig.COLLECT_RECOMMEND_COUNT &&
				   createTime >= startTime && createTime <= now)
					TaskComponent.collectRecommends(threadInfo.getUserId());
			}
			else
			{
				///取消主题点赞
				threadService.cancelRecommend(userId, threadId);
			}
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.recommend throw an error.", e);
		}
	}
	
	@Override
	public ResultValue getInfo(long threadId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			FeedThread threadInfo = threadService.getFullInfo(threadId);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			JSONObject data = new JSONObject();
			JSONObject jsonForum = null;
			JSONObject jsonUser = null;
			data.put("tid", threadInfo.getThreadId());       ///主题ID
			data.put("subject", threadInfo.getSubjectFilter());       ///主题标题
			data.put("replies", threadInfo.getReplies());         ///主题回复数(楼层+评论)
			data.put("page_view", threadInfo.getPageView());        ///主题浏览数
			data.put("create_time", threadInfo.getCreateTime());        ///主题发布时间
			data.put("last_post_time", threadInfo.getLastPostTime());        ///主题最后回复时间
			data.put("recommends", threadInfo.getRecommends());        ///主题点赞数
			data.put("tag_id", threadInfo.getTagId());						///主题标签ID
			data.put("status", threadInfo.getStatus());         ///主题状态
			data.put("is_elite", threadInfo.isElite());			///是否为精华帖
			data.put("is_top", threadInfo.isTop());			///是否为置顶帖
			data.put("is_closed", threadInfo.isClosed());		///主题是否关闭(锁定)
			
			jsonForum = new JSONObject();
			jsonForum.put("fid", threadInfo.getForumId());
			FeedForum forumInfo = forumService.getInfo(threadInfo.getForumId());
			if(null != forumInfo)
				jsonForum.put("name", forumInfo.getName());
			
			FeedPost postInfo = threadInfo.getPost();
			if(null != postInfo)
			{
				data.put("content", postInfo.getContentFilter());
				data.put("html_content", postInfo.getHtmlContentFilter());
				data.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
			}
			
			jsonUser = new JSONObject();
			jsonUser.put("user_id", threadInfo.getUserId());
			data.put("forum", jsonForum);
			data.put("user", jsonUser);
			
			///返回结果
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
	public ResultValue getForumThreadList(long forumId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		try
		{
			/*********记录用户浏览数**********/
			StatForumViewHistoryRecorder.recordInThreadLogic(forumId, currentUserId);
			
			Page<FeedThread> page = threadService.getForumThreadList(forumId, pageNum, pageSize);
			return convertPageToJSON(page);
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getForumThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getForumTopThreadList(long forumId, long currentUserId) throws Exception
	{
		try
		{
			/*********记录用户浏览数**********/
			StatForumViewHistoryRecorder.recordInThreadLogic(forumId, currentUserId);
			
			Page<FeedThread> page = threadService.getForumTopThreadList(forumId, 3);
			return convertPageToJSON(page);
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getForumTopThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getForumThreadListByCondition(long forumId, int tagId, boolean isElite, int timeType, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		try
		{
			/*********记录用户浏览数**********/
			StatForumViewHistoryRecorder.recordInThreadLogic(forumId, currentUserId);
			
			Page<FeedThread> page = threadService.getForumThreadListByCondition(forumId, tagId, isElite, timeType, pageNum, pageSize);
			return convertPageToJSON(page);
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getForumThreadListByCondition throw an error.", e);
		}
	}

	@Override
	public ResultValue getReplyHighestThreadList(long forumId) throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			Set<String> strThreadIdList = replyHighestThreadListService.getThreadIds(forumId);
			if(strThreadIdList != null){
				JSONObject objThread = null;
				for(String strThreadId : strThreadIdList) {
					long threadId = Long.valueOf(strThreadId);
					objThread = new JSONObject();
					FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
					if(threadInfo == null)
						continue;
					objThread.put("tid", threadId);
					objThread.put("subject", threadInfo.getSubjectFilter());
					
					data.put(objThread);
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedThreadLogicImpl.getReplyHighestThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			Page<FeedThread> page = threadService.search(forumId, forumName, author, keyword, status, pageNum, pageSize);
			return convertPageToJSON(page);
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.search throw an error.", e);
		}
	}
	
	private ResultValue convertPageToJSON(Page<FeedThread> page) throws Exception
	{
		///存储缓存中没有数据的用户ID, 用于批量获取用户信息
		Set<Long> uids = new HashSet<Long>();
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
				FeedPost postInfo = null;
				User userInfo = null;
				for(FeedThread threadInfo : threads)
				{
					jsonThread = new JSONObject();
					jsonThread.put("tid", threadInfo.getThreadId());       ///主题ID
					jsonThread.put("subject", threadInfo.getSubjectFilter());   ///主题标题
					jsonThread.put("replies", threadInfo.getReplies());         ///主题回复数(楼层+评论)
					jsonThread.put("pageview", threadInfo.getPageView());        ///主题浏览数
					jsonThread.put("share_times", threadInfo.getShareTimes());        ///主题分享数
					jsonThread.put("create_time", threadInfo.getCreateTime());        ///主题发布时间
					jsonThread.put("is_closed", threadInfo.isClosed());        ///主题是否关闭(如果关闭, 则不能进行回复和评论)
					jsonThread.put("is_elite", threadInfo.isElite());				///是否为精华帖
					jsonThread.put("is_top", threadInfo.isTop());					///是否为置顶帖
					jsonThread.put("recommends", threadInfo.getRecommends());        ///主题点赞数
					jsonThread.put("status", threadInfo.getStatus());        ///主题状态
					
					///获取主题内容
					postInfo = postService.getStartPost(threadInfo.getThreadId());
					if(null != postInfo)
					{
						jsonThread.put("content", postInfo.getContentFilter());
						jsonThread.put("html_content", postInfo.getHtmlContentFilter());
						jsonThread.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
					}	
					///填充版块信息
					jsonForum = new JSONObject();
					jsonForum.put("fid", threadInfo.getForumId());        ///所属版块ID
					forumInfo = forumService.getInfo(threadInfo.getForumId());
					if(null != forumInfo)
						jsonForum.put("name", forumInfo.getName());
					
					///获取发布主题的用户信息
					jsonUser = new JSONObject();
					jsonUser.put("user_id", threadInfo.getUserId());         ///发布主题的用户ID	
					userInfo = UserComponent.getInfoFromCache(threadInfo.getUserId());
					if(null == userInfo)
						uids.add(threadInfo.getUserId());
					else
					{
						jsonUser.put("nickname", userInfo.getNickName());
						jsonUser.put("avatar", userInfo.getAvatar());
					}
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
							String nickName = jsonThread.optString("nickname", "");
							long userId = jsonThread.optLong("uid", 0L);
							
							///填充发帖用户信息
							if(StringUtil.isNullOrEmpty(nickName))
							{
								if(userMap.containsKey(userId))
								{
									userInfo = userMap.get(userId);
									jsonUser.put("nickname", userInfo.getNickName());
									jsonUser.put("avatar", userInfo.getAvatar());
								}
							}
						}
					}
				}
			}
		}
		
		data.put("total", total);
		data.put("threads", arrayThreads);
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		result.setData(data);
		return result;
	}
}