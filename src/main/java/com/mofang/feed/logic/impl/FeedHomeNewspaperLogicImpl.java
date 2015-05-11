package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeNewspaperLogic;
import com.mofang.feed.model.FeedHomeNewspaper;
import com.mofang.feed.service.FeedHomeNewspaperService;
import com.mofang.feed.service.impl.FeedHomeNewspaperServiceImpl;

public class FeedHomeNewspaperLogicImpl implements FeedHomeNewspaperLogic {

	private static final FeedHomeNewspaperLogicImpl LOGIC = new FeedHomeNewspaperLogicImpl();
	private FeedHomeNewspaperService newsPaperService = FeedHomeNewspaperServiceImpl.getInstance();
	
	public static FeedHomeNewspaperLogicImpl getInstance(){
		return LOGIC;
	}
	
	private FeedHomeNewspaperLogicImpl(){}
	

	@Override
	public ResultValue update(List<FeedHomeNewspaper> modelList) throws Exception {
		try {
			ResultValue result = new ResultValue();
			newsPaperService.update(modelList);
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
			List<FeedHomeNewspaper> list = newsPaperService.getList();
			if(list != null){
				JSONObject objNewspaper = null;
				for(FeedHomeNewspaper model : list){
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
			throw new Exception("at FeedHomeNewspaperLogicImpl.delete getList an error.", e);
		}
	}

}
