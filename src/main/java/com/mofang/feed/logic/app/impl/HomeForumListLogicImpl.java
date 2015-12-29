package com.mofang.feed.logic.app.impl;

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
import com.mofang.feed.logic.app.HomeForumListLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.model.FeedHomeRecommendGame;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeHotForumService;
import com.mofang.feed.service.FeedHomeRecommendGameService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeHotForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeRecommendGameServiceImpl;

public class HomeForumListLogicImpl implements HomeForumListLogic{

	private static final HomeForumListLogicImpl LOGIC = new HomeForumListLogicImpl();
	private FeedHomeHotForumService hotForumService = FeedHomeHotForumServiceImpl.getInstance();
	private FeedHomeRecommendGameService recommendGameService = FeedHomeRecommendGameServiceImpl.getInstance();
	public FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private HomeForumListLogicImpl(){}
	
	public static HomeForumListLogicImpl getInstatnce() {
		return LOGIC;
	}
	
	@Override
	public ResultValue getHomeFourmList() throws Exception {

		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			JSONArray arrayPrefecture = new JSONArray();
			JSONArray arrayHotForum = new JSONArray();
			JSONArray arrayRecommendGame = new JSONArray();
			JSONObject objPrefectureOut = new JSONObject();
			JSONObject objHotForumOut = new JSONObject();
			JSONObject objRecommendGameOut = new JSONObject();
			
			StringBuilder forumIds = new StringBuilder();
			
			//首页综合专区信息
			String forumIdsStr = GlobalConfig.HOME_PREFECTURE_IDS;
			String[] forumIdArr = forumIdsStr.split(",");
			JSONObject objPrefecture = null;
			//综合专区版块id
			forumIds.append(forumIdsStr);
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
				
				///帖子数=帖子数 + 楼层数 + 评论数 (replies = 楼层数 + 评论数)
				objPrefecture.put("total_threads", forum.getThreads() + forum.getReplies());
				
				arrayPrefecture.put(objPrefecture);
			}
			objPrefectureOut.put("prefectureList", arrayPrefecture);
			data.put(objPrefectureOut);
			
			
			
			//首页热门游戏版块信息
			List<FeedHomeHotForum> hotForumList = hotForumService.getList();
			JSONObject objHotForum = null;
			if(hotForumList != null){
				
				Set<Integer> gameIds = new HashSet<Integer>();
				for(FeedHomeHotForum model : hotForumList){
					objHotForum = new JSONObject();
					
					long forumId = model.getForumId();
					FeedForum feedForum = forumService.getInfo(forumId);
					if(feedForum == null)
						continue;
					//热门游戏版块ids
					forumIds.append(",").append(forumId);
					objHotForum.put("forum_id", forumId);
					objHotForum.put("forum_name", feedForum.getName());
					objHotForum.put("game_id", feedForum.getGameId());
					objHotForum.put("icon", feedForum.getIcon());
					objHotForum.put("today_threads", feedForum.getTodayThreads());
					///帖子数=帖子数 + 楼层数 + 评论数 (replies = 楼层数 + 评论数)
					objHotForum.put("total_threads", feedForum.getThreads() + feedForum.getReplies());
					objHotForum.put("prefecture_url", model.getPrefectureUrl());
					objHotForum.put("gift_url", model.getGiftUrl());
					arrayHotForum.put(objHotForum);
					
					gameIds.add(feedForum.getGameId());
				}
				
				///批量获取游戏简介并填充
				Map<Integer, String> map = HttpComponent.getGameCommentByIds(gameIds);
				if(null != map)
				{
					for(int i=0; i<arrayHotForum.length(); i++)
					{
						objHotForum = arrayHotForum.optJSONObject(i);
						int gameId = objHotForum.optInt("game_id", 0);
						if(map.containsKey(gameId))
							objHotForum.put("comment", map.get(gameId));
					}
				}
			}
			objHotForumOut.put("hotForumList", arrayHotForum);
			data.put(objHotForumOut);
			
			
			
			//首页新游推荐版块信息
			List<FeedHomeRecommendGame> recommendGameList = recommendGameService.getList();
			JSONObject objRecommendGame = null;
			if(recommendGameList != null){
				
				Set<Integer> gameIds = new HashSet<Integer>();
				for(FeedHomeRecommendGame model : recommendGameList){
					long forumId = model.getForumId();
					FeedForum feedForum = forumService.getInfo(forumId);
					if(feedForum == null)
						continue;
					
					//热门游戏版块ids
					forumIds.append(",").append(forumId);
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
					arrayRecommendGame.put(objRecommendGame);
					
					gameIds.add(feedForum.getGameId());
				}
				
				///批量获取游戏简介并填充
				Map<Integer, String> map = HttpComponent.getGameCommentByIds(gameIds);
				if(null != map)
				{
					for(int i=0; i<arrayRecommendGame.length(); i++)
					{
						objRecommendGame = arrayRecommendGame.optJSONObject(i);
						int gameId = objRecommendGame.optInt("game_id", 0);
						if(map.containsKey(gameId))
							objRecommendGame.put("comment", map.get(gameId));
					}
				}
			}
			objRecommendGameOut.put("recommendGameList", arrayRecommendGame);
			data.put(objRecommendGameOut);
			
			///批量获取版块关注并填充
			Map<Long, Integer> mapFollows = HttpComponent.getForumFollowsByForumIds(forumIds.toString());
			if(null != mapFollows)
			{
				for(int i=0; i<arrayPrefecture.length(); i++)
				{	
					objPrefecture = arrayPrefecture.optJSONObject(i);
					long forumId = objPrefecture.optLong("forum_id", 0);
					if(mapFollows.containsKey(forumId))
						objPrefecture.put("follow_num", mapFollows.get(forumId));
				}
				
				for(int i=0; i<arrayHotForum.length(); i++)
				{	
					objHotForum = arrayHotForum.optJSONObject(i);
					long forumId = objHotForum.optLong("forum_id", 0);
					if(mapFollows.containsKey(forumId))
						objHotForum.put("follow_num", mapFollows.get(forumId));
				}
				
				for(int i=0; i<arrayRecommendGame.length(); i++)
				{	
					objRecommendGame = arrayRecommendGame.optJSONObject(i);
					long forumId = objRecommendGame.optLong("forum_id", 0);
					if(mapFollows.containsKey(forumId))
						objRecommendGame.put("follow_num", mapFollows.get(forumId));
				}
			}
			
			
			
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at HomeForumListLogicImpl.getHomeFourmList throw an error.",e);
		}
	
	}

}
