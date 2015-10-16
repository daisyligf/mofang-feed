package com.mofang.feed.logic.app.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
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
import com.mofang.feed.logic.app.FeedCommentLogic;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.FeedPost;
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
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedCommentServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.feed.service.impl.task.FeedThreadRepliesRewardServiceImpl;
import com.mofang.feed.service.task.FeedThreadRepliesRewardService;
import com.mofang.feed.util.HtmlTagFilter;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedCommentLogicImpl implements FeedCommentLogic
{
	private final static FeedCommentLogicImpl LOGIC = new FeedCommentLogicImpl();
	private WaterproofWallRedis waterproofWallRedis = WaterproofWallRedisImpl.getInstance();
	private FeedBlackListService blackListService = FeedBlackListServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedOperateHistoryService operateService = FeedOperateHistoryServiceImpl.getInstance();
	private FeedCommentService commentService = FeedCommentServiceImpl.getInstance();
	private FeedThreadRepliesRewardService rewardService = FeedThreadRepliesRewardServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedCommentLogicImpl()
	{}
	
	public static FeedCommentLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedComment model) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			String content = model.getContent();
			long userId = model.getUserId();
			long postId = model.getPostId();
			
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

			if(StringUtil.isNullOrEmpty(contentFilter))
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
			
			///验证楼层是否存在
			FeedPost postInfo = postService.getInfo(postId, DataSource.REDIS);
			if(null == postInfo)
			{
				result.setCode(ReturnCode.POST_NOT_EXISTS);
				result.setMessage(ReturnMessage.POST_NOT_EXISTS);
				return result;
			}
			
			///验证主题是否存在 
			long threadId = postInfo.getThreadId();
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
			
			///完善Comment实体对象
			model.setForumId(forumId);
			model.setThreadId(threadId);
			model.setContentFilter(contentFilter);
			model.setContentMark(contentMark);
			
			///保存评论信息
			long commentId = commentService.add(model);
			
			/******************************执行任务******************************/
			TaskComponent.reply(userId);
			
			/******************************回复通知******************************/
			PostReplyNotify notify = new PostReplyNotify();
			notify.setPostId(threadInfo.getThreadId());
			notify.setPostTitle(threadInfo.getSubjectFilter());
			notify.setReplyId(postId);
			notify.setReplyText(model.getContentFilter());
			notify.setReplyPictures("");
			notify.setReplyUserId(model.getUserId());
			notify.setReplyType(ReplyType.POST);
			notify.setForumId(forumId);
			FeedForum forumInfo = forumService.getInfo(forumId);
			if(null != forumInfo)
				notify.setForumName(forumInfo.getName());
			
			///非自己回复的需要发送通知(给楼主发送通知)
			if(userId != threadInfo.getUserId())
			{
				notify.setUserId(threadInfo.getUserId());
				HttpComponent.pushPostReplyNotify(notify);
			}
			///非自己回复的需要发送通知(给层主发送通知)
			if(userId != postInfo.getUserId())
			{
				notify.setUserId(postInfo.getUserId());
				HttpComponent.pushPostReplyNotify(notify);
			}
			
			/******************************回复奖励******************************/
			rewardService.checkAndReword(threadId);
			
			///创建返回结果
			JSONObject data = new JSONObject();
			data.put("cid", commentId);
			data.put("content", contentFilter);
			data.put("create_time", model.getCreateTime());
			
			JSONObject jsonPost = new JSONObject();
			jsonPost.put("pid", model.getPostId());
			///获取楼层评论数
			postInfo = postService.getInfo(model.getPostId(), DataSource.REDIS);
			if(null != postInfo)
				jsonPost.put("comments", postInfo.getComments());
			
			///获取用户信息
			JSONObject jsonUser = new JSONObject();
			jsonUser.put("user_id", model.getUserId());
			User userInfo = UserComponent.getInfo(userId);
			if(null != userInfo)
			{
				jsonUser.put("nickname", userInfo.getNickName());
				jsonUser.put("avatar", userInfo.getAvatar());
			}
			data.put("post", jsonPost);
			data.put("user", jsonUser);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedCommentLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long commentId, long operatorId, String reason) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///评论有效性检查
			FeedComment commentInfo = commentService.getInfo(commentId, DataSource.REDIS);
			if(null == commentInfo)
			{
				result.setCode(ReturnCode.COMMENT_NOT_EXISTS);
				result.setMessage(ReturnMessage.COMMENT_NOT_EXISTS);
				return result;
			}
			///权限检查
			long forumId = commentInfo.getForumId();
			long userId = commentInfo.getUserId();
			if(operatorId != userId)  ///如果不是作者
			{
				boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.DELETE_COMMENT);
				if(!hasPrivilege)
				{
					result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
					result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
					return result;
				}
			}
			
			///删除楼层
			commentService.delete(commentInfo);
			
			/******************************操作记录******************************/
			FeedOperateHistory operateInfo = new FeedOperateHistory();
			operateInfo.setUserId(userId);
			operateInfo.setForumId(forumId);
			operateInfo.setPrivilegeId(FeedPrivilege.DELETE_COMMENT);
			operateInfo.setSourceType(OperateSourceType.COMMENT);
			operateInfo.setSourceId(commentId);
			operateInfo.setOperateBehavior(OperateBehavior.DELETE_COMMENT);
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
			throw new Exception("at FeedCommentLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue getPostCommentList(long postId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayComments = new JSONArray();
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			long total = 0;
			Page<FeedComment> page = commentService.getPostCommentList(postId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedComment> comments = page.getList();
				if(null != comments)
				{
					JSONObject jsonComment = null;
					JSONObject jsonPost = null;
					JSONObject jsonUser = null;
					User userInfo = null;
					for(FeedComment commentInfo : comments)
					{
						jsonComment = new JSONObject();
						jsonComment.put("cid", commentInfo.getCommentId());
						jsonComment.put("content", commentInfo.getContentFilter());
						jsonComment.put("create_time", commentInfo.getCreateTime());
						
						jsonPost = new JSONObject();
						jsonPost.put("pid", commentInfo.getPostId());
						
						///获取用户信息
						jsonUser = new JSONObject();
						jsonUser.put("user_id", commentInfo.getUserId());
						userInfo = UserComponent.getInfoFromCache(commentInfo.getUserId());
						if(null == userInfo)
							uids.add(commentInfo.getUserId());
						else
						{
							jsonUser.put("nickname", userInfo.getNickName());
							jsonUser.put("avatar", userInfo.getAvatar());
						}
						jsonComment.put("post", jsonPost);
						jsonComment.put("user", jsonUser);
						arrayComments.put(jsonComment);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayComments.length(); i++)
							{
								jsonComment = arrayComments.getJSONObject(i);
								jsonUser = jsonComment.optJSONObject("user");
								String nickName = jsonUser.optString("nickname", "");
								long userId = jsonUser.optLong("user_id", 0L);
								
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
			data.put("comments", arrayComments);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedCommentLogicImpl.getPostCommentList throw an error.", e);
		}
	}
}