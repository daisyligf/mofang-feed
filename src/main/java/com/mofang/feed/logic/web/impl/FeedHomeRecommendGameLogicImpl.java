package com.mofang.feed.logic.web.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.web.FeedHomeRecommendGameLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeRecommendGame;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.Game;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeRecommendGameService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeRecommendGameServiceImpl;

public class FeedHomeRecommendGameLogicImpl implements
		FeedHomeRecommendGameLogic {

	private static final FeedHomeRecommendGameLogicImpl LOGIC = new FeedHomeRecommendGameLogicImpl();
	private FeedHomeRecommendGameService recommendGameService = FeedHomeRecommendGameServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	
	private FeedHomeRecommendGameLogicImpl(){}
	
	public static FeedHomeRecommendGameLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue edit(List<FeedHomeRecommendGame> modelList, long operatorId) throws Exception {
		try {
			ResultValue result = new ResultValue();
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege) {
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			for(FeedHomeRecommendGame model : modelList){
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
			recommendGameService.edit(modelList);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeRecommendGameLogicImp.update throw an error.",e);
		}
	}

	@Override
	public ResultValue getList() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedHomeRecommendGame> list = recommendGameService.getList();
			if(list != null){
				JSONObject objRecommendGame = null;
				Set<Integer> gameIds = new HashSet<Integer>();
				for(FeedHomeRecommendGame model : list){
					long forumId = model.getForumId();
					FeedForum feedForum = forumService.getInfo(forumId);
					if(feedForum == null)
						continue;
					
					objRecommendGame = new JSONObject();
					objRecommendGame.put("forum_id", forumId);
					objRecommendGame.put("forum_name", feedForum.getName());
					objRecommendGame.put("game_id", feedForum.getGameId());
					objRecommendGame.put("icon", feedForum.getIcon());
					objRecommendGame.put("today_threads", feedForum.getTodayThreads());
					///帖子数=帖子数 + 楼层数 + 评论数 (replies = 楼层数 + 评论数)
					objRecommendGame.put("total_threads", feedForum.getThreads() + feedForum.getReplies());
					objRecommendGame.put("download_url", model.getDownloadUrl());
					objRecommendGame.put("gift_url", model.getGiftUrl());
					data.put(objRecommendGame);
					
					gameIds.add(feedForum.getGameId());
				}
				
				///批量获取游戏简介并填充
				Map<Integer, String> map = HttpComponent.getGameCommentByIds(gameIds);
				if(null != map)
				{
					for(int i=0; i<data.length(); i++)
					{
						objRecommendGame = data.optJSONObject(i);
						int gameId = objRecommendGame.optInt("game_id", 0);
						if(map.containsKey(gameId))
							objRecommendGame.put("comment", map.get(gameId));
					}
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeRecommendGameLogicImp.update throw an error.",e);
		}
	}

	@Override
	public ResultValue getListByLetterGroup(String letterGroup, int pageNum,
			int pageSize) throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			Page<FeedHomeRecommendGame> page = recommendGameService.getListByLetterGroup(letterGroup, pageNum, pageSize);
			long total = page.getTotal();
			data.put("total", total);
			JSONArray jsonArray = new JSONArray();
			List<FeedHomeRecommendGame> list = page.getList();
			JSONObject objRecommendGame = null;
			for(FeedHomeRecommendGame model : list){
				long forumId = model.getForumId();
				FeedForum feedForum = forumService.getInfo(forumId);
				if(feedForum == null)
					continue;
				
				objRecommendGame = new JSONObject();
				objRecommendGame.put("fid", forumId);
				objRecommendGame.put("name", feedForum.getName());
				objRecommendGame.put("game_id", feedForum.getGameId());
				objRecommendGame.put("icon", feedForum.getIcon());
				objRecommendGame.put("today_threads", feedForum.getTodayThreads());
				objRecommendGame.put("total_threads", feedForum.getThreads());
				objRecommendGame.put("download_url", model.getDownloadUrl());
				objRecommendGame.put("gift_url", model.getGiftUrl());
				objRecommendGame.put("prefecture_url", "");
				jsonArray.put(objRecommendGame);
			}
			data.put("list", jsonArray);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeRecommendGameLogicImpl.getListByLetterGroup throw an error.", e);
		}
	}

}
