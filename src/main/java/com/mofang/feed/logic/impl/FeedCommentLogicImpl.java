package com.mofang.feed.logic.impl;

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
import com.mofang.feed.global.common.ThreadTag;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.feed.logic.FeedCommentLogic;
import com.mofang.feed.model.FeedComment;
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
import com.mofang.feed.service.FeedOperateHistoryService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedBlackListServiceImpl;
import com.mofang.feed.service.impl.FeedCommentServiceImpl;
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
			notify.setUserId(threadInfo.getUserId());
			notify.setPostId(threadInfo.getThreadId());
			notify.setPostTitle(threadInfo.getSubjectFilter());
			notify.setReplyId(commentId);
			notify.setReplyText(model.getContentFilter());
			notify.setReplyPictures("");
			notify.setReplyUserId(model.getUserId());
			notify.setReplyType(ReplyType.POST);
			HttpComponent.pushPostReplyNotify(notify);
			
			///构建返回结果
			JSONObject data = new JSONObject();
			data.put("tid", threadId);
			data.put("rid", commentId);
			data.put("uid", userId);
			data.put("ctime", model.getCreateTime());
			data.put("content", model.getContentFilter());
			
			///获取用户信息
			User userInfo = UserComponent.getInfo(userId);
			if(null != userInfo)
			{
				data.put("nickname", userInfo.getNickName());
				data.put("avatar", userInfo.getAvatar());
			}
			
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
	public ResultValue restore(long commentId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///评论有效性检查
			FeedComment commentInfo = commentService.getInfo(commentId, DataSource.MYSQL);
			if(null == commentInfo)
			{
				result.setCode(ReturnCode.COMMENT_NOT_EXISTS);
				result.setMessage(ReturnMessage.COMMENT_NOT_EXISTS);
				return result;
			}
			///权限检查
			long forumId = commentInfo.getForumId();
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.RESTORE_COMMENT);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///还原评论
			commentService.restore(commentInfo);
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedCommentLogicImpl.restore throw an error.", e);
		}
	}

	@Override
	public ResultValue remove(long commentId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///评论有效性检查
			FeedComment commentInfo = commentService.getInfo(commentId, DataSource.MYSQL);
			if(null == commentInfo)
			{
				result.setCode(ReturnCode.COMMENT_NOT_EXISTS);
				result.setMessage(ReturnMessage.COMMENT_NOT_EXISTS);
				return result;
			}
			///权限检查
			long forumId = commentInfo.getForumId();
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.REMOVE_COMMENT);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///删除评论(从回收站移除)
			commentService.remove(commentInfo);
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedCommentLogicImpl.remove throw an error.", e);
		}
	}

	@Override
	public ResultValue getCommentList(long postId, int status, int pageNum, int pageSize) throws Exception
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
			Page<FeedComment> page = commentService.getCommentList(postId, status, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedComment> comments = page.getList();
				if(null != comments)
				{
					JSONObject jsonComment = null;
					for(FeedComment commentInfo : comments)
					{
						jsonComment = new JSONObject();
						jsonComment.put("fid", commentInfo.getForumId());        ///所属版块ID
						jsonComment.put("tid", commentInfo.getThreadId());       ///主题ID
						jsonComment.put("cpid", commentInfo.getPostId());          ///楼层ID
						jsonComment.put("user_id", commentInfo.getUserId());         ///发布楼层的用户ID
						jsonComment.put("position", 0);
						jsonComment.put("original_message", commentInfo.getContent());
						jsonComment.put("message", commentInfo.getContent());
						jsonComment.put("status", commentInfo.getStatus());
						jsonComment.put("post_time", commentInfo.getCreateTime() / 1000);
						
						threadInfo = threadService.getInfo(commentInfo.getThreadId(), DataSource.REDIS);
						if(null != threadInfo)
							jsonComment.put("thread_subject", threadInfo.getSubjectFilter());
						
						userInfo = UserComponent.getInfoFromCache(commentInfo.getUserId());
						if(null == userInfo)
							uids.add(commentInfo.getUserId());
						else
							jsonComment.put("nickname", userInfo.getNickName());
						
						arrayPosts.put(jsonComment);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayPosts.length(); i++)
							{
								jsonComment = arrayPosts.getJSONObject(i);
								String nickName = jsonComment.optString("nickname", "");
								long userId = jsonComment.optLong("uid", 0L);
								
								///填充发帖用户信息
								if(StringUtil.isNullOrEmpty(nickName))
								{
									if(userMap.containsKey(userId))
									{
										userInfo = userMap.get(userId);
										jsonComment.put("nickname", userInfo.getNickName());
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
			throw new Exception("at FeedCommentLogicImpl.getCommentList throw an error.", e);
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
					User userInfo = null;
					for(FeedComment commentInfo : comments)
					{
						jsonComment = new JSONObject();
						jsonComment.put("tid", commentInfo.getCommentId());
						jsonComment.put("rid", commentInfo.getPostId());
						jsonComment.put("uid", commentInfo.getUserId());
						jsonComment.put("content", commentInfo.getContentFilter());
						jsonComment.put("ctime", commentInfo.getCreateTime() / 1000);
						
						///获取用户信息
						userInfo = UserComponent.getInfoFromCache(commentInfo.getUserId());
						if(null == userInfo)
							uids.add(commentInfo.getUserId());
						else
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
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayComments.length(); i++)
							{
								jsonComment = arrayComments.getJSONObject(i);
								String nickName = jsonComment.optString("nickname", "");
								long userId = jsonComment.optLong("uid", 0L);
								
								///填充发帖用户信息
								if(StringUtil.isNullOrEmpty(nickName))
								{
									if(userMap.containsKey(userId))
									{
										userInfo = userMap.get(userId);
										jsonComment.put("nickname", userInfo.getNickName());
										jsonComment.put("avatar", userInfo.getAvatar());
										jsonComment.put("level", userInfo.getLevel());
										jsonComment.put("exp", userInfo.getExp());
										jsonComment.put("coin", userInfo.getCoin());
										jsonComment.put("diamond", userInfo.getDiamond());
										jsonComment.put("upgrade_exp", userInfo.getUpgradeExp());
										jsonComment.put("gained_exp", userInfo.getGainedExp());
										jsonComment.put("badge", userInfo.getBadges());
									}
								}
							}
						}
					}
				}
			}

			JSONObject jsonTotal = new JSONObject();
			jsonTotal.put("total", total);
			data.put("comments", jsonTotal);
			data.put("commentlist", arrayComments);
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

	@Override
	public ResultValue getUserCommentList(long userId, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayItems = new JSONArray();
			JSONObject jsonItem = null;
			long total = 0;
			Page<FeedComment> page = commentService.getUserCommentList(userId, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedComment> comments = page.getList();
				if(null != comments)
				{
					JSONObject jsonPost = null;
					JSONObject jsonThread = null;
					JSONObject jsonComment = null;
					User userInfo = null;
					for(FeedComment commentInfo : comments)
					{
						jsonItem = new JSONObject();
						jsonComment = new JSONObject();
						jsonComment.put("pid", commentInfo.getCommentId());
						jsonComment.put("cpid", commentInfo.getPostId());
						jsonComment.put("user_id", commentInfo.getUserId());
						jsonComment.put("message", commentInfo.getContentFilter());
						jsonComment.put("post_time", commentInfo.getCreateTime() / 1000);
						
						
						jsonPost = new JSONObject();
						FeedPost postInfo = postService.getInfo(commentInfo.getPostId(), DataSource.REDIS);
						if(null != postInfo)
						{
							jsonPost.put("fid", postInfo.getForumId());
							jsonPost.put("pid", postInfo.getPostId());
							jsonPost.put("tid", postInfo.getThreadId());
							jsonPost.put("user_id", postInfo.getUserId());
							jsonPost.put("start", (postInfo.getPosition() == 1 ? 1 : 0));
							jsonPost.put("message", postInfo.getContentFilter());
							jsonPost.put("original_message", postInfo.getContent());
							jsonPost.put("html_content", postInfo.getHtmlContentFilter());
							jsonPost.put("pic", MiniTools.StringToJSONArray(postInfo.getPictures()));
							jsonPost.put("post_time", postInfo.getCreateTime() / 1000);
						}
						
						jsonThread = new JSONObject();
						FeedThread threadInfo = threadService.getFullInfo(commentInfo.getThreadId());
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
							jsonThread.put("type", threadInfo.getType());
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
						
						jsonItem.put("comment", commentInfo);
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
			throw new Exception("at FeedCommentLogicImpl.getUserCommentList throw an error.", e);
		}
	}
}