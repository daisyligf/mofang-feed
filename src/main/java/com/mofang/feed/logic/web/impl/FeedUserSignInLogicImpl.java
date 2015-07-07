package com.mofang.feed.logic.web.impl;

import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedUserSignInLogic;
import com.mofang.feed.service.FeedUserSignInService;
import com.mofang.feed.service.impl.FeedUserSignInServiceImpl;

public class FeedUserSignInLogicImpl implements FeedUserSignInLogic {

	private static final FeedUserSignInLogicImpl LOGIC = new FeedUserSignInLogicImpl();
	private FeedUserSignInService signInService = FeedUserSignInServiceImpl.getInstance();
	
	private FeedUserSignInLogicImpl(){}
	
	public static FeedUserSignInLogicImpl getInstance() {
		return LOGIC;
	}
	
	@Override
	public ResultValue sign(long userId) throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			signInService.sign(userId);
			
			data.put("is_sign_in", true);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedUserSignInLogicImpl.sign throw an error.", e);
		}
	}

	@Override
	public ResultValue isSignIned(long userId) throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			boolean flag = signInService.isSignIned(userId);
			data.put("is_sign_in", flag);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedUserSignInLogicImpl.isSignIned throw an error.", e);
		}
	}

}