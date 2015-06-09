package com.mofang.feed.logic.web.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedHomeRecommendGameRankLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeRecommendGameRank;
import com.mofang.feed.model.external.Game;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeRecommendGameRankService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeRecommendGameRankServiceImpl;

public class FeedHomeRecommendGameRankLogicImpl implements
		FeedHomeRecommendGameRankLogic {

	private static final FeedHomeRecommendGameRankLogicImpl LOGIC = new FeedHomeRecommendGameRankLogicImpl();
	private FeedHomeRecommendGameRankService recommendGameRankService = FeedHomeRecommendGameRankServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	
	private FeedHomeRecommendGameRankLogicImpl(){}
	
	public static FeedHomeRecommendGameRankLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue edit(List<FeedHomeRecommendGameRank> modelList, long operatorId)
			throws Exception {
		try {
			ResultValue result = new ResultValue();
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege) {
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			for(FeedHomeRecommendGameRank model : modelList){
				long forumId = model.getForumId();
				FeedForum forum = forumService.getInfo(forumId);
				if(forum == null){
					result.setCode(ReturnCode.FORUM_NOT_EXISTS);
					result.setMessage(ReturnMessage.FORUM_NOT_EXISTS);
					return result;
				}
				int gameId = forum.getGameId();
				//设置下载地址
				model.setDownloadUrl(GlobalConfig.GAME_DOWNLOAD_URL + gameId);
				//设置礼包地址
				boolean flag = HttpComponent.checkGift(gameId);
				if(flag){
					Game game = HttpComponent.getGameInfo(gameId);
					if(game != null) {
						model.setGiftUrl(GlobalConfig.GIFT_INFO_URL + game.getName());
					}
				}
			}
			recommendGameRankService.edit(modelList);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeRecommendGameRankLogicImpl.update throw an error.",e);
		}
	}

	@Override
	public ResultValue getList() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedHomeRecommendGameRank> list = recommendGameRankService.getList();
			if(list != null){
				JSONObject objRecommendGameRank = null;
				for(FeedHomeRecommendGameRank model : list){
					long forumId = model.getForumId();
					FeedForum forum = forumService.getInfo(forumId);
					if(forum == null)
						continue;
					objRecommendGameRank = new JSONObject();
					
					objRecommendGameRank.put("forum_id", forumId);
					objRecommendGameRank.put("forum_name", forum.getName());
					objRecommendGameRank.put("icon", forum.getIcon());
					//objRecommendGameRank.put("link_url", GlobalConfig.FORUM_DETAIL_URL + "?fid=" + forumId);
					objRecommendGameRank.put("download_url", model.getDownloadUrl());
					objRecommendGameRank.put("gift_url", model.getGiftUrl());
					data.put(objRecommendGameRank);
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeRecommendGameRankLogicImpl.getList throw an error.",e);
		}
	}

}
