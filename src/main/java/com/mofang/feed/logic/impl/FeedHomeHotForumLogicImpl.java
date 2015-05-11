package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedHomeHotForumLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeHotForumService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeHotForumServiceImpl;

public class FeedHomeHotForumLogicImpl implements FeedHomeHotForumLogic {

	private static final FeedHomeHotForumLogicImpl LOGIC = new FeedHomeHotForumLogicImpl();
	private FeedHomeHotForumService hotForumService = FeedHomeHotForumServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedHomeHotForumLogicImpl(){}
	
	public static FeedHomeHotForumLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue update(List<FeedHomeHotForum> modelList)
			throws Exception {
		try {
			ResultValue result = new ResultValue();
			for(FeedHomeHotForum model : modelList){
				long forumId = model.getForumId();
				
				//设置专区地址
				String prefectureUrl = HttpComponent.getPrefectureUrl(forumId);
				model.setPrefectureUrl(prefectureUrl);
				
				/*
				 * 设置礼包地址
				 * 1、通过该game_id判断是否有礼包
				 * 2、如果有礼包，设置礼包地址s
				 */
				FeedForum forum = forumService.getInfo(forumId);
				boolean flag = HttpComponent.checkGift(forum.getGameId());
				if(flag){
					model.setGiftUrl(GlobalConfig.GIFT_INFO_URL + forum.getName());
				}
			}
			hotForumService.update(modelList);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeHotForumLogicImpl.update throw an error.", e);
		}
	}

	@Override
	public ResultValue getList() throws Exception {
		try{
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedHomeHotForum> list = hotForumService.getList();
			if(list != null){
				JSONObject objHotForum = null;
				for(FeedHomeHotForum model : list){
					objHotForum = new JSONObject();
					
					long forumId = model.getForumId();
					
					FeedForum feedForum = forumService.getInfo(forumId);
					
					objHotForum.put("forum_id", forumId);
					objHotForum.put("forum_name", feedForum.getName());
					objHotForum.put("icon", feedForum.getIcon());
					objHotForum.put("today_threads", feedForum.getTodayThreads());
					objHotForum.put("total_threads", feedForum.getTodayThreads());
					objHotForum.put("forum_url", GlobalConfig.FORUM_DETAIL_URL + "?fid=" + forumId);
					objHotForum.put("prefecture_url", model.getPrefectureUrl());//专区地址 调用产品库
					objHotForum.put("gift_url", model.getGiftUrl());//自己拼
					
					data.put(objHotForum);
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeHotForumLogicImpl.getList throw an error.", e);
		}
	}

}