package com.mofang.feed.logic.web.impl;

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
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.global.common.LimitConstants;
import com.mofang.feed.global.common.OperateBehavior;
import com.mofang.feed.global.common.OperateSourceType;
import com.mofang.feed.global.common.RecommendType;
import com.mofang.feed.global.common.ReplyType;
import com.mofang.feed.logic.web.FeedPostLogic;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.FeedRecommendNotify;
import com.mofang.feed.model.external.Game;
import com.mofang.feed.model.external.PostReplyNotify;
import com.mofang.feed.model.external.SensitiveWord;
import com.mofang.feed.model.external.User;
import com.mofang.feed.record.StatForumViewHistoryRecorder;
import com.mofang.feed.redis.FeedPostCache;
import com.mofang.feed.redis.FeedUserCountRedis;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.feed.redis.impl.FeedPostCacheImpl;
import com.mofang.feed.redis.impl.FeedUserCountRedisImpl;
import com.mofang.feed.redis.impl.WaterproofWallRedisImpl;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedBlackListService;
import com.mofang.feed.service.FeedCommentService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedOperateHistoryService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedSysRoleService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.FeedUserFavoriteService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedCommentServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysRoleServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.feed.service.impl.FeedUserFavoriteServiceImpl;
import com.mofang.feed.service.impl.task.FeedDifferenceThreadRepilyServiceImpl;
import com.mofang.feed.service.impl.task.FeedThreadRepliesRewardServiceImpl;
import com.mofang.feed.service.task.FeedDifferenceThreadRepilyService;
import com.mofang.feed.service.task.FeedThreadRepliesRewardService;
import com.mofang.feed.util.HtmlTagFilter;
import com.mofang.feed.util.MiniTools;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostLogicImpl implements FeedPostLogic
{
	private final static FeedPostLogicImpl LOGIC = new FeedPostLogicImpl();
	private WaterproofWallRedis waterproofWallRedis = WaterproofWallRedisImpl.getInstance();
	private FeedBlackListService blackListService = FeedBlackListServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedOperateHistoryService operateService = FeedOperateHistoryServiceImpl.getInstance();
	private FeedCommentService commentService = FeedCommentServiceImpl.getInstance();
	private FeedUserFavoriteService favoriteService = FeedUserFavoriteServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedSysRoleService roleService = FeedSysRoleServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private FeedThreadRepliesRewardService rewardService = FeedThreadRepliesRewardServiceImpl.getInstance();
	private FeedUserCountRedis userCountRedis = FeedUserCountRedisImpl.getInstance();
	private FeedDifferenceThreadRepilyService diffThreadPepilyService = FeedDifferenceThreadRepilyServiceImpl.getInstance();
	
	private FeedPostCache cache = FeedPostCacheImpl.getInstance();
	
	private FeedPostLogicImpl()
	{}
	
	public static FeedPostLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedPost model) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			String content = model.getContent();
			String htmlContent = model.getHtmlContent();
			long userId = model.getUserId();
			long threadId = model.getThreadId();
			
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
			
			if(content.length() > LimitConstants.THREAD_CONTENT_LENGTH)
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
			
			///验证主题是否存在
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			///检查主题是否已关闭
			if(threadInfo.isClosed())
			{
				result.setCode(ReturnCode.THREAD_HAS_CLOSED);
				result.setMessage(ReturnMessage.THREAD_HAS_CLOSED);
				return result;
			}
			///获取版块ID
			long forumId = threadInfo.getForumId();
			
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
			
			///完善Post实体对象
			model.setForumId(forumId);
			model.setContentFilter(contentFilter);
			model.setContentMark(contentMark);
			model.setHtmlContentFilter(htmlContentFilter);
			model.setHtmlContentMark(htmlContentMark);
			
			///保存楼层信息
			long postId = postService.add(model);
			
			/******************************执行任务******************************/
			TaskComponent.reply(userId);
			
			/******************************回复通知******************************/
			///非自己回复的需要发送通知
			if(userId != threadInfo.getUserId())
			{
				PostReplyNotify notify = new PostReplyNotify();
				notify.setUserId(threadInfo.getUserId());
				notify.setPostId(threadInfo.getThreadId());
				notify.setPostTitle(threadInfo.getSubjectFilter());
				notify.setReplyId(postId);
				notify.setReplyText(model.getContentFilter());
				notify.setReplyPictures(model.getPictures());
				notify.setReplyUserId(model.getUserId());
				notify.setReplyType(ReplyType.THREAD);
				notify.setForumId(forumId);
				FeedForum forumInfo = forumService.getInfo(forumId);
				if(null != forumInfo)
					notify.setForumName(forumInfo.getName());
				HttpComponent.pushPostReplyNotify(notify);
			}
			
			/******************************回复奖励******************************/
			rewardService.checkAndReword(threadId);
			
			/****************回复32个不同主题任务触发************************/
			diffThreadPepilyService.checkAndcallTask(userId, threadId);

			
			///获取楼层总数(用于跳转到用户回复的最新楼层)
			long posts = postService.getThreadPostCount(threadId);
			JSONObject data = new JSONObject();
			data.put("total", posts);
			data.put("post_id", postId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue edit(FeedPost model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			String content = model.getContent();
			String htmlContent = model.getHtmlContent();
			long postId = model.getPostId();
			
			///验证楼层是否存在
			FeedPost postInfo = postService.getInfo(postId, DataSource.REDIS);
			if(null == postInfo)
			{
				result.setCode(ReturnCode.POST_NOT_EXISTS);
				result.setMessage(ReturnMessage.POST_NOT_EXISTS);
				return result;
			}
			long userId = postInfo.getUserId();
			long threadId = postInfo.getThreadId();
			long forumId = postInfo.getForumId();
			
			///验证主题是否存在
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			///检查主题是否已关闭
			if(threadInfo.isClosed())
			{
				result.setCode(ReturnCode.THREAD_HAS_CLOSED);
				result.setMessage(ReturnMessage.THREAD_HAS_CLOSED);
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
			
			///权限检查
			long postUserId = postInfo.getUserId();
			long threadUserId = threadInfo.getUserId();
			if (operatorId != postUserId && operatorId != threadUserId)
			{
				boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.EDIT_POST);
				if(!hasPrivilege)
				{
					result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
					result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
					return result;
				}
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
			
			if(content.length() > LimitConstants.THREAD_CONTENT_LENGTH)
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
			
			///完善Post实体对象
			postInfo.setContentFilter(contentFilter);
			postInfo.setContentMark(contentMark);
			postInfo.setHtmlContentFilter(htmlContentFilter);
			postInfo.setHtmlContentMark(htmlContentMark);
			postInfo.setPictures(model.getPictures());
			postInfo.setVideoId(model.getVideoId());
			postInfo.setUpdateTime(System.currentTimeMillis());
			
			///保存楼层信息
			postService.edit(postInfo);
			
			///返回结果
			JSONObject data = new JSONObject();
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.edit throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long postId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///楼层有效性检查
			FeedPost postInfo = postService.getInfo(postId, DataSource.REDIS);
			if(null == postInfo)
			{
				result.setCode(ReturnCode.POST_NOT_EXISTS);
				result.setMessage(ReturnMessage.POST_NOT_EXISTS);
				return result;
			}
			
			///主题有效性检查
			long threadId = postInfo.getThreadId();
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			///权限检查
			long forumId = postInfo.getForumId();
			long postUserId = postInfo.getUserId();
			long threadUserId = threadInfo.getUserId();
			if(operatorId != postUserId && operatorId != threadUserId)  ///如果不是作者并且不是楼主
			{
				boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.DELETE_POST);
				if(!hasPrivilege)
				{
					result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
					result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
					return result;
				}
			}
			
			///如果删除的是一楼，则将主题删除
			String subject = "";
			subject = threadInfo.getSubject();
			if(postInfo.getPosition() == 1)
				threadService.delete(threadInfo);
			
			///删除楼层
			postService.delete(postInfo);
			
			/******************************系统通知******************************/
			///不是自己删帖才需要发通知
			if(operatorId != postUserId)
				SysMessageNotifyComponent.deletePost(operatorId, postUserId, subject, postInfo.getContentFilter(), reason);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(postUserId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.DELETE_POST);
			operateInfo.setSourceType(OperateSourceType.POST);
			operateInfo.setSourceId(postId);
			operateInfo.setOperateBehavior(OperateBehavior.DELETE_POST);
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
			throw new Exception("at FeedPostLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue recommend(long userId, long recommendUserId, long postId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///楼层有效性检查
			FeedPost postInfo = postService.getInfo(postId, DataSource.REDIS);
			if(null == postInfo)
			{
				result.setCode(ReturnCode.POST_NOT_EXISTS);
				result.setMessage(ReturnMessage.POST_NOT_EXISTS);
				return result;
			}
			
			boolean exists = postService.existsRecommend(userId, postId);
			if(!exists)
			{
				///设置楼层点赞
				postService.setRecommend(userId, postId);
				
				/******************************点赞通知******************************/
				FeedThread threadInfo = threadService.getInfo(postInfo.getThreadId(), DataSource.REDIS);
				if(null != threadInfo)
				{
					FeedRecommendNotify notify = new FeedRecommendNotify();
					notify.setUserId(recommendUserId);
					notify.setThreadId(postInfo.getThreadId());
					notify.setSubject(threadInfo.getSubjectFilter());
					notify.setRecommendType(RecommendType.POST);
					notify.setRecommendUserId(userId);
					notify.setPostId(postId);
					notify.setPosition(postService.getRank(postInfo.getThreadId(), postId));
					notify.setForumId(threadInfo.getForumId());
					FeedForum forumInfo = forumService.getInfo(threadInfo.getForumId());
					if(null != forumInfo)
						notify.setForumName(forumInfo.getName());
					HttpComponent.pushFeedRecommendNotify(notify);
				}
				
				/******************************执行任务******************************/
				TaskComponent.recommendThread(userId);
			}
			else
			{
				///取消楼层点赞
				postService.cancelRecommend(userId, postId);
			}
				
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.recommend throw an error.", e);
		}
	}

	@Override
	public ResultValue getInfo(long postId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			FeedPost postInfo = postService.getInfo(postId, DataSource.REDIS);
			if(null == postInfo)
			{
				result.setCode(ReturnCode.POST_NOT_EXISTS);
				result.setMessage(ReturnMessage.POST_NOT_EXISTS);
				return result;
			}
			
			JSONObject data = new JSONObject();
			data.put("pid", postInfo.getPostId());          ///楼层ID
			data.put("content", postInfo.getContentFilter());      ///楼层内容
			data.put("html_content", postInfo.getHtmlContentFilter());    ///楼层HTML内容
			data.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));     ///楼层图片
			data.put("position", postInfo.getPosition());     ///楼层数
			data.put("create_time", postInfo.getCreateTime());      ///创建时间
			
			///版块信息
			JSONObject jsonForum = new JSONObject();
			jsonForum.put("fid", postInfo.getForumId());
			
			///主题信息
			JSONObject jsonThread = new JSONObject();
			jsonThread.put("tid", postInfo.getThreadId());
			
			///用户信息
			JSONObject jsonUser = new JSONObject();
			jsonUser.put("user_id", postInfo.getUserId());
			User userInfo = UserComponent.getInfo(postInfo.getUserId());
			if(null != userInfo)
			{
				jsonUser.put("nickname", userInfo.getNickName());
				jsonUser.put("avatar", userInfo.getAvatar());
			}
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.getInfo throw an error.", e);
		}
	}

	@Override
	public ResultValue getThreadPostList(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		ResultValue result = new ResultValue();
		
		///获取缓存中获取
		String cacheResult = cache.getThreadPostList(threadId, pageNum, pageSize);
		JSONObject data = null;
		if(!StringUtil.isNullOrEmpty(cacheResult))
		{
			data = new JSONObject(cacheResult);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		
		///验证主题是否存在
		FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
		if(null == threadInfo)
		{
			result.setCode(ReturnCode.THREAD_NOT_EXISTS);
			result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
			return result;
		}
		
		Page<FeedPost> page = postService.getThreadPostList(threadId, pageNum, pageSize);
		result = getPostList(page, threadId, pageNum, pageSize, currentUserId);
		
		///保存到缓存中
		data = (JSONObject)result.getData();
		cache.setThreadPostList(threadId, pageNum, pageSize, data.toString());
		
		/*********记录用户浏览数**********/
		StatForumViewHistoryRecorder.recordInPostLogic(threadId, currentUserId);
		
		return result;
	}
	
	@Override
	public ResultValue getThreadPostList(long threadId, int pageNum, int pageSize, Set<Long> userIds, boolean include, boolean sort) throws Exception 
	{
		try
		{
			///验证主题是否存在
			ResultValue result = new ResultValue();
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null == threadInfo)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			Page<FeedPost> page = postService.getThreadPostList(threadId, pageNum, pageSize, userIds, include, sort);
			ResultValue resultValue = getPostList(page, threadId, pageNum, pageSize, 0);
			
			return resultValue;
		} 
		catch (Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.getThreadPostList by userIds throw an error.", e);
		}
	}

	@Override
	public ResultValue getHostPostList(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		ResultValue result = new ResultValue();
		///验证主题是否存在
		FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
		if(null == threadInfo)
		{
			result.setCode(ReturnCode.THREAD_NOT_EXISTS);
			result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
			return result;
		}
		
		///获取缓存中获取
		String cacheResult = cache.getHostPostList(threadId, threadInfo.getUserId(), pageNum, pageSize);
		JSONObject data = null;
		if(!StringUtil.isNullOrEmpty(cacheResult))
		{
			data = new JSONObject(cacheResult);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
			
		Page<FeedPost> page = postService.getHostPostList(threadId, threadInfo.getUserId(), pageNum, pageSize);
		result = getPostList(page, threadId, pageNum, pageSize, currentUserId);
		
		///保存到缓存中
		data = (JSONObject)result.getData();
		cache.setHostPostList(threadId, threadInfo.getUserId(), pageNum, pageSize, data.toString());
		
		/*********记录用户浏览数**********/
		StatForumViewHistoryRecorder.recordInPostLogic(threadId, currentUserId);
		
		return result;
	}

	private ResultValue getPostList(Page<FeedPost> page, long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayPosts = new JSONArray();
			JSONObject jsonThread = new JSONObject();
			if(null != page)
			{
				total = page.getTotal();
				List<FeedPost> posts = page.getList();
				if(null != posts)
				{
					///获取当前用户点赞的楼层列表
					Set<String> recommendPostIds = postService.getUserRecommendPostSet(currentUserId);
					///存储缓存中没有数据的用户ID, 用于批量获取用户信息
					Set<Long> uids = new HashSet<Long>();
					JSONObject jsonPost = null;
					JSONObject jsonForum = null;
					JSONObject jsonUser = null;
					User userInfo = null;
					long userId = 0L;
					for(FeedPost postInfo : posts)
					{
						userId = postInfo.getUserId();
						jsonPost = new JSONObject();
						jsonPost.put("pid", postInfo.getPostId());
						jsonPost.put("content", postInfo.getContentFilter());
						jsonPost.put("html_content", postInfo.getHtmlContentFilter());
						jsonPost.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
						jsonPost.put("recommends", postInfo.getRecommends());
						jsonPost.put("comments", postInfo.getComments());
						jsonPost.put("position", postInfo.getPosition());
						jsonPost.put("create_time", postInfo.getCreateTime());
						jsonPost.put("update_time", postInfo.getUpdateTime());
						
						///是否点赞
						boolean isRecommend = false;
						if(null != recommendPostIds)
							isRecommend = recommendPostIds.contains(String.valueOf(postInfo.getPostId()));
						jsonPost.put("is_recommend", isRecommend);
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", postInfo.getForumId());
						jsonPost.put("forum", jsonForum);
						
						///获取楼层用户信息
						jsonUser = new JSONObject();
						jsonUser.put("user_id", postInfo.getUserId());
						
						userInfo = UserComponent.getInfoFromCache(userId);
						if(null == userInfo)
							uids.add(postInfo.getUserId());
						else
						{
							jsonUser.put("nickname", userInfo.getNickName());
							jsonUser.put("avatar", userInfo.getAvatar());
							jsonUser.put("level", userInfo.getLevel());
						}
						jsonPost.put("user", jsonUser);
						
						///获取楼层评论
						JSONArray arrayComments = buildPostCommentList(postInfo.getPostId());
						if(null == arrayComments)
							arrayComments = new JSONArray();
						jsonPost.put("comment_list", arrayComments);
						arrayPosts.put(jsonPost);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
							fillPostUser(arrayPosts, userMap);
					}
				}
			}
			
			///获取主题
			FeedThread threadInfo = threadService.getFullInfo(threadId);
			jsonThread = buildThreadJSONObject(threadInfo, currentUserId);
			if(null == jsonThread)
				jsonThread = new JSONObject();
			
			///获取当前用户角色权限
			JSONObject jsonCurrentUser = new JSONObject();
			boolean isAdmin = adminService.exists(currentUserId);
			int roleId = userRoleService.getRoleId(threadInfo.getForumId(), currentUserId);
			boolean isModerator = false;
			JSONArray arrayPrivileges = new JSONArray();
			if(roleId > 0)
			{
				FeedSysRole roleInfo = roleService.getInfo(roleId);
				if(null != roleInfo)
				{
					isModerator = true;
					arrayPrivileges = MiniTools.StringToJSONArray(roleInfo.getPrivileges());
				}
			}
			jsonCurrentUser.put("is_admin", isAdmin);
			jsonCurrentUser.put("is_moderator", isModerator);
			jsonCurrentUser.put("privileges", arrayPrivileges);

			data.put("thread", jsonThread);
			data.put("total", total);
			data.put("posts", arrayPosts);
			data.put("current_user", jsonCurrentUser);
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.getPostList throw an error.", e);
		}
	}
	
	private JSONObject buildThreadJSONObject(FeedThread threadInfo, long currentUserId) throws Exception
	{
		JSONObject jsonThread = new JSONObject();
		jsonThread.put("tid", threadInfo.getThreadId());
		jsonThread.put("subject", threadInfo.getSubjectFilter());
		jsonThread.put("replies", threadInfo.getReplies());
		jsonThread.put("recommends", threadInfo.getRecommends());
		jsonThread.put("page_view", threadInfo.getPageView());
		jsonThread.put("create_time", threadInfo.getCreateTime());
		jsonThread.put("update_time", threadInfo.getUpdateTime());
		jsonThread.put("is_elite", threadInfo.isElite());
		jsonThread.put("is_top", threadInfo.isTop());
		jsonThread.put("is_closed", threadInfo.isClosed());
		
		///判断是否已对该主题点赞
		boolean isRecommend = threadService.existsRecommend(currentUserId, threadInfo.getThreadId());
		jsonThread.put("is_recommend", isRecommend);
		
		///判断是否已收藏该主题
		boolean isFavorite = favoriteService.exists(currentUserId, threadInfo.getThreadId());
		jsonThread.put("is_fav", isFavorite);
		
		JSONObject jsonForum = new JSONObject();
		jsonForum.put("fid", threadInfo.getForumId());
		jsonThread.put("forum", jsonForum);
		FeedForum forumInfo = forumService.getInfo(threadInfo.getForumId());
		if(null != forumInfo)
		{
			jsonForum.put("name", forumInfo.getName());
			int gameId = forumInfo.getGameId();
			Game gameInfo = HttpComponent.getGameInfo(gameId);
			if(null != gameInfo)
			{
				jsonForum.put("game_name", gameInfo.getName());
			}
		}
		
		///获取主题用户信息
		JSONObject jsonUser = new JSONObject();
		jsonUser.put("user_id", threadInfo.getUserId());
		User threadUserInfo = UserComponent.getInfo(threadInfo.getUserId());
		if(null != threadUserInfo)
		{
			jsonUser = buildUserJSONObject(threadUserInfo);
//			long threadCount = threadService.getUserThreadCount(threadInfo.getUserId());
//			long eliteThreadCount = threadService.getUserEliteThreadCount(threadInfo.getUserId());
//			long postCount = postService.getUserPostCount(threadInfo.getUserId());
//			long commentCount = commentService.getUserCommentCount(threadInfo.getUserId());
//			jsonUser.put("threads", threadCount);
//			jsonUser.put("replies", postCount + commentCount);
//			jsonUser.put("elite_threads", eliteThreadCount);
			
			
			//先从2分钟缓存里取，如果没有，从数据库加载并缓存2分钟
			String countInfo = userCountRedis.userCountInfo(threadInfo.getUserId());
			if(StringUtil.isNullOrEmpty(countInfo)) {
				long threadCount = threadService.getUserThreadCount(threadInfo.getUserId());
				long eliteThreadCount = threadService.getUserEliteThreadCount(threadInfo.getUserId());
				long postCount = postService.getUserPostCount(threadInfo.getUserId());
				long commentCount = commentService.getUserCommentCount(threadInfo.getUserId());
				
				jsonUser.put("threads", threadCount);
				jsonUser.put("replies", postCount + commentCount);
				jsonUser.put("elite_threads", eliteThreadCount);
				
				userCountRedis.saveAndExpire(threadInfo.getUserId(), threadCount, postCount, commentCount, eliteThreadCount);
			} else {
				JSONObject jsonCountInfo = new JSONObject(countInfo);
				jsonUser.put("threads", jsonCountInfo.optLong("threads", 0l));
				jsonUser.put("replies",  jsonCountInfo.optLong("replies", 0l));
				jsonUser.put("elite_threads", jsonCountInfo.optLong("elite_threads", 0l));
			}
			
		
		}
		jsonThread.put("forum", jsonForum);
		jsonThread.put("user", jsonUser);
		return jsonThread;
	}
	
	private JSONArray buildPostCommentList(long postId) throws Exception
	{
		JSONArray arrayComments = new JSONArray();
		Page<FeedComment> pageComments = commentService.getPostCommentList(postId, 1, 10);
		if(null != pageComments)
		{
			List<FeedComment> comments = pageComments.getList();
			if(null != comments && comments.size() > 0)
			{
				JSONObject jsonComment = null;
				JSONObject jsonPost = null;
				JSONObject jsonUser = null;
				for(FeedComment commentInfo : comments)
				{
					jsonComment = new JSONObject();
					jsonComment.put("cid", commentInfo.getCommentId());
					jsonComment.put("content", commentInfo.getContentFilter());
					jsonComment.put("create_time", commentInfo.getCreateTime());
					
					jsonPost = new JSONObject();
					jsonPost.put("pid", commentInfo.getPostId());
					
					jsonUser = new JSONObject();
					jsonUser.put("user_id", commentInfo.getUserId());
					///获取评论用户信息
					User userInfo = UserComponent.getInfo(commentInfo.getUserId());
					if(null != userInfo)
						jsonUser = buildUserJSONObject(userInfo);
					
					jsonComment.put("post", jsonPost);
					jsonComment.put("user", jsonUser);
					arrayComments.put(jsonComment);
				}
			}
		}
		return arrayComments;
	}
	
	private JSONObject buildUserJSONObject(User userInfo) throws Exception
	{
		JSONObject jsonUser = new JSONObject();
		jsonUser.put("user_id", userInfo.getUserId());
		jsonUser.put("nickname", userInfo.getNickName());
		jsonUser.put("avatar", userInfo.getAvatar());
		jsonUser.put("level", userInfo.getLevel());
		jsonUser.put("coin", userInfo.getCoin());
		return jsonUser;
	}
	
	private void fillPostUser(JSONArray arrayPosts, Map<Long, User> userMap) throws Exception
	{
		JSONObject jsonPost = null;
		JSONObject jsonUser = null;
		long userId = 0L;
		String nickName = "";
		User userInfo = null;
		for(int i=0; i<arrayPosts.length(); i++)
		{
			jsonPost = arrayPosts.getJSONObject(i);
			jsonUser = jsonPost.optJSONObject("user");
			nickName = jsonUser.optString("nickname", "");
			userId = jsonUser.optLong("user_id", 0L);
			
			///填充发表楼层用户信息
			if(StringUtil.isNullOrEmpty(nickName))
			{
				if(userMap.containsKey(userId))
				{
					userInfo = userMap.get(userId);
					jsonUser.put("nickname", userInfo.getNickName());
					jsonUser.put("avatar", userInfo.getAvatar());
					jsonUser.put("level", userInfo.getLevel());
				}
			}
		}
	}

}