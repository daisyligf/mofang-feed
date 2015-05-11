package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeForumRankLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeForumRank;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeForumRankService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeForumRankServiceImpl;

public class FeedHomeForumRankLogicImpl implements FeedHomeForumRankLogic {

	private static final FeedHomeForumRankLogicImpl LOGIC = new FeedHomeForumRankLogicImpl();
	private FeedHomeForumRankService forumRankService = FeedHomeForumRankServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedHomeForumRankLogicImpl(){}
	
	public static FeedHomeForumRankLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue update(List<FeedHomeForumRank> modelList) throws Exception {
		try {
			ResultValue result = new ResultValue();
			forumRankService.update(modelList);
			
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
			List<FeedHomeForumRank> list = forumRankService.getList();
			if(list != null){
				JSONObject objForumRank = null;
				for(FeedHomeForumRank model : list){
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
