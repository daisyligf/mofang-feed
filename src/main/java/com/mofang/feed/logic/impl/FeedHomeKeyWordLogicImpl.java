package com.mofang.feed.logic.impl;

import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeKeyWordLogic;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedHomeKeyWordService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedHomeKeyWordServiceImpl;

public class FeedHomeKeyWordLogicImpl implements FeedHomeKeyWordLogic {

	private static final FeedHomeKeyWordLogicImpl LOGIC = new FeedHomeKeyWordLogicImpl();
	private FeedHomeKeyWordService keyWordService = FeedHomeKeyWordServiceImpl
			.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();

	private FeedHomeKeyWordLogicImpl() {
	}

	public static FeedHomeKeyWordLogicImpl getInstance() {
		return LOGIC;
	}

	@Override
	public ResultValue setKeyWord(String word, long operatorId) throws Exception {
		try {
			ResultValue result = new ResultValue();
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege) {
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			keyWordService.setKeyWord(word);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception(
					"at FeedHomeKeyWordLogicImpl.setKeyWord throw an error.", e);
		}

	}

	@Override
	public ResultValue getKeyWord() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			String keyWord = keyWordService.getKeyWord();
			data.put("key_word", keyWord);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception(
					"at FeedHomeKeyWordLogicImpl.getKeyWord throw an error.", e);
		}
	}

}
