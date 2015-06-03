package com.mofang.feed.logic.app.impl;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.app.FeedUserFavoriteLogic;
import com.mofang.feed.model.FeedUserFavorite;
import com.mofang.feed.service.FeedUserFavoriteService;
import com.mofang.feed.service.impl.FeedUserFavoriteServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedUserFavoriteLogicImpl implements FeedUserFavoriteLogic
{
	private final static FeedUserFavoriteLogicImpl LOGIC = new FeedUserFavoriteLogicImpl();
	private FeedUserFavoriteService favoriteService = FeedUserFavoriteServiceImpl.getInstance();
	
	private FeedUserFavoriteLogicImpl()
	{}
	
	public static FeedUserFavoriteLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue add(FeedUserFavorite model) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///判断是否已经收藏该主题
			boolean exists = favoriteService.exists(model.getUserId(), model.getThreadId());
			if(exists)
			{
				result.setCode(ReturnCode.FAVORITE_HAS_EXISTS);
				result.setMessage(ReturnMessage.FAVORITE_HAS_EXISTS);
				return result;
			}
			
			favoriteService.add(model);
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserFavoriteLogicImpl.add throw an error.", e);
		}
	}

	@Override
	public ResultValue delete(long userId, long threadId) throws Exception
	{
		try
		{
			favoriteService.delete(userId, threadId);
			
			///返回结果
			ResultValue result = new ResultValue();
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedUserFavoriteLogicImpl.delete throw an error.", e);
		}
	}
}