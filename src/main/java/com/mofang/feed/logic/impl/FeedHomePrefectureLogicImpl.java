package com.mofang.feed.logic.impl;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomePrefectureLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;

public class FeedHomePrefectureLogicImpl implements FeedHomePrefectureLogic {

	private static final FeedHomePrefectureLogicImpl LOGIC = new FeedHomePrefectureLogicImpl();
	public FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedHomePrefectureLogicImpl(){}
	
	public static FeedHomePrefectureLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue getList() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			String forumIdsStr = GlobalConfig.HOME_PREFECTURE_IDS;
			String[] forumIdArr = forumIdsStr.split(",");
			JSONObject objPrefecture = null;
			for(String forumIdStr : forumIdArr){
				long forumId = Long.valueOf(forumIdStr);
				FeedForum forum = forumService.getInfo(forumId);
				if(forum ==null)
					continue;
				objPrefecture = new JSONObject();
				objPrefecture.put("forum_id", forumId);
				objPrefecture.put("forum_name", forum.getName());
				objPrefecture.put("icon", forum.getIcon());
				objPrefecture.put("today_threads", forum.getTodayThreads());
				objPrefecture.put("totay_threads", forum.getTodayThreads());
				
				data.put(objPrefecture);
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomePrefectureLogicImpl.getList throw an error.",e);
		}
	}

}
