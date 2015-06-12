package com.mofang.feed.logic.app.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import com.mofang.feed.global.common.ThreadTag;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.feed.logic.app.FeedThreadLogic;
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
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
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
			data.put("fid", threadInfo.getForumId());        ///所属版块ID
			data.put("tid", threadInfo.getThreadId());       ///主题ID
			data.put("uid", threadInfo.getUserId());         ///发布主题的用户ID
			data.put("title", threadInfo.getSubject());       ///主题标题
			data.put("postcnt", threadInfo.getReplies());         ///主题回复数(楼层+评论)
			data.put("page_view", threadInfo.getPageView());        ///主题浏览数
			data.put("share_times", threadInfo.getShareTimes());        ///主题分享数
			data.put("utime", threadInfo.getCreateTime() / 1000);        ///主题发布时间
			data.put("last_poster_id", threadInfo.getLastPostUid());        ///主题最后回复用户ID
			data.put("last_post_time", threadInfo.getLastPostTime() / 1000);        ///主题最后回复时间
			data.put("is_closed", threadInfo.isClosed());        ///主题是否关闭(如果关闭, 则不能进行回复和评论)
			data.put("recommends", threadInfo.getRecommends());        ///主题点赞数
			data.put("category", threadInfo.isTop() ? 1 : 0);        ///是否为置顶帖
			
			///构建linkurl (linkurl是一种link规则，客户端根据link规则来进行跳转)
			String linkurl = threadInfo.getLinkUrl();
			if(StringUtil.isNullOrEmpty(linkurl))
				linkurl = GlobalConfig.FEED_DETAIL_URL + "?tid=" + threadInfo.getThreadId() + "&type=0";
			data.put("link_url", linkurl);
			
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
			
			data.put("tags", tags);
			
			///获取1楼信息
			FeedPost postInfo = threadInfo.getPost();
			if(null != postInfo)
			{
				data.put("content", postInfo.getContentFilter());
				data.put("html_content", postInfo.getHtmlContentFilter());
				String pics = postInfo.getPictures();
				JSONArray jsonArrayPics = MiniTools.StringToJSONArray(pics);
				data.put("pic", pics);
				data.put("pic_url", jsonArrayPics);
				
				///获取视频信息
				if(threadInfo.isVideo())
				{
					JSONObject jsonVideo = new JSONObject();
					jsonVideo.put("id", postInfo.getVideoId());
					jsonVideo.put("duration", postInfo.getDuration());
					jsonVideo.put("thumbnail", postInfo.getThumbnail());
					data.put("video", jsonVideo);
				}
			}
			
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
			Page<FeedThread> page = threadService.getForumThreadList(forumId, pageNum, pageSize);
			ResultValue resultValue = formatForumThreads(forumId, page, currentUserId);
			
			/*********记录用户浏览数**********/
			StatForumViewHistoryRecorder.recordInThreadLogic(forumId, currentUserId);
			
			return resultValue;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getForumThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getForumTopThreadList(long forumId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayThreads = new JSONArray();
			Page<FeedThread> page = threadService.getForumTopThreadList(forumId, 3);
			if(null != page)
			{
				List<FeedThread> threads = page.getList();
				if(null != threads)
					arrayThreads = listToJSONArray(threads, 0L);
			}
			
			data.put("threads", arrayThreads);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getForumTopThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getForumEliteThreadList(long forumId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		try
		{
			Page<FeedThread> page = threadService.getForumEliteThreadList(forumId, pageNum, pageSize);
			ResultValue resultValue = formatForumThreads(forumId, page, currentUserId);
			
			/*********记录用户浏览数**********/
			StatForumViewHistoryRecorder.recordInThreadLogic(forumId, currentUserId);
			
			return resultValue;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getForumEliteThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getUserThreadList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			Page<FeedThread> page = threadService.getUserThreadList(userId, pageNum, pageSize);
			return formatUserThreads(page);
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getUserThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getUserFavoriteThreadList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			Page<FeedThread> page = threadService.getUserFavoriteThreadList(userId, pageNum, pageSize);
			ResultValue value = formatUserThreads(page);
			JSONObject data = (JSONObject)value.getData();
			if(null != data)
			{
				JSONArray arrayThreads = data.optJSONArray("threadlist");
				if(null != arrayThreads && arrayThreads.length() > 0)
				{
					JSONObject jsonThread = null;
					long forumId = 0L;
					FeedForum forumInfo = null;
					for(int i=0; i<arrayThreads.length(); i++)
					{
						jsonThread = arrayThreads.getJSONObject(i);
						forumId = jsonThread.optLong("fid", 0L);
						forumInfo = forumService.getInfo(forumId);
						if(null != forumInfo)
						{
							jsonThread.put("name", forumInfo.getName());
							jsonThread.put("icon", forumInfo.getIcon());
						}
					}
				}
			}
			return value;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getUserFavoriteThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getGlobalEliteThreadList(int pageNum, int pageSize) throws Exception
	{
		try {
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			Page<FeedThread> page = threadService.getGlobalEliteThreadList(pageNum, pageSize);
			long total = 0;
			JSONArray arrayThreads = new JSONArray();
			JSONObject jsonThread = null;
			if(null != page)
			{
				total = page.getTotal();
				List<FeedThread> threads = page.getList();
				if(null != threads)
				{
					for(FeedThread threadInfo : threads)
					{
						jsonThread = new JSONObject();
						jsonThread.put("tid", threadInfo.getThreadId());
						jsonThread.put("subject", threadInfo.getSubjectFilter());
						JSONObject jsonForum = new JSONObject();
						jsonForum.put("fid", threadInfo.getForumId());
						FeedForum forumInfo = forumService.getInfo(threadInfo.getForumId());
						if(null != forumInfo)
							jsonForum.put("name", forumInfo.getName());
						jsonThread.put("forum", jsonForum);
						jsonThread.put("create_time", threadInfo.getCreateTime());
						jsonThread.put("reply_cnt", threadInfo.getReplies());
						jsonThread.put("page_view", threadInfo.getPageView());
						arrayThreads.put(jsonThread);
					}
				}
			}
			
			data.put("total", total);
			data.put("list", arrayThreads);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedThreadLogicImpl.getGlobalEliteThreadList throw an error.", e);
		}
	}

	@Override
	public ResultValue getForumEliteThreadList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayThreads = new JSONArray();
			
			long start = System.currentTimeMillis();
			
			Set<Long> forumIds = HttpComponent.getUserFllowForums(userId);
			/*
			Set<Long> forumIds = new HashSet<Long>();
			forumIds.add(12L);
			forumIds.add(185L);
			*/
			long end = System.currentTimeMillis();
			System.out.println("get user follow forum cost time:" + (end - start));
			if(null != forumIds && forumIds.size() > 0)
			{
				///获取版块信息
				Map<Long, FeedForum> forumMap = new HashMap<Long, FeedForum>();
				for(long forumId : forumIds)
				{
					FeedForum forumInfo = forumService.getInfo(forumId);
					if(null != forumInfo)
						forumMap.put(forumId, forumInfo);
				}
				
				start = System.currentTimeMillis();
				Page<FeedThread> page = threadService.getForumEliteThreadList(forumIds, pageNum, pageSize);
				end = System.currentTimeMillis();
				System.out.println("get forum thread cost time:" + (end - start));
				
				JSONObject jsonThread = null;
				if(null != page)
				{
					total = page.getTotal();
					List<FeedThread> threads = page.getList();
					if(null != threads)
					{
						start = System.currentTimeMillis();
						for(FeedThread threadInfo : threads)
						{
							jsonThread = new JSONObject();
							jsonThread.put("tid", threadInfo.getThreadId());
							jsonThread.put("subject", threadInfo.getSubjectFilter());
							JSONObject jsonForum = new JSONObject();
							jsonForum.put("fid", threadInfo.getForumId());
							if(forumMap.containsKey(threadInfo.getForumId()))
							{
								FeedForum forumInfo = forumMap.get(threadInfo.getForumId());
								jsonForum.put("name", forumInfo.getName());
							}
							jsonThread.put("forum", jsonForum);
							jsonThread.put("create_time", threadInfo.getCreateTime());
							jsonThread.put("reply_cnt", threadInfo.getReplies());
							jsonThread.put("page_view", threadInfo.getPageView());
							arrayThreads.put(jsonThread);
						}
						end = System.currentTimeMillis();
						System.out.println("build json data cost time:" + (end - start));
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
			throw new Exception("at FeedThreadLogicImpl.getForumEliteThreadList throw an error.", e);
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
			JSONObject jsonThread = null;
			User userInfo = null;
			if(null != page)
			{
				total = page.getTotal();
				List<FeedThread> threads = page.getList();
				if(null != threads)
				{
					for(FeedThread threadInfo : threads)
					{
						jsonThread = new JSONObject();
						jsonThread.put("fid", threadInfo.getForumId());        ///所属版块ID
						jsonThread.put("tid", threadInfo.getThreadId());       ///主题ID
						jsonThread.put("user_id", threadInfo.getUserId());         ///发布主题的用户ID
						jsonThread.put("title", threadInfo.getSubjectFilter());       ///主题标题
						jsonThread.put("subject", threadInfo.getSubjectFilter());   ///主题标题
						jsonThread.put("replies", threadInfo.getReplies());         ///主题回复数(楼层+评论)
						jsonThread.put("pageview", threadInfo.getPageView());        ///主题浏览数
						jsonThread.put("share_times", threadInfo.getShareTimes());        ///主题分享数
						jsonThread.put("utime", threadInfo.getCreateTime() / 1000);        ///主题发布时间
						jsonThread.put("last_poster_id", threadInfo.getLastPostUid());        ///主题最后回复用户ID
						jsonThread.put("last_post_time", threadInfo.getLastPostTime() / 1000);        ///主题最后回复时间
						jsonThread.put("is_closed", threadInfo.isClosed());        ///主题是否关闭(如果关闭, 则不能进行回复和评论)
						jsonThread.put("recommends", threadInfo.getRecommends());        ///主题点赞数
						jsonThread.put("status", status);        ///主题状态
						
						///构建linkurl (linkurl是一种link规则，客户端根据link规则来进行跳转)
						String linkurl = threadInfo.getLinkUrl();
						if(StringUtil.isNullOrEmpty(linkurl))
							linkurl = GlobalConfig.FEED_DETAIL_URL + "?tid=" + threadInfo.getThreadId() + "&type=0";
						jsonThread.put("link_url", linkurl);
						
						///获取发布主题的用户信息
						userInfo = UserComponent.getInfoFromCache(threadInfo.getUserId());
						if(null == userInfo)
							uids.add(threadInfo.getUserId());
						else
						{
							jsonThread.put("nickname", userInfo.getNickName());
							jsonThread.put("avatar", userInfo.getAvatar());
							JSONObject jsonUser = new JSONObject();
							jsonUser.put("level", userInfo.getLevel());
							jsonUser.put("exp", userInfo.getExp());
							jsonUser.put("coin", userInfo.getCoin());
							jsonUser.put("diamond", userInfo.getDiamond());
							jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
							jsonUser.put("gained_exp", userInfo.getGainedExp());
							jsonUser.put("badge", userInfo.getBadges());
							jsonThread.put("user", jsonUser);
						}
						
						///获取最后回复主题的用户信息
						userInfo = UserComponent.getInfoFromCache(threadInfo.getLastPostUid());
						if(null == userInfo)
							uids.add(threadInfo.getLastPostUid());
						else
						{
							jsonThread.put("last_poster_name", userInfo.getNickName());
						}
						
						arrayThreads.put(jsonThread);
					}
				}
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
						String nickName = jsonThread.optString("nickname", "");
						String lastPosterName = jsonThread.optString("last_poster_name", "");
						long userId = jsonThread.optLong("uid", 0L);
						long lastPosterId = jsonThread.optLong("last_poster_id", 0L);
						
						///填充发帖用户信息
						if(StringUtil.isNullOrEmpty(nickName))
						{
							if(userMap.containsKey(userId))
							{
								userInfo = userMap.get(userId);
								jsonThread.put("nickname", userInfo.getNickName());
								jsonThread.put("avatar", userInfo.getAvatar());
								JSONObject jsonUser = new JSONObject();
								jsonUser.put("level", userInfo.getLevel());
								jsonUser.put("exp", userInfo.getExp());
								jsonUser.put("coin", userInfo.getCoin());
								jsonUser.put("diamond", userInfo.getDiamond());
								jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
								jsonUser.put("gained_exp", userInfo.getGainedExp());
								jsonUser.put("badge", userInfo.getBadges());
								jsonThread.put("user", jsonUser);
							}
						}
						///填充最后回复用户信息
						if(StringUtil.isNullOrEmpty(lastPosterName))
						{
							if(userMap.containsKey(lastPosterId))
							{
								jsonThread.put("last_poster_name", userInfo.getNickName());
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
			throw new Exception("at FeedThreadLogicImpl.getForumEliteThreadList throw an error.", e);
		}
	}

	private ResultValue formatForumThreads(long forumId, Page<FeedThread> page, long currentUserId) throws Exception
	{
		ResultValue result = new ResultValue();
		JSONObject data = new JSONObject();
		long total = 0;
		JSONArray arrayThreads = null;
		JSONObject jsonForum = new JSONObject();
		if(null != page)
		{
			total = page.getTotal();
			List<FeedThread> threads = page.getList();
			if(null != threads)
				arrayThreads = listToJSONArray(threads, currentUserId);
			else
				arrayThreads = new JSONArray();
		}
		
		///获取版块信息
		FeedForum forumInfo = forumService.getInfo(forumId);
		if(null != forumInfo)
		{
			jsonForum.put("total", total);
			jsonForum.put("icon", forumInfo.getIcon());
			jsonForum.put("name", forumInfo.getName());
			jsonForum.put("edit", forumInfo.isEdit() ? 1 : 0);
			jsonForum.put("users", "");
		}
		
		data.put("forum", jsonForum);
		data.put("threads", arrayThreads);
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		result.setData(data);
		return result;
	}
	
	private ResultValue formatUserThreads(Page<FeedThread> page) throws Exception
	{
		ResultValue result = new ResultValue();
		JSONObject data = new JSONObject();
		long total = 0;
		JSONArray arrayThreads = null;
		JSONObject jsonThread = new JSONObject();
		if(null != page)
		{
			total = page.getTotal();
			List<FeedThread> threads = page.getList();
			if(null != threads)
				arrayThreads = listToJSONArray(threads, 0L);
			else
				arrayThreads = new JSONArray();
		}
		
		jsonThread.put("total", total);
		data.put("thread", jsonThread);
		data.put("threadlist", arrayThreads);
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		result.setData(data);
		return result;
	}
	
	private JSONArray listToJSONArray(List<FeedThread> list, long currentUserId) throws Exception
	{
		///获取当前用户点赞的主题列表
		Set<String> recommendThreadIds = threadService.getUserRecommendThreadSet(currentUserId);
		///存储缓存中没有数据的用户ID, 用于批量获取用户信息
		Set<Long> uids = new HashSet<Long>();
		JSONArray arrayThreads = new JSONArray();
		JSONObject jsonThread = null;
		User userInfo = null;
		FeedPost postInfo = null;
		for(FeedThread threadInfo : list)
		{
			jsonThread = new JSONObject();
			jsonThread.put("fid", threadInfo.getForumId());        ///所属版块ID
			jsonThread.put("tid", threadInfo.getThreadId());       ///主题ID
			jsonThread.put("uid", threadInfo.getUserId());         ///发布主题的用户ID
			jsonThread.put("title", threadInfo.getSubjectFilter());       ///主题标题
			jsonThread.put("postcnt", threadInfo.getReplies());         ///主题回复数(楼层+评论)
			jsonThread.put("page_view", threadInfo.getPageView());        ///主题浏览数
			jsonThread.put("share_times", threadInfo.getShareTimes());        ///主题分享数
			jsonThread.put("utime", threadInfo.getCreateTime() / 1000);        ///主题发布时间
			jsonThread.put("last_poster_id", threadInfo.getLastPostUid());        ///主题最后回复用户ID
			jsonThread.put("last_post_time", threadInfo.getLastPostTime() / 1000);        ///主题最后回复时间
			//jsonThread.put("is_closed", threadInfo.isClosed());        ///主题是否关闭(如果关闭, 则不能进行回复和评论)
			jsonThread.put("state", threadInfo.isClosed());        ///主题是否关闭(如果关闭, 则不能进行回复和评论)
			jsonThread.put("recommends", threadInfo.getRecommends());        ///主题点赞数
			jsonThread.put("category", threadInfo.isTop() ? 1 : 0);        ///是否为置顶帖
			
			///构建linkurl (linkurl是一种link规则，客户端根据link规则来进行跳转)
			String linkurl = threadInfo.getLinkUrl();
			if(StringUtil.isNullOrEmpty(linkurl))
				linkurl = GlobalConfig.FEED_DETAIL_URL + "?tid=" + threadInfo.getThreadId() + "&type=0";
			jsonThread.put("link_url", linkurl);
			
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
			
			jsonThread.put("tags", tags);
			
			///判断是否点赞(主题的点赞是可以取消的)
			boolean isRecommend = false;
			if(null != recommendThreadIds)
				isRecommend = recommendThreadIds.contains(String.valueOf(threadInfo.getThreadId()));
			jsonThread.put("isrecommend", isRecommend);
			
			///判断是否为版主
			int roleId = userRoleService.getRoleId(threadInfo.getForumId(), threadInfo.getUserId());
			jsonThread.put("is_moderator", roleId > 0);
			
			///获取1楼信息
			postInfo = threadInfo.getPost();
			if(null != postInfo)
			{
				jsonThread.put("content", postInfo.getContentFilter());
				jsonThread.put("html_content", postInfo.getHtmlContentFilter());
				String pics = postInfo.getPictures();
				JSONArray jsonArrayPics = MiniTools.StringToJSONArray(pics);
				jsonThread.put("pic", jsonArrayPics);
				
				///获取视频信息
				if(threadInfo.isVideo())
				{
					JSONObject jsonVideo = new JSONObject();
					jsonVideo.put("id", postInfo.getVideoId());
					jsonVideo.put("duration", postInfo.getDuration());
					jsonVideo.put("thumbnail", postInfo.getThumbnail());
					jsonThread.put("video", jsonVideo);
				}
			}
			
			///获取发布主题的用户信息
			userInfo = UserComponent.getInfoFromCache(threadInfo.getUserId());
			if(null == userInfo)
				uids.add(threadInfo.getUserId());
			else
			{
				jsonThread.put("nickname", userInfo.getNickName());
				jsonThread.put("avatar", userInfo.getAvatar());
				JSONObject jsonUser = new JSONObject();
				jsonUser.put("level", userInfo.getLevel());
				jsonUser.put("exp", userInfo.getExp());
				jsonUser.put("coin", userInfo.getCoin());
				jsonUser.put("diamond", userInfo.getDiamond());
				jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
				jsonUser.put("gained_exp", userInfo.getGainedExp());
				jsonUser.put("badge", userInfo.getBadges());
				jsonThread.put("user", jsonUser);
			}
			
			///获取最后回复主题的用户信息
			userInfo = UserComponent.getInfoFromCache(threadInfo.getLastPostUid());
			if(null == userInfo)
				uids.add(threadInfo.getLastPostUid());
			else
			{
				jsonThread.put("last_poster_name", userInfo.getNickName());
			}
			
			///兼容老版本一些属性
			jsonThread.put("views", threadInfo.getPageView());
			jsonThread.put("isHot", 0);
			jsonThread.put("url", linkurl);
			jsonThread.put("dblastpost", threadInfo.getLastPostTime() / 1000);
			jsonThread.put("display_order", 0);
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
					String nickName = jsonThread.optString("nickname", "");
					String lastPosterName = jsonThread.optString("last_poster_name", "");
					long userId = jsonThread.optLong("uid", 0L);
					long lastPosterId = jsonThread.optLong("last_poster_id", 0L);
					
					///填充发帖用户信息
					if(StringUtil.isNullOrEmpty(nickName))
					{
						if(userMap.containsKey(userId))
						{
							userInfo = userMap.get(userId);
							jsonThread.put("nickname", userInfo.getNickName());
							jsonThread.put("avatar", userInfo.getAvatar());
							JSONObject jsonUser = new JSONObject();
							jsonUser.put("level", userInfo.getLevel());
							jsonUser.put("exp", userInfo.getExp());
							jsonUser.put("coin", userInfo.getCoin());
							jsonUser.put("diamond", userInfo.getDiamond());
							jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
							jsonUser.put("gained_exp", userInfo.getGainedExp());
							jsonUser.put("badge", userInfo.getBadges());
							jsonThread.put("user", jsonUser);
						}
					}
					///填充最后回复用户信息
					if(StringUtil.isNullOrEmpty(lastPosterName))
					{
						if(userMap.containsKey(lastPosterId))
						{
							jsonThread.put("last_poster_name", userInfo.getNickName());
						}
					}
				}
			}
		}
		return arrayThreads;
	}
}