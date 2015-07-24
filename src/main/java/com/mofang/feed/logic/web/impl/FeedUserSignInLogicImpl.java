package com.mofang.feed.logic.web.impl;

import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedUserSignInLogic;
import com.mofang.feed.model.external.SignInResult;
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
			SignInResult signInResult = signInService.sign(userId);
			
			data.put("is_sign_in", signInResult.isSignIn);
			data.put("days", signInResult.days);
			data.put("rank", signInResult.rank);
			data.put("totalMember", signInResult.totalMember);
			data.put("is_repeat", signInResult.repeat);
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
			SignInResult signInResult = signInService.getResult(userId);
			data.put("is_sign_in", signInResult.isSignIn);
			data.put("days", signInResult.days);
			data.put("rank", signInResult.rank);
			data.put("totalMember", signInResult.totalMember);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedUserSignInLogicImpl.isSignIned throw an error.", e);
		}
	}

	@Override
	public ResultValue totalMember() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			int totalMember = signInService.totalMember();
			data.put("is_sign_in", false);
			data.put("days", 0);
			data.put("rank", 0);
			data.put("totalMember", totalMember);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedUserSignInLogicImpl.totalMember throw an error.", e);
		}
	}

}
