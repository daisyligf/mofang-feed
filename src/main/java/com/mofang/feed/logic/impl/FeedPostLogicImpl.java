package com.mofang.feed.logic.impl;

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
import com.mofang.feed.global.common.OperateBehavior;
import com.mofang.feed.global.common.OperateSourceType;
import com.mofang.feed.global.common.ReplyType;
import com.mofang.feed.global.common.RequestFrom;
import com.mofang.feed.global.common.ThreadTag;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.feed.logic.FeedPostLogic;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedPostAndComment;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.PostReplyNotify;
import com.mofang.feed.model.external.SensitiveWord;
import com.mofang.feed.model.external.User;
import com.mofang.feed.redis.WaterproofWallRedis;
import com.mofang.feed.redis.impl.WaterproofWallRedisImpl;
import com.mofang.feed.service.FeedBlackListService;
import com.mofang.feed.service.FeedCommentService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedOperateHistoryService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.FeedUserFavoriteService;
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedCommentServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.feed.service.impl.FeedUserFavoriteServiceImpl;
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
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
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
			PostReplyNotify notify = new PostReplyNotify();
			notify.setUserId(threadInfo.getUserId());
			notify.setPostId(threadInfo.getThreadId());
			notify.setPostTitle(threadInfo.getSubjectFilter());
			notify.setReplyId(postId);
			notify.setReplyText(model.getContentFilter());
			notify.setReplyPictures(model.getPictures());
			notify.setReplyUserId(model.getUserId());
			notify.setReplyType(ReplyType.THREAD);
			HttpComponent.pushPostReplyNotify(notify);
			
			///构建返回结果
			JSONObject data = new JSONObject();
			data.put("tid", threadId);
			data.put("pid", postId);
			data.put("uid", userId);
			data.put("replys", new JSONArray());
			data.put("pic", MiniTools.StringToJSONArray(model.getPictures()));
			data.put("ctime", model.getCreateTime());
			data.put("content", model.getContentFilter());
			data.put("html_content", model.getHtmlContentFilter());
			data.put("floor", model.getPosition());
			data.put("replycnt", model.getComments());
			
			///老版本一些属性
			data.put("voice", "");
			data.put("duration", "");
			data.put("userId", 0);
			
			///获取用户信息
			User userInfo = UserComponent.getInfo(userId);
			if(null != userInfo)
			{
				data.put("nickname", userInfo.getNickName());
				data.put("avatar", userInfo.getAvatar());
				data.put("level", userInfo.getLevel());
				data.put("coin", userInfo.getCoin());
				data.put("diamond", userInfo.getDiamond());
				data.put("exp", userInfo.getExp());
				data.put("upgrade_exp", userInfo.getUpgradeExp());
				data.put("gained_exp", userInfo.getGainedExp());
				data.put("badge", userInfo.getBadges());
				
				JSONObject jsonUser = new JSONObject();
				jsonUser.put("nickname", userInfo.getNickName());
				jsonUser.put("avatar", userInfo.getAvatar());
				jsonUser.put("level", userInfo.getLevel());
				jsonUser.put("coin", userInfo.getCoin());
				jsonUser.put("diamond", userInfo.getDiamond());
				jsonUser.put("exp", userInfo.getExp());
				jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
				jsonUser.put("gained_exp", userInfo.getGainedExp());
				jsonUser.put("badge", userInfo.getBadges());
				data.put("user", jsonUser);
			}
			
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.EDIT_THREAD);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
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
			///权限检查
			long forumId = postInfo.getForumId();
			long userId = postInfo.getUserId();
			long threadId = postInfo.getThreadId();
			if(operatorId != userId)  ///如果不是作者
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
			FeedThread threadInfo = threadService.getInfo(threadId, DataSource.REDIS);
			if(null != threadInfo)
			{
				subject = threadInfo.getSubject();
				if(postInfo.getPosition() == 1)
					threadService.delete(threadInfo);
			}
			
			///删除楼层
			postService.delete(postInfo);
			
			/******************************系统通知******************************/
			///不是自己删帖才需要发通知
			if(operatorId != userId)
				SysMessageNotifyComponent.deletePost(operatorId, userId, subject, postInfo.getContentFilter(), reason);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
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
	public ResultValue restore(long postId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///楼层有效性检查
			FeedPost postInfo = postService.getInfo(postId, DataSource.MYSQL);
			if(null == postInfo)
			{
				result.setCode(ReturnCode.POST_NOT_EXISTS);
				result.setMessage(ReturnMessage.POST_NOT_EXISTS);
				return result;
			}
			///权限检查
			long forumId = postInfo.getForumId();
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.RESTORE_POST);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///还原楼层
			postService.restore(postInfo);
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.restore throw an error.", e);
		}
	}

	@Override
	public ResultValue remove(long postId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///楼层有效性检查
			FeedPost postInfo = postService.getInfo(postId, DataSource.MYSQL);
			if(null == postInfo)
			{
				result.setCode(ReturnCode.POST_NOT_EXISTS);
				result.setMessage(ReturnMessage.POST_NOT_EXISTS);
				return result;
			}
			///权限检查
			long forumId = postInfo.getForumId();
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.REMOVE_POST);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除楼层(从回收站移除)
			postService.remove(postInfo);
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.remove throw an error.", e);
		}
	}

	@Override
	public ResultValue recommend(long userId, long postId) throws Exception
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
			
			///设置楼层点赞
			postService.recommend(userId, postId);
				
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
			data.put("fid", postInfo.getForumId());        ///所属版块ID
			data.put("tid", postInfo.getThreadId());       ///主题ID
			data.put("pid", postInfo.getPostId());          ///楼层ID
			data.put("uid", postInfo.getUserId());         ///发布楼层的用户ID
			data.put("content", postInfo.getContentFilter());      ///楼层内容
			data.put("html_content", postInfo.getHtmlContentFilter());    ///楼层HTML内容
			data.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));     ///楼层图片
			data.put("floor", postInfo.getPosition());     ///楼层数
			data.put("ctime", postInfo.getCreateTime());      ///创建时间
			
			///获取用户信息
			User userInfo = UserComponent.getInfo(postInfo.getUserId());
			if(null != userInfo)
			{
				data.put("nickname", userInfo.getNickName());
				data.put("avatar", userInfo.getAvatar());
				data.put("level", userInfo.getLevel());
				data.put("coin", userInfo.getCoin());
				data.put("diamond", userInfo.getDiamond());
				data.put("exp", userInfo.getExp());
				data.put("upgrade_exp", userInfo.getUpgradeExp());
				data.put("gained_exp", userInfo.getGainedExp());
				data.put("badge", userInfo.getBadges());
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
	public ResultValue getPostList(long threadId, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			long total = 0;
			User userInfo = null;
			FeedThread threadInfo = null;
			JSONArray arrayPosts =new JSONArray();
			Page<FeedPost> page = postService.getPostList(threadId, status, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedPost> posts = page.getList();
				if(null != posts)
				{
					JSONObject jsonPost = null;
					for(FeedPost postInfo : posts)
					{
						jsonPost = new JSONObject();
						jsonPost.put("fid", postInfo.getForumId());        ///所属版块ID
						jsonPost.put("tid", postInfo.getThreadId());       ///主题ID
						jsonPost.put("pid", postInfo.getPostId());          ///楼层ID
						jsonPost.put("user_id", postInfo.getUserId());         ///发布楼层的用户ID
						jsonPost.put("position", postInfo.getPosition());       ///楼层数
						jsonPost.put("original_message", postInfo.getContent());
						jsonPost.put("message", postInfo.getContent());
						jsonPost.put("html_message", postInfo.getHtmlContentFilter());
						jsonPost.put("original_html_message", postInfo.getHtmlContent());
						jsonPost.put("createtime", postInfo.getCreateTime() / 1000);
						jsonPost.put("isexistvideo", postInfo.getVideoId() > 0 ? 1 : 0);
						jsonPost.put("video_id", postInfo.getVideoId());
						jsonPost.put("duration", postInfo.getDuration());
						jsonPost.put("thumbnail", postInfo.getThumbnail());
						jsonPost.put("status", postInfo.getStatus());
						jsonPost.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
						jsonPost.put("start", postInfo.getPosition() == 1 ? 1 : 0);
						jsonPost.put("replycnt", postInfo.getComments());
						jsonPost.put("comments", postInfo.getComments());
						jsonPost.put("post_time", postInfo.getCreateTime() / 1000);
						jsonPost.put("recommends", postInfo.getRecommends());
						jsonPost.put("iscomment", 0);
						
						threadInfo = threadService.getInfo(postInfo.getThreadId(), DataSource.REDIS);
						if(null != threadInfo)
							jsonPost.put("thread_subject", threadInfo.getSubjectFilter());
						
						userInfo = UserComponent.getInfoFromCache(postInfo.getUserId());
						if(null == userInfo)
							uids.add(postInfo.getUserId());
						else
							jsonPost.put("nickname", userInfo.getNickName());
						
						arrayPosts.put(jsonPost);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayPosts.length(); i++)
							{
								jsonPost = arrayPosts.getJSONObject(i);
								String nickName = jsonPost.optString("nickname", "");
								long userId = jsonPost.optLong("uid", 0L);
								
								///填充发帖用户信息
								if(StringUtil.isNullOrEmpty(nickName))
								{
									if(userMap.containsKey(userId))
									{
										userInfo = userMap.get(userId);
										jsonPost.put("nickname", userInfo.getNickName());
									}
								}
							}
						}
					}
				}
			}
			data.put("total", total);
			data.put("list", arrayPosts);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedThreadLogicImpl.getPostList throw an error.", e);
		}
	}

	@Override
	public ResultValue getThreadPostList(long threadId, int pageNum, int pageSize, long currentUserId, RequestFrom from) throws Exception
	{
		if(from == RequestFrom.APP)
			return getThreadPostListByApp(threadId, pageNum, pageSize, currentUserId);
		else if(from == RequestFrom.WEB)
			return getThreadPostListByWeb(threadId, pageNum, pageSize, currentUserId);
		
		ResultValue result = new ResultValue();
		result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
		result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
		return result;
	}

	@Override
	public ResultValue getHostPostList(long threadId, int pageNum, int pageSize, long currentUserId, RequestFrom from) throws Exception
	{
		if(from == RequestFrom.APP)
			return getHostPostListByApp(threadId, pageNum, pageSize, currentUserId);
		else if(from == RequestFrom.WEB)
			return getHostPostListByWeb(threadId, pageNum, pageSize, currentUserId);
		
		ResultValue result = new ResultValue();
		result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
		result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
		return result;
	}

	@Override
	public ResultValue getUserPostList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayItems = new JSONArray();
			JSONObject jsonItem = null;
			long total = 0;
			Page<FeedPost> page = postService.getUserPostList(userId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedPost> posts = page.getList();
				if(null != posts)
				{
					JSONObject jsonPost = null;
					JSONObject jsonThread = null;
					User userInfo = null;
					for(FeedPost postInfo : posts)
					{
						jsonItem = new JSONObject();
						jsonPost = new JSONObject();
						jsonPost.put("fid", postInfo.getForumId());
						jsonPost.put("pid", postInfo.getPostId());
						jsonPost.put("user_id", userId);
						jsonPost.put("start", (postInfo.getPosition() == 1 ? 1 : 0));
						jsonPost.put("message", postInfo.getContentFilter());
						jsonPost.put("html_content", postInfo.getHtmlContentFilter());
						jsonPost.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
						jsonPost.put("post_time", postInfo.getCreateTime() / 1000);
						
						jsonThread = new JSONObject();
						FeedThread threadInfo = threadService.getFullInfo(postInfo.getThreadId());
						if(null != threadInfo)
						{
							jsonThread.put("fid", threadInfo.getForumId());
							jsonThread.put("tid", threadInfo.getThreadId());
							jsonThread.put("user_id", threadInfo.getUserId());
							jsonThread.put("subject", threadInfo.getSubjectFilter());
							jsonThread.put("recommends", threadInfo.getRecommends());
							jsonThread.put("page_view", threadInfo.getPageView());
							jsonThread.put("replies", threadInfo.getReplies());
							jsonThread.put("share_times", threadInfo.getShareTimes());
							jsonThread.put("is_closed", threadInfo.isClosed() ? 1 : 0);
							jsonThread.put("end_post_time", threadInfo.getLastPostTime() / 1000);
							jsonThread.put("type", "0");
							jsonThread.put("last_poster_id", threadInfo.getLastPostUid());
							jsonThread.put("create_time", threadInfo.getCreateTime() / 1000);
							jsonThread.put("display_order", 0);
							
							///获取用户信息
							userInfo = UserComponent.getInfo(threadInfo.getUserId());
							if(null != userInfo)
								jsonThread.put("nickname", userInfo.getNickName());
							
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
							
							if(null != threadInfo.getPost())
							{
								if(threadInfo.isVideo())
								{
									jsonThread.put("video_id", threadInfo.getPost().getVideoId());
									jsonThread.put("duration", threadInfo.getPost().getDuration());
									jsonThread.put("thumbnail", threadInfo.getPost().getThumbnail());
								}
								
								jsonThread.put("pic", MiniTools.StringToJSONArray(threadInfo.getPost().getPictures()));
								jsonThread.put("content", threadInfo.getPost().getContentFilter());
								jsonThread.put("html_content", threadInfo.getPost().getHtmlContentFilter());
							}
						}
						
						jsonItem.put("floor", postInfo);
						jsonItem.put("thread", threadInfo);
						arrayItems.put(jsonItem);
					}
				}
			}

			data.put("total", total);
			data.put("list", arrayItems);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.getUserPostList throw an error.", e);
		}
	}

	@Override
	public ResultValue getUserReplyList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayItems = new JSONArray();
			JSONObject jsonItem = null;
			long total = 0;
			Page<FeedPostAndComment> page = postService.getUserReplyList(userId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedPostAndComment> replies = page.getList();
				if(null != replies)
				{
					JSONObject jsonThread = null;
					JSONObject jsonForum = null;
					for(FeedPostAndComment postAndCommentInfo : replies)
					{
						jsonItem = new JSONObject();
						jsonThread = new JSONObject();
						jsonForum = new JSONObject();
						
						jsonThread.put("tid", postAndCommentInfo.getThreadId());
						jsonThread.put("subject", postAndCommentInfo.getSubject());
						jsonForum.put("fid", postAndCommentInfo.getForumId());
						jsonForum.put("name", postAndCommentInfo.getForumName());
						
						jsonItem.put("thread", jsonThread);
						jsonItem.put("forum", jsonForum);
						jsonItem.put("pid", postAndCommentInfo.getPostId());
						jsonItem.put("position", postAndCommentInfo.getPosition());
						jsonItem.put("reply_content", postAndCommentInfo.getReplyContent());
						jsonItem.put("reply_time", postAndCommentInfo.getReplyTime());
						arrayItems.put(jsonItem);
					}
				}
			}

			data.put("total", total);
			data.put("list", arrayItems);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.getUserReplyList throw an error.", e);
		}
	}
	
	private ResultValue getThreadPostListByApp(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		Page<FeedPost> page = postService.getThreadPostList(threadId, pageNum, pageSize);
		return getPostListByApp(page, threadId, pageNum, pageSize, currentUserId);
	}
	
	private ResultValue getThreadPostListByWeb(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		Page<FeedPost> page = postService.getThreadPostList(threadId, pageNum, pageSize);
		return getPostListByWeb(page, threadId, pageNum, pageSize, currentUserId);
	}
	
	private ResultValue getHostPostListByApp(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		Page<FeedPost> page = postService.getHostPostList(threadId, pageNum, pageSize);
		return getPostListByApp(page, threadId, pageNum, pageSize, currentUserId);
	}
	
	private ResultValue getHostPostListByWeb(long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		Page<FeedPost> page = postService.getHostPostList(threadId, pageNum, pageSize);
		return getPostListByWeb(page, threadId, pageNum, pageSize, currentUserId);
	}
	
	private ResultValue getPostListByApp(Page<FeedPost> page, long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayPosts = new JSONArray();
			JSONArray arrayHotPosts = new JSONArray();
			
			///获取主题
			FeedThread threadInfo = threadService.getFullInfo(threadId);
			JSONObject jsonThread = buildThreadJSONObject(threadInfo, currentUserId);
			if(null == jsonThread)
			{
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
			}
			
			jsonThread.put("total", total);
			///获取主题相关视频列表
			JSONArray arrayRelationVideoes = buildRelationVideoList(threadInfo.getForumId(), currentUserId);
			if(null == arrayRelationVideoes)
				arrayRelationVideoes = new JSONArray();
			jsonThread.put("related_videos", arrayRelationVideoes);
			
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
					User userInfo = null;
					long userId = 0L;
					for(FeedPost postInfo : posts)
					{
						userId = postInfo.getUserId();
						jsonPost = new JSONObject();
						jsonPost.put("fid", postInfo.getForumId());
						jsonPost.put("pid", postInfo.getPostId());
						jsonPost.put("uid", userId);
						jsonPost.put("content", postInfo.getContentFilter());
						jsonPost.put("html_content", postInfo.getHtmlContentFilter());
						jsonPost.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
						jsonPost.put("support_cnt", postInfo.getRecommends());
						jsonPost.put("replycnt", postInfo.getComments());
						jsonPost.put("floor", postInfo.getPosition());
						jsonPost.put("ctime", postInfo.getCreateTime() / 1000);
						jsonPost.put("voice", "");
						jsonPost.put("duration", "");
						
						///是否点赞
						boolean isRecommend = false;
						if(null != recommendPostIds)
							isRecommend = recommendPostIds.contains(String.valueOf(postInfo.getPostId()));
						jsonPost.put("is_support", isRecommend ? 1 : 0);
						
						///获取楼层用户信息
						userInfo = UserComponent.getInfoFromCache(userId);
						if(null == userInfo)
							uids.add(postInfo.getUserId());
						else
						{
							jsonPost.put("nickname", userInfo.getNickName());
							jsonPost.put("avatar", userInfo.getAvatar());
							jsonPost.put("level", userInfo.getLevel());
							jsonPost.put("coin", userInfo.getCoin());
							jsonPost.put("diamond", userInfo.getDiamond());
							jsonPost.put("exp", userInfo.getExp());
							jsonPost.put("upgrade_exp", userInfo.getUpgradeExp());
							jsonPost.put("gained_exp", userInfo.getGainedExp());
							jsonPost.put("badge", userInfo.getBadges());
							
							JSONObject jsonUser = buildUserJSONObject(userInfo);
							long threadCount = threadService.getUserThreadCount(userId);
							long eliteThreadCount = threadService.getUserEliteThreadCount(userId);
							long postCount = postService.getUserPostCount(userId);
							long commentCount = commentService.getUserCommentCount(userId);
							jsonUser.put("threads", threadCount);
							jsonUser.put("replies", postCount + commentCount);
							jsonUser.put("elite_threads", eliteThreadCount);
							
							///判断是否为版主
							long forumId = postInfo.getForumId();
							int roleId = userRoleService.getRoleId(forumId, userId);
							jsonUser.put("is_moderator", roleId > 0);
							jsonPost.put("user", jsonUser);
						}
						
						///获取楼层评论
						JSONArray arrayComments = buildPostCommentList(postInfo.getPostId());
						if(null == arrayComments)
							arrayComments = new JSONArray();
						jsonPost.put("replys", arrayComments);
						
						if(postInfo.getComments() >= 10)
							arrayHotPosts.put(jsonPost);
						else
							arrayPosts.put(jsonPost);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							fillPostUser(arrayPosts, userMap);
							fillPostUser(arrayHotPosts, userMap);
						}
					}
				}
			}
			data.put("postlist", arrayPosts);
			data.put("hotpostlist", arrayHotPosts);
			data.put("thread", jsonThread);
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.getPostListByApp throw an error.", e);
		}
	}
	
	private ResultValue getPostListByWeb(Page<FeedPost> page, long threadId, int pageNum, int pageSize, long currentUserId) throws Exception
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
					User userInfo = null;
					long userId = 0L;
					for(FeedPost postInfo : posts)
					{
						userId = postInfo.getUserId();
						jsonPost = new JSONObject();
						jsonPost.put("fid", postInfo.getForumId());
						jsonPost.put("pid", postInfo.getPostId());
						jsonPost.put("uid", userId);
						jsonPost.put("content", postInfo.getContentFilter());
						jsonPost.put("html_content", postInfo.getHtmlContentFilter());
						jsonPost.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
						jsonPost.put("support_cnt", postInfo.getRecommends());
						jsonPost.put("replycnt", postInfo.getComments());
						jsonPost.put("floor", postInfo.getPosition());
						jsonPost.put("ctime", postInfo.getCreateTime() / 1000);
						jsonPost.put("voice", "");
						jsonPost.put("duration", "");
						
						///是否点赞
						boolean isRecommend = false;
						if(null != recommendPostIds)
							isRecommend = recommendPostIds.contains(String.valueOf(postInfo.getPostId()));
						jsonPost.put("is_support", isRecommend ? 1 : 0);
						
						///获取楼层用户信息
						userInfo = UserComponent.getInfoFromCache(userId);
						if(null == userInfo)
							uids.add(postInfo.getUserId());
						else
						{
							jsonPost.put("nickname", userInfo.getNickName());
							jsonPost.put("avatar", userInfo.getAvatar());
							jsonPost.put("level", userInfo.getLevel());
							jsonPost.put("coin", userInfo.getCoin());
							jsonPost.put("diamond", userInfo.getDiamond());
							jsonPost.put("exp", userInfo.getExp());
							jsonPost.put("upgrade_exp", userInfo.getUpgradeExp());
							jsonPost.put("gained_exp", userInfo.getGainedExp());
							jsonPost.put("badge", userInfo.getBadges());
							
							JSONObject jsonUser = buildUserJSONObject(userInfo);
							long threadCount = threadService.getUserThreadCount(userId);
							long eliteThreadCount = threadService.getUserEliteThreadCount(userId);
							long postCount = postService.getUserPostCount(userId);
							long commentCount = commentService.getUserCommentCount(userId);
							jsonUser.put("threads", threadCount);
							jsonUser.put("replies", postCount + commentCount);
							jsonUser.put("elite_threads", eliteThreadCount);
							
							///判断是否为版主
							long forumId = postInfo.getForumId();
							int roleId = userRoleService.getRoleId(forumId, userId);
							jsonUser.put("is_moderator", roleId > 0);
							jsonPost.put("user", jsonUser);
						}
						
						///获取楼层评论
						JSONArray arrayComments = buildPostCommentList(postInfo.getPostId());
						if(null == arrayComments)
							arrayComments = new JSONArray();
						jsonPost.put("replys", arrayComments);
						arrayPosts.put(jsonPost);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
							fillPostUser(arrayPosts, userMap);
					}
					
					///获取主题
					FeedThread threadInfo = threadService.getFullInfo(threadId);
					jsonThread = buildThreadJSONObject(threadInfo, currentUserId);
					if(null != jsonThread)
						jsonThread.put("total", total);
					else
						jsonThread = new JSONObject();
					
					JSONArray arrayRelationVideoes = buildRelationVideoList(threadInfo.getForumId(), currentUserId);
					if(null == arrayRelationVideoes)
						arrayRelationVideoes = new JSONArray();
					jsonThread.put("related_videos", arrayRelationVideoes);
				}
			}
			data.put("postlist", arrayPosts);
			data.put("thread", jsonThread);
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.getPostListByWeb throw an error.", e);
		}
	}
	
	private JSONObject buildThreadJSONObject(FeedThread threadInfo, long currentUserId) throws Exception
	{
		JSONObject jsonThread = new JSONObject();
		jsonThread.put("fid", threadInfo.getForumId());
		jsonThread.put("tid", threadInfo.getThreadId());
		jsonThread.put("uid", threadInfo.getUserId());
		jsonThread.put("title", threadInfo.getSubjectFilter());
		jsonThread.put("postcnt", threadInfo.getReplies());
		jsonThread.put("recommends", threadInfo.getRecommends());
		
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
		
		///判断是否已对该主题点赞
		boolean isRecommend = threadService.existsRecommend(currentUserId, threadInfo.getThreadId());
		jsonThread.put("isrecommend", isRecommend ? 1 : 0);
		
		///判断是否已收藏该主题
		boolean isFavorite = favoriteService.exists(currentUserId, threadInfo.getThreadId());
		jsonThread.put("fav", isFavorite ? 1 : 0);
		jsonThread.put("is_closed", threadInfo.isClosed() ? 1 : 0);
		jsonThread.put("state", threadInfo.isClosed() ? "1" : "0");
		jsonThread.put("page_view", threadInfo.getPageView());
		jsonThread.put("category", threadInfo.isTop() ? 1 : 0);
		
		///获取版块信息
		FeedForum forumInfo = forumService.getInfo(threadInfo.getForumId());
		if(null != forumInfo)
		{
			jsonThread.put("forum_name", forumInfo.getName());
			jsonThread.put("forum_type", forumInfo.getType());
		}
		
		///获取主题视频信息
		if(threadInfo.isVideo())
		{
			FeedPost firstPostInfo = threadInfo.getPost();
			if(null != firstPostInfo)
			{
				JSONObject jsonVideo = new JSONObject();
				jsonVideo.put("id", firstPostInfo.getVideoId());
				jsonVideo.put("thumbnail", firstPostInfo.getThumbnail());
				jsonVideo.put("duration", firstPostInfo.getDuration());
				jsonThread.put("video", jsonVideo);
			}
		}
		
		///获取主题用户信息
		User threadUserInfo = UserComponent.getInfo(threadInfo.getUserId());
		if(null != threadUserInfo)
		{
			JSONObject jsonUser = buildUserJSONObject(threadUserInfo);
			
			long threadCount = threadService.getUserThreadCount(threadInfo.getUserId());
			long eliteThreadCount = threadService.getUserEliteThreadCount(threadInfo.getUserId());
			long postCount = postService.getUserPostCount(threadInfo.getUserId());
			long commentCount = commentService.getUserCommentCount(threadInfo.getUserId());
			jsonUser.put("threads", threadCount);
			jsonUser.put("replies", postCount + commentCount);
			jsonUser.put("elite_threads", eliteThreadCount);
			
			///判断是否为版主
			int roleId = userRoleService.getRoleId(threadInfo.getForumId(), threadInfo.getUserId());
			jsonUser.put("is_moderator", roleId > 0);
			jsonThread.put("user", jsonUser);
		}
		return jsonThread;
	}
	
	private JSONArray buildPostCommentList(long postId) throws Exception
	{
		JSONArray arrayComments = new JSONArray();
		Page<FeedComment> pageComments = commentService.getPostCommentList(postId, 1, 2);
		if(null != pageComments)
		{
			List<FeedComment> comments = pageComments.getList();
			if(null != comments && comments.size() > 0)
			{
				JSONObject jsonComment = null;
				for(FeedComment commentInfo : comments)
				{
					jsonComment = new JSONObject();
					jsonComment.put("tid", commentInfo.getCommentId());
					jsonComment.put("rid", commentInfo.getPostId());
					jsonComment.put("uid", commentInfo.getUserId());
					jsonComment.put("content", commentInfo.getContentFilter());
					jsonComment.put("ctime", commentInfo.getCreateTime());
					///获取评论用户信息
					User userInfo = UserComponent.getInfo(commentInfo.getUserId());
					if(null != userInfo)
					{
						jsonComment.put("nickname", userInfo.getNickName());
						jsonComment.put("avatar", userInfo.getAvatar());
						jsonComment.put("level", userInfo.getLevel());
						jsonComment.put("coin", userInfo.getCoin());
						jsonComment.put("diamond", userInfo.getDiamond());
						jsonComment.put("exp", userInfo.getExp());
						jsonComment.put("upgrade_exp", userInfo.getUpgradeExp());
						jsonComment.put("gained_exp", userInfo.getGainedExp());
						jsonComment.put("badge", userInfo.getBadges());
					}
					arrayComments.put(jsonComment);
				}
			}
		}
		return arrayComments;
	}
	
	private JSONArray buildRelationVideoList(long forumId, long currentUserId) throws Exception
	{
		Page<FeedThread> hotVideoPage = threadService.getForumHotVideoThreadList(forumId, 1, 10);
		JSONArray arrayVideoes = new JSONArray();
		if(null != hotVideoPage)
		{
			List<FeedThread> list = hotVideoPage.getList();
			if(null != list && list.size() > 0)
			{
				for(FeedThread threadInfo : list)
					arrayVideoes.put(buildThreadJSONObject(threadInfo, currentUserId));
			}
		}
		return arrayVideoes;
	}
	
	private JSONObject buildUserJSONObject(User userInfo) throws Exception
	{
		JSONObject jsonUser = new JSONObject();
		jsonUser.put("id", userInfo.getUserId());
		jsonUser.put("nickname", userInfo.getNickName());
		jsonUser.put("avatar", userInfo.getAvatar());
		jsonUser.put("level", userInfo.getLevel());
		jsonUser.put("coin", userInfo.getCoin());
		jsonUser.put("diamond", userInfo.getDiamond());
		jsonUser.put("exp", userInfo.getExp());
		jsonUser.put("upgrade_exp", userInfo.getUpgradeExp());
		jsonUser.put("gained_exp", userInfo.getGainedExp());
		jsonUser.put("badge", userInfo.getBadges());
		return jsonUser;
	}
	
	private void fillPostUser(JSONArray arrayPosts, Map<Long, User> userMap) throws Exception
	{
		JSONObject jsonPost = null;
		long userId = 0L;
		long forumId = 0L;
		String nickName = "";
		User userInfo = null;
		for(int i=0; i<arrayPosts.length(); i++)
		{
			jsonPost = arrayPosts.getJSONObject(i);
			nickName = jsonPost.optString("nickname", "");
			userId = jsonPost.optLong("uid", 0L);
			forumId = jsonPost.optLong("fid", 0L);
			
			///填充发表楼层用户信息
			if(StringUtil.isNullOrEmpty(nickName))
			{
				if(userMap.containsKey(userId))
				{
					userInfo = userMap.get(userId);
					jsonPost.put("nickname", userInfo.getNickName());
					jsonPost.put("avatar", userInfo.getAvatar());
					jsonPost.put("level", userInfo.getLevel());
					jsonPost.put("coin", userInfo.getCoin());
					jsonPost.put("diamond", userInfo.getDiamond());
					jsonPost.put("exp", userInfo.getExp());
					jsonPost.put("upgrade_exp", userInfo.getUpgradeExp());
					jsonPost.put("gained_exp", userInfo.getGainedExp());
					jsonPost.put("badge", userInfo.getBadges());
					
					JSONObject jsonUser = buildUserJSONObject(userInfo);
					long threadCount = threadService.getUserThreadCount(userId);
					long eliteThreadCount = threadService.getUserEliteThreadCount(userId);
					long postCount = postService.getUserPostCount(userId);
					long commentCount = commentService.getUserCommentCount(userId);
					jsonUser.put("threads", threadCount);
					jsonUser.put("replies", postCount + commentCount);
					jsonUser.put("elite_threads", eliteThreadCount);
					
					///判断是否为版主
					int roleId = userRoleService.getRoleId(forumId, userId);
					jsonUser.put("is_moderator", roleId > 0);
					jsonPost.put("user", jsonUser);
				}
			}
		}
	}
}