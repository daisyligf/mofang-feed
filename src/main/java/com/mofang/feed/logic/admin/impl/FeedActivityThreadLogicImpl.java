package com.mofang.feed.logic.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.logic.admin.FeedActivityThreadLogic;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;
import com.mofang.feed.model.external.FeedActivityUser;
import com.mofang.feed.service.FeedActivityThreadService;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedActivityThreadServiceImpl;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;

public class FeedActivityThreadLogicImpl implements FeedActivityThreadLogic {

	private static final FeedActivityThreadLogicImpl LOGIC = new FeedActivityThreadLogicImpl();
	private FeedActivityThreadService activityThreadService = FeedActivityThreadServiceImpl
			.getInstance();
	private FeedSysUserRoleService sysUserRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedAdminUserService adminUserService = FeedAdminUserServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();

	private FeedActivityThreadLogicImpl() {
	}

	public static FeedActivityThreadLogicImpl getInstance() {
		return LOGIC;
	}

	@Override
	public ResultValue generateRewardUserList(long operatorId, long threadId,
			FeedActivityThreadRewardCondition condition) throws Exception {
		try {
			ResultValue result = new ResultValue();
			
			///权限检查
			boolean hasPrivilege = adminUserService.exists(operatorId);
			if(!hasPrivilege) {
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			FeedThread feedThread = threadService.getInfo(threadId, DataSource.REDIS);
			if(feedThread == null) {
				result.setCode(ReturnCode.THREAD_NOT_EXISTS);
				result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
				return result;
			}
			
			List<FeedActivityUser> userList = activityThreadService
					.generateRewardUserList(threadId, condition);

			if(userList == null || userList.size() == 0) {
				throw new Exception("帖子id: " + threadId + "对应的用户获奖名单是空的");
			}
			
			JSONObject data = new JSONObject();
			data.put("thread_subject", feedThread.getSubject());
			JSONArray userJsonArr = new JSONArray();
			
			int size = userList.size();
			if(size > 100) {
				List<FeedActivityUser> subUserList =  new ArrayList<FeedActivityUser>(50);
				for(int idx = 0; idx < size; idx ++) {
					subUserList.add(userList.get(idx));
					if(subUserList.size() == 100 || idx == size - 1){
						HttpComponent.fillUserInfo(subUserList);
						subUserList.clear();
					}
				}
			}else HttpComponent.fillUserInfo(userList);
			
			JSONObject jsonUser = null;
			for(int idx = 0; idx < userList.size(); idx ++) {
				FeedActivityUser user = userList.get(idx);
				long userId = user.getUserId();
				
				//过滤版主管理员
				boolean banzuAndadmin = false;
				boolean isBanzu = false;
				boolean isAdmin = false;
				if(condition.admin) {
					isBanzu = sysUserRoleService.exists(user.getForumId(), userId);
					isAdmin = adminUserService.exists(userId);
					if(isBanzu || isAdmin) banzuAndadmin = true;
				}
				
				//如果相符
				if( ( condition.admin && banzuAndadmin) ) continue; 
				
				jsonUser = new JSONObject();
				jsonUser.put("user_id", userId);
				jsonUser.put("nickname", user.getNickName());
				jsonUser.put("position", user.getPostion());
				jsonUser.put("level", user.getLevel());
				userJsonArr.put(jsonUser);
				
			}
			data.put("users", userJsonArr);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception(
					"at FeedActivityThreadLogicImpl.generateRewardUserList throw an error.",
					e);
		}
	}

}
