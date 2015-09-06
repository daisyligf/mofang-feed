package com.mofang.feed.controller.v3.admin.forum;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.logic.admin.FeedForumLogic;
import com.mofang.feed.logic.admin.impl.FeedForumLogicImpl;
import com.mofang.feed.model.FeedForum;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v3/backend/forum/edit")
public class ForumEditAction extends AbstractActionExecutor
{
	private FeedForumLogic logic = FeedForumLogicImpl.getInstance();

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
		long forumId = json.optLong("fid", 0L);
		String name = json.optString("name", "");
		String color = json.optString("color", "");
		String icon = json.optString("icon", "");
		int type = json.optInt("type", ForumType.HOT_FORUM);
		int gameId = json.optInt("game_id", 0);
		boolean isHidden = json.optBoolean("is_hidden", false);
		JSONArray arrTags = json.optJSONArray("tags");
		
		///参数检查
		if(forumId < 0 || StringUtil.isNullOrEmpty(name))
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
		forumInfo.setForumId(forumId);
		forumInfo.setGameId(gameId);
		forumInfo.setName(name);
		forumInfo.setIcon(icon);
		forumInfo.setColor(color);
		forumInfo.setType(type);
		forumInfo.setHidden(isHidden);
		
		Set<Integer> tagSet = new HashSet<Integer>();
		for(int i=0; i<arrTags.length(); i++)
			tagSet.add(arrTags.getInt(i));
		
		forumInfo.setTags(tagSet);
		
		return logic.edit(forumInfo, operatorId);
	}
}