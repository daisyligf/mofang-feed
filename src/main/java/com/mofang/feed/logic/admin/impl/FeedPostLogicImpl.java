package com.mofang.feed.logic.admin.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.SysMessageNotifyComponent;
import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.global.common.FeedPrivilege;
import com.mofang.feed.global.common.OperateBehavior;
import com.mofang.feed.global.common.OperateSourceType;
import com.mofang.feed.logic.admin.FeedPostLogic;
import com.mofang.feed.model.FeedOperateHistory;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.OperatorHistoryInfo;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedOperateHistoryService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedOperateHistoryServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPostLogicImpl implements FeedPostLogic
{
	private final static FeedPostLogicImpl LOGIC = new FeedPostLogicImpl();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedOperateHistoryService operateService = FeedOperateHistoryServiceImpl.getInstance();
	
	private FeedPostLogicImpl()
	{}
	
	public static FeedPostLogicImpl getInstance()
	{
		return LOGIC;
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
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
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
			boolean hasPrivilege = adminService.exists(operatorId);
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
			boolean hasPrivilege = adminService.exists(operatorId);
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
	public ResultValue getPostList(long threadId, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			long total = 0;
			JSONArray arrayPosts =new JSONArray();
			Page<FeedPost> page = postService.getPostList(threadId, status, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedPost> posts = page.getList();
				if(null != posts)
				{
					
					Map<Long, OperatorHistoryInfo> historyMap = null;
					if(status == 0) {
						Set<Long> postIds = new HashSet<Long>(posts.size());
						for(int idx = 0; idx < posts.size(); idx ++) {
							postIds.add(posts.get(idx).getPostId());
						}
						historyMap = operateService.getMap(postIds, FeedPrivilege.DELETE_POST);
					}
					
					JSONObject jsonPost = null;
					JSONObject jsonForum = null;
					JSONObject jsonThread = null;
					JSONObject jsonUser = null;
					User userInfo = null;
					FeedThread threadInfo = null;
					for(FeedPost postInfo : posts)
					{
						jsonPost = new JSONObject();
						jsonPost.put("pid", postInfo.getPostId());          ///楼层ID
						jsonPost.put("content", postInfo.getContentFilter());      ///楼层内容
						jsonPost.put("position", postInfo.getPosition());       ///楼层数
						jsonPost.put("comments", postInfo.getComments());
						jsonPost.put("recommends", postInfo.getRecommends());
						jsonPost.put("create_time", postInfo.getCreateTime());
						jsonPost.put("status", postInfo.getStatus());
						
						if(null != historyMap && historyMap.size() != 0) {
							OperatorHistoryInfo historyInfo = historyMap.get(postInfo.getPostId());
							if(null != historyInfo) {
								jsonPost.put("operator_name", historyInfo.operatorName);
								jsonPost.put("oprerator_name", historyInfo.operateTime);
							}
						}
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", postInfo.getForumId());
						
						jsonThread = new JSONObject();
						jsonThread.put("tid", postInfo.getThreadId());
						threadInfo = threadService.getInfo(postInfo.getThreadId(), DataSource.REDIS);
						if(null != threadInfo)
							jsonThread.put("subject", threadInfo.getSubjectFilter());
						
						jsonUser = new JSONObject();
						jsonUser.put("user_id", postInfo.getUserId());
						///获取发布主题的用户信息
						userInfo = UserComponent.getInfoFromCache(postInfo.getUserId());
						if(null == userInfo)
							uids.add(postInfo.getUserId());
						else
							jsonUser.put("nickname", userInfo.getNickName());
						
						jsonPost.put("forum", jsonForum);
						jsonPost.put("thread", jsonThread);
						jsonPost.put("user", jsonUser);
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
								jsonUser = jsonPost.optJSONObject("user");
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
			data.put("list", arrayPosts);
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

	@Override
	public ResultValue search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception
	{
		try
		{
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			JSONArray arrayPosts = new JSONArray();
			long total = 0;
			Page<FeedPost> page = postService.search(forumId, forumName, author, keyword, status, pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedPost> list = page.getList();
				if(null != list)
				{
					JSONObject jsonPost = null;
					JSONObject jsonForum = null;
					JSONObject jsonThread = null;
					JSONObject jsonUser = null;
					User userInfo = null;
					FeedThread threadInfo = null;
					for(FeedPost postInfo : list)
					{
						jsonPost = new JSONObject();
						jsonPost.put("pid", postInfo.getPostId());          ///楼层ID
						jsonPost.put("content", postInfo.getContentFilter());      ///楼层内容
						jsonPost.put("position", postInfo.getPosition());       ///楼层数
						jsonPost.put("comments", postInfo.getComments());     ///楼层评论数
						jsonPost.put("recommends", postInfo.getRecommends());     ///楼层点赞数
						jsonPost.put("create_time", postInfo.getCreateTime());        ///楼层回复时间
						jsonPost.put("status", postInfo.getStatus());      ///楼层状态
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", postInfo.getForumId());        ///楼层所属版块ID
						
						jsonThread = new JSONObject();
						jsonThread.put("tid", postInfo.getThreadId());       ///楼层所属主题ID
						threadInfo = threadService.getInfo(postInfo.getThreadId(), DataSource.REDIS);
						if(null != threadInfo)
							jsonThread.put("subject", threadInfo.getSubjectFilter());
						
						jsonUser = new JSONObject();
						jsonUser.put("user_id", postInfo.getUserId());
						///获取发布主题的用户信息
						userInfo = UserComponent.getInfoFromCache(postInfo.getUserId());
						if(null == userInfo)
							uids.add(postInfo.getUserId());
						else
							jsonUser.put("nickname", userInfo.getNickName());
						
						jsonPost.put("forum", jsonForum);
						jsonPost.put("thread", jsonThread);
						jsonPost.put("user", jsonUser);
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
								jsonUser = jsonPost.optJSONObject("user");
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
			data.put("list", arrayPosts);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedPostLogicImpl.search throw an error.", e);
		}
	}
}