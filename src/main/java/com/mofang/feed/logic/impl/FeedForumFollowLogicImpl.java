package com.mofang.feed.logic.impl;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedForumFollowLogic;
import com.mofang.feed.service.FeedForumFollowService;
import com.mofang.feed.service.impl.FeedForumFollowServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumFollowLogicImpl implements FeedForumFollowLogic
{
	private final static FeedForumFollowLogicImpl LOGIC = new FeedForumFollowLogicImpl();
	private FeedForumFollowService followService = FeedForumFollowServiceImpl.getInstance();
	
	private FeedForumFollowLogicImpl()
	{}
	
	public static FeedForumFollowLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue follow(long forumId, long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断用户是否已经关注该版块
			if(followService.isFollow(forumId, userId))
			{
				result.setCode(ReturnCode.USER_FOLLOWED_FORUM);
				result.setMessage(ReturnMessage.USER_FOLLOWED_FORUM);
				return result;
			}
			
			///保存关注信息
			followService.follow(forumId, userId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumFollowLogicImpl.follow throw an error.", e);
		}
	}

	@Override
	public ResultValue cancel(long forumId, long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断用户是否已经关注该版块
			if(!followService.isFollow(forumId, userId))
			{
				result.setCode(ReturnCode.USER_UNFOLLOW_FORUM);
				result.setMessage(ReturnMessage.USER_UNFOLLOW_FORUM);
				return result;
			}
			
			///取消关注信息
			followService.cancel(forumId, userId);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedForumFollowLogicImpl.cancel throw an error.", e);
		}
	}
}