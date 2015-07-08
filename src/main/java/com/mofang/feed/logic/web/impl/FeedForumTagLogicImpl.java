package com.mofang.feed.logic.web.impl;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedForumTagLogic;
import com.mofang.feed.service.FeedForumTagService;
import com.mofang.feed.service.FeedTagService;
import com.mofang.feed.service.impl.FeedForumTagServiceImpl;
import com.mofang.feed.service.impl.FeedTagServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumTagLogicImpl implements FeedForumTagLogic
{
	private final static FeedForumTagLogicImpl LOGIC  = new FeedForumTagLogicImpl();
	private FeedForumTagService forumTagService = FeedForumTagServiceImpl.getInstance();
	private FeedTagService tagService = FeedTagServiceImpl.getInstance();
	
	private FeedForumTagLogicImpl()
	{}
	
	public static FeedForumTagLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue getTagList(long forumId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			Set<Integer> tagSet = forumTagService.getTagIdListByForumId(forumId);
			if(tagSet != null)
			{
				JSONObject jsonTag = null;
				for(Integer tagId : tagSet)
				{
					jsonTag = new JSONObject();
					jsonTag.put("tag_id", tagId);
					jsonTag.put("tag_name", tagService.getTagName(tagId));
					data.put(jsonTag);
				}
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumTagLogicImpl.getTagList throw an error.", e);
		}
	}
}