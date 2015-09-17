package com.mofang.feed.logic.admin.impl;

import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedStatisticsLogic;
import com.mofang.feed.model.external.ForumStatisticsInfo;
import com.mofang.feed.service.FeedStatisticsService;
import com.mofang.feed.service.impl.FeedStatisticsServiceImpl;

public class FeedStatisticsLogicImpl implements FeedStatisticsLogic {

	private static final  FeedStatisticsLogicImpl LOGIC = new FeedStatisticsLogicImpl();
	private FeedStatisticsService statisticsService = FeedStatisticsServiceImpl.getInstance();
	
	private FeedStatisticsLogicImpl(){}
	
	public static FeedStatisticsLogicImpl getInstance() {
		return LOGIC;
	}
	
	@Override
	public ResultValue forumStatisticsInfos(Set<Long> forumIds, long startTime,
			long endTime) throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			Map<Long, ForumStatisticsInfo> statisticsMap = statisticsService.forumStatisticsInfos(forumIds, startTime, endTime);
			JSONObject infoJson = null;
			for(Map.Entry<Long, ForumStatisticsInfo> entry : statisticsMap.entrySet()) {
				long forumId = entry.getKey();
				ForumStatisticsInfo info = entry.getValue();
				
				infoJson = new JSONObject();
				infoJson.put("forum_id", forumId);
				infoJson.put("type", info.type);
				infoJson.put("forum_name", info.name);
				infoJson.put("thread_count", info.threadCount);
				infoJson.put("post_count", info.postCount);
				infoJson.put("comment_count", info.commentCount);
				data.put(infoJson);
			}
			
			result.setData(data);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception(
					"at FeedStatisticsLogicImpl.forumStatisticsInfos throw an error.", e);
		}

	}

}
