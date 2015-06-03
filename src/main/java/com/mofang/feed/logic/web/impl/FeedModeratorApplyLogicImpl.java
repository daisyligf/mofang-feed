package com.mofang.feed.logic.web.impl;

import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedModeratorApplyLogic;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.ModeratorApplyCondition;
import com.mofang.feed.service.FeedModeratorApplyService;
import com.mofang.feed.service.impl.FeedModeratorApplyServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModeratorApplyLogicImpl implements FeedModeratorApplyLogic
{	
	private static final FeedModeratorApplyLogicImpl LOGIC = new FeedModeratorApplyLogicImpl();
	private FeedModeratorApplyService applyService = FeedModeratorApplyServiceImpl.getInstance();

	private FeedModeratorApplyLogicImpl()
	{}
	
	public static FeedModeratorApplyLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue check(long forumId, long userId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			ModeratorApplyCondition condition = applyService.checkCondition(userId, forumId, false);
			boolean isPass = false;
			if(condition.isFollowForumIsOK() && condition.isThreadsIsOK() && condition.isTimeIntervalIsOK() && condition.isTopEliteCountIsOK())
			{
				isPass = true;
				result.setCode(ReturnCode.SUCCESS);
				result.setMessage(ReturnMessage.SUCCESS);
			}
			else
			{
				result.setCode(ReturnCode.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
				result.setMessage(ReturnMessage.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
			}
			
			JSONObject data = new JSONObject();
			data.put("is_pass", isPass);
			data.put("is_follow_ok", condition.isFollowForumIsOK());
			data.put("is_threads_ok", condition.isThreadsIsOK());
			data.put("is_interval_ok", condition.isTimeIntervalIsOK());
			data.put("is_elitecount_ok", condition.isTopEliteCountIsOK());
			result.setData(data);
			return result;
		}
		catch (Exception e)
		{
			throw new Exception("at FeedModeratorApplyLogicImpl.check throw an error.", e);
		}
	}
	
	@Override
	public ResultValue apply(FeedModeratorApply model) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			ModeratorApplyCondition condition = applyService.checkCondition(model.getUserId(), model.getForumId(), false);
			boolean isPass = false;
			if(condition.isFollowForumIsOK() && condition.isThreadsIsOK() && condition.isTimeIntervalIsOK() && condition.isTopEliteCountIsOK())
			{
				applyService.add(model);
				isPass = true;
				result.setCode(ReturnCode.SUCCESS);
				result.setMessage(ReturnMessage.SUCCESS);
			}
			else
			{
				result.setCode(ReturnCode.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
				result.setMessage(ReturnMessage.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
			}
			
			JSONObject data = new JSONObject();
			data.put("is_pass", isPass);
			data.put("is_follow_ok", condition.isFollowForumIsOK());
			data.put("is_threads_ok", condition.isThreadsIsOK());
			data.put("is_interval_ok", condition.isTimeIntervalIsOK());
			data.put("is_elitecount_ok", condition.isTopEliteCountIsOK());
			result.setData(data);
			return result;
		}
		catch (Exception e)
		{
			throw new Exception("at FeedModeratorApplyLogicImpl.apply throw an error.", e);
		}
	}
}