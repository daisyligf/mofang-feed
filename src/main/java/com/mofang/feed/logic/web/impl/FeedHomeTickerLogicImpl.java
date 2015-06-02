package com.mofang.feed.logic.web.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedHomeTickerLogic;
import com.mofang.feed.model.FeedHomeTicker;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedHomeTickerService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedHomeTickerServiceImpl;

public class FeedHomeTickerLogicImpl implements FeedHomeTickerLogic {

	private static final FeedHomeTickerLogicImpl LOGIC = new FeedHomeTickerLogicImpl();
	private FeedHomeTickerService newsPaperService = FeedHomeTickerServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	
	public static FeedHomeTickerLogicImpl getInstance(){
		return LOGIC;
	}
	
	private FeedHomeTickerLogicImpl(){}
	

	@Override
	public ResultValue edit(List<FeedHomeTicker> modelList, long operatorId) throws Exception {
		try {
			ResultValue result = new ResultValue();
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege) {
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			newsPaperService.edit(modelList);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeNewspaperLogicImpl.update throw an error.", e);
		}
	}

	@Override
	public ResultValue getList() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedHomeTicker> list = newsPaperService.getList();
			if(list != null){
				JSONObject objNewspaper = null;
				for(FeedHomeTicker model : list){
					objNewspaper = new JSONObject();
					objNewspaper.put("icon", model.getIcon());
					objNewspaper.put("link_url", model.getLinkUrl());
					objNewspaper.put("display_order", model.getDisplayOrder());
					data.put(objNewspaper);
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeNewspaperLogicImpl.getList getList an error.", e);
		}
	}

}
