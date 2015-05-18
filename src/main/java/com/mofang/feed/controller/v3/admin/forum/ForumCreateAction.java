package com.mofang.feed.controller.v3.admin.forum;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.logic.FeedForumLogic;
import com.mofang.feed.logic.impl.FeedForumLogicImpl;
import com.mofang.feed.model.FeedForum;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/forum/create")
public class ForumCreateAction extends AbstractActionExecutor
{
	private FeedForumLogic logic = FeedForumLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("uid");
		if(!StringUtil.isLong(strUserId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		long operatorId = Long.parseLong(strUserId);
		JSONObject json = new JSONObject(postData);
		long parentId = json.optLong("parent_id", 0L);
		String name = json.optString("name", "");
		String color = json.optString("color", "");
		String icon = json.optString("icon", "");
		int type = json.optInt("type", ForumType.HOT_FORUM);
		int gameId = json.optInt("game_id", 0);
		JSONArray arrTags = json.optJSONArray("tags");
		
		///参数检查
		if(StringUtil.isNullOrEmpty(name))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		if(type != ForumType.OFFICAL)
		{
			if(gameId <= 0)
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
		}
		else
		{
			if(StringUtil.isNullOrEmpty(icon))
			{
				result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
				result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
				return result;
			}
		}
		
		///构造Forum实体对象
		FeedForum forumInfo = new FeedForum();
		forumInfo.setParentId(parentId);
		forumInfo.setName(name);
		forumInfo.setIcon(icon);
		forumInfo.setColor(color);
		forumInfo.setType(type);
		forumInfo.setEdit(true);
		forumInfo.setHidden(false);
		
		return logic.add(forumInfo, operatorId);
	}
}