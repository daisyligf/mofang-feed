package com.mofang.feed.logic.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.global.common.OperateBehavior;
import com.mofang.feed.global.common.OperateSourceType;
import com.mofang.feed.logic.admin.FeedCommentLogic;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedCommentService;
import com.mofang.feed.service.FeedOperateHistoryService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.impl.FeedCommentServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedCommentLogicImpl implements FeedCommentLogic
{
	private final static FeedCommentLogicImpl LOGIC = new FeedCommentLogicImpl();
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
			boolean hasPrivilege = userRoleService.hasPrivilege(forumId, operatorId, FeedPrivilege.DELETE_COMMENT);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
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
			JSONArray arrayComments =new JSONArray();
			Page<FeedComment> page = commentService.getCommentList(postId, status, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedComment> comments = page.getList();
				if(null != comments)
				{
					JSONObject jsonComment = null;
					JSONObject jsonForum = null;
					JSONObject jsonThread = null;
					JSONObject jsonPost = null;
					JSONObject jsonUser = null;
					User userInfo = null;
					for(FeedComment commentInfo : comments)
					{
						jsonComment = new JSONObject();
						jsonComment.put("cid", commentInfo.getCommentId());        ///评论ID
						jsonComment.put("content", commentInfo.getContentFilter());
						jsonComment.put("status", commentInfo.getStatus());
						jsonComment.put("create_time", commentInfo.getCreateTime());
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", commentInfo.getForumId());
						
						jsonThread = new JSONObject();
						jsonThread.put("tid", commentInfo.getThreadId());
						
						jsonPost = new JSONObject();
						jsonPost.put("pid", commentInfo.getPostId());
						
						jsonUser = new JSONObject();
						jsonUser.put("user_id", commentInfo.getUserId());
						///获取发布主题的用户信息
						userInfo = UserComponent.getInfoFromCache(commentInfo.getUserId());
						if(null == userInfo)
							uids.add(commentInfo.getUserId());
						else
							jsonUser.put("nickname", userInfo.getNickName());
						
						jsonComment.put("forum", jsonForum);
						jsonComment.put("thread", jsonThread);
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
									}
								}
							}
						}
					}
				}
			}
			data.put("total", total);
			data.put("list", arrayComments);
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
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayComments = new JSONArray();
			long total = 0;
			Page<FeedComment> page = commentService.search(forumId, forumName, author, keyword, status, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedComment> comments = page.getList();
				if(null != comments)
				{
					JSONObject jsonComment = null;
					JSONObject jsonForum = null;
					JSONObject jsonThread = null;
					JSONObject jsonPost = null;
					JSONObject jsonUser = null;
					User userInfo = null;
					for(FeedComment commentInfo : comments)
					{
						jsonComment = new JSONObject();
						jsonComment.put("cid", commentInfo.getCommentId());        ///评论ID
						jsonComment.put("content", commentInfo.getContentFilter());
						jsonComment.put("status", commentInfo.getStatus());
						jsonComment.put("create_time", commentInfo.getCreateTime());
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", commentInfo.getForumId());
						
						jsonThread = new JSONObject();
						jsonThread.put("tid", commentInfo.getThreadId());
						
						jsonPost = new JSONObject();
						jsonPost.put("pid", commentInfo.getPostId());
						
						jsonUser = new JSONObject();
						jsonUser.put("user_id", commentInfo.getUserId());
						///获取发布主题的用户信息
						userInfo = UserComponent.getInfoFromCache(commentInfo.getUserId());
						if(null == userInfo)
							uids.add(commentInfo.getUserId());
						else
							jsonUser.put("nickname", userInfo.getNickName());
						
						jsonComment.put("forum", jsonForum);
						jsonComment.put("thread", jsonThread);
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
									}
								}
							}
						}
					}
				}
			}

			data.put("total", total);
			data.put("list", arrayComments);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedCommentLogicImpl.search throw an error.", e);
		}
	}
}