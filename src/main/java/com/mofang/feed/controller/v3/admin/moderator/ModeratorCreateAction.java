package com.mofang.feed.controller.v3.admin.moderator;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedSysUserRoleLogic;
import com.mofang.feed.logic.admin.impl.FeedSysUserRoleLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/backend/moderator/create")
public class ModeratorCreateAction extends AbstractActionExecutor
{
	private FeedSysUserRoleLogic logic = FeedSysUserRoleLogicImpl.getInstance();
	private static int ROLE_ID = 1;   ///版主角色ID

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strOperatorId = context.getParameters("uid");
		if(!StringUtil.isLong(strOperatorId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
			return result;
		}
		
		long operatorId = Long.parseLong(strOperatorId);
		JSONObject json = new JSONObject(postData);
		long userId = json.optLong("user_id", 0L);
		JSONArray arrayForumIds = json.optJSONArray("fids");
		if(userId <= 0L)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		if(null == arrayForumIds || arrayForumIds.length() == 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		Set<Long> forumIdSet = new HashSet<Long>();
		long forumId = 0L;
		boolean isValid = true;
		for(int i=0; i<arrayForumIds.length(); i++)
		{
			String strForumId = arrayForumIds.getString(i);
			if(!StringUtil.isLong(strForumId))
			{
				isValid = false;
				break;
			}
			forumId = Long.parseLong(strForumId);
			forumIdSet.add(forumId);
		}
		
		if(!isValid)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		return logic.batchAdd(userId, forumIdSet, ROLE_ID, operatorId);
	}
}