package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeHotForumRankLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeHotForumRankService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeHotForumRankServiceImpl;

public class FeedHomeHotForumRankLogicImpl implements FeedHomeHotForumRankLogic {

	private static final FeedHomeHotForumRankLogicImpl LOGIC = new FeedHomeHotForumRankLogicImpl();
	private FeedHomeHotForumRankService forumRankService = FeedHomeHotForumRankServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedHomeHotForumRankLogicImpl(){}
	
	public static FeedHomeHotForumRankLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue edit(List<FeedHomeHotForumRank> modelList) throws Exception {
		try {
			ResultValue result = new ResultValue();
			forumRankService.edit(modelList);
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeForumRankLogicImpl.update throw an error.", e);
		}
	}

	@Override
	public ResultValue getList() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedHomeHotForumRank> list = forumRankService.getList();
			if(list != null){
				JSONObject objForumRank = null;
				for(FeedHomeHotForumRank model : list){
					objForumRank = new JSONObject();
					
					long forumId = model.getForumId();
					FeedForum forum = forumService.getInfo(forumId);
					if(forum == null)
						continue;
					
					objForumRank.put("forum_id", forumId);
					objForumRank.put("forum_name", forum.getName());
					objForumRank.put("up_down", model.getUpDown());
					
					data.put(objForumRank);
					
				}
			}
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeForumRankLogicImpl.getList throw an error.", e);
		}
	}

}