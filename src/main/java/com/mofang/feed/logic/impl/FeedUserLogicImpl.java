package com.mofang.feed.logic.impl;

import org.json.JSONObject;

import com.mofang.feed.component.UserComponent;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedUserLogic;
import com.mofang.feed.model.external.User;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;

public class FeedUserLogicImpl implements FeedUserLogic {
	
	private static final FeedUserLogicImpl LOGIC = new FeedUserLogicImpl();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();

	
	private FeedUserLogicImpl(){}
	
	public static FeedUserLogicImpl getInstance(){
		return LOGIC;
	}

	@Override
	public ResultValue getInfo(long userId) throws Exception {
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			User userInfo = UserComponent.getInfo(userId);
			if(null == userInfo)
			{
				result.setCode(ReturnCode.USER_NOT_EXISTS);
				result.setMessage(ReturnMessage.USER_NOT_EXISTS);
				return result;				
			}
			
			data.put("user_id", userId);
			data.put("nickname", userInfo.getNickName());
			data.put("register_time", userInfo.getRegisterTime());
			///获取用户发帖总数
			long threads = threadService.getUserThreadCount(userId);
			//精华帖子数
			long eliteThreadCount = threadService.getUserEliteThreadCount(userId);
			///获取用户回帖总数
			long replies = postService.getUserReplyCount(userId);
			data.put("threads", threads);
			data.put("replies", replies);
			data.put("elite_threads", eliteThreadCount);
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserLogicImpl.getInfo throw an error.", e);
		}
	}

}
