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
import com.mofang.feed.logic.admin.FeedAdminUserLogic;
import com.mofang.feed.model.FeedAdminUser;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedAdminUserLogicImpl implements FeedAdminUserLogic
{
	private final static FeedAdminUserLogicImpl LOGIC = new FeedAdminUserLogicImpl();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	
	private  FeedAdminUserLogicImpl()
	{}
	
	public static FeedAdminUserLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue exists(long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			boolean isExists = adminService.exists(userId);
			
			///返回结果
			JSONObject data = new JSONObject();
			data.put("is_exists", isExists);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedAdminUserLogicImpl.exists throw an error.", e);
		}
	}

	@Override
	public ResultValue add(FeedAdminUser model, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断管理员是否已存在
			boolean isExists = adminService.exists(model.getUserId());
			if(isExists)
			{
				result.setCode(ReturnCode.ADMIN_USER_EXISTS);
				result.setMessage(ReturnMessage.ADMIN_USER_EXISTS);
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
			
			///添加管理员
			adminService.add(model);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedAdminUserLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long userId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断管理员是否已存在
			boolean isExists = adminService.exists(userId);
			if(!isExists)
			{
				result.setCode(ReturnCode.ADMIN_USER_NOT_EXISTS);
				result.setMessage(ReturnMessage.ADMIN_USER_NOT_EXISTS);
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
			
			///删除管理员
			adminService.delete(userId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedAdminUserLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue getUserList(int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			///存储缓存中没有数据的用户ID, 用于批量获取用户信息
			Set<Long> uids = new HashSet<Long>();
			long total = 0;
			JSONArray arrayAdmins =new JSONArray();
			Page<FeedAdminUser> page = adminService.getAdminList(pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedAdminUser> admins = page.getList();
				if(null != admins)
				{
					JSONObject jsonAdmin = null;
					User userInfo = null;
					for(FeedAdminUser adminInfo : admins)
					{
						jsonAdmin = new JSONObject();
						jsonAdmin.put("user_id", adminInfo.getUserId());          ///用户ID
						///获取用户信息
						userInfo = UserComponent.getInfoFromCache(adminInfo.getUserId());
						if(null == userInfo)
							uids.add(adminInfo.getUserId());
						else
							jsonAdmin.put("nickname", userInfo.getNickName());
						
						///获取用户发帖总数
						long threads = threadService.getUserThreadCount(adminInfo.getUserId());
						///获取用户回帖总数
						long replies = postService.getUserReplyCount(adminInfo.getUserId());
						jsonAdmin.put("threads", threads);
						jsonAdmin.put("replies", replies);
						jsonAdmin.put("create_time", adminInfo.getCreateTime());
						arrayAdmins.put(jsonAdmin);
					}
					
					///填充用户信息
					if(uids.size() > 0)
					{
						Map<Long, User> userMap = UserComponent.getInfoByIds(uids);
						if(null != userMap)
						{
							for(int i=0; i<arrayAdmins.length(); i++)
							{
								jsonAdmin = arrayAdmins.getJSONObject(i);
								String nickName = jsonAdmin.optString("nickname", "");
								long userId = jsonAdmin.optLong("user_id", 0L);
								
								///填充用户信息
								if(StringUtil.isNullOrEmpty(nickName))
								{
									if(userMap.containsKey(userId))
									{
										userInfo = userMap.get(userId);
										jsonAdmin.put("nickname", userInfo.getNickName());
									}
								}
							}
						}
					}
				}
			}
			data.put("total", total);
			data.put("list", arrayAdmins);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedSysUserRoleLogicImpl.getUserList throw an error.", e);
		}
	}
}