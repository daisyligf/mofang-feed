package com.mofang.feed.logic.admin.impl;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedActivityThreadLogic;
import com.mofang.feed.model.external.FeedActivityThreadRewardCondition;
import com.mofang.feed.model.external.FeedActivityUser;
import com.mofang.feed.service.FeedActivityThreadService;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.impl.FeedActivityThreadServiceImpl;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.util.HtmlTagFilter;
import com.mofang.framework.util.StringUtil;

public class FeedActivityThreadLogicImpl implements FeedActivityThreadLogic {

	private static final FeedActivityThreadLogicImpl LOGIC = new FeedActivityThreadLogicImpl();
	private FeedActivityThreadService activityThreadService = FeedActivityThreadServiceImpl
			.getInstance();
	private FeedSysUserRoleService sysUserRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedAdminUserService adminUserService = FeedAdminUserServiceImpl.getInstance();
	//private FeedBlackListService blackListService = FeedBlackListServiceImpl.getInstance();

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
			JSONArray data = new JSONArray();
			
			Map<Long, FeedActivityUser> userMap = activityThreadService
					.generateRewardUserList(threadId, condition);

			//填充nickname, level
			HttpComponent.fillUserInfoNoMoreByIds(userMap);
			
			JSONObject jsonUser = null;
			for(Map.Entry<Long, FeedActivityUser>  entry : userMap.entrySet()) {
				long userId = entry.getKey();
				FeedActivityUser user = entry.getValue();
				
				//是上传图片的
//				boolean havePic = false;
//				if(condition.havePic) {
//					boolean webHavePic = HtmlTagFilter.findImg(user.getContent());
//					boolean appHavePic = !StringUtil.isNullOrEmpty(user.getPictures());
//					havePic = webHavePic || appHavePic;
//				}
				
				//过滤版主管理员
				boolean banzuAndadmin = false;
				boolean isBanzu = false;
				boolean isAdmin = false;
				if(condition.admin) {
					isBanzu = sysUserRoleService.exists(user.getForumId(), userId);
					isAdmin = adminUserService.exists(userId);
					if(isBanzu && isAdmin) banzuAndadmin = true;
				}
				
				//是否被禁言
//				boolean isProhibitAction =  blackListService.exists(user.getForumId(), userId);
				
				//如果相符
				if( (banzuAndadmin == condition.admin) ) {
					jsonUser = new JSONObject();
					jsonUser.put("user_id", userId);
					jsonUser.put("nickname", user.getNickName());
					jsonUser.put("position", user.getPostion());
					jsonUser.put("level", user.getLevel());
					data.put(jsonUser);
				}
				
			}
			
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
