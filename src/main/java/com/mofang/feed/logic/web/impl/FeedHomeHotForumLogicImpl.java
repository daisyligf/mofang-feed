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
import com.mofang.feed.logic.web.FeedHomeHotForumLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.Game;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeHotForumService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeHotForumServiceImpl;
import com.mofang.framework.util.StringUtil;

public class FeedHomeHotForumLogicImpl implements FeedHomeHotForumLogic {

	private static final FeedHomeHotForumLogicImpl LOGIC = new FeedHomeHotForumLogicImpl();
	private FeedHomeHotForumService hotForumService = FeedHomeHotForumServiceImpl.getInstance();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedHomeHotForumLogicImpl(){}
	
	public static FeedHomeHotForumLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue edit(List<FeedHomeHotForum> modelList, long operatorId)
			throws Exception {
		try {
			ResultValue result = new ResultValue();
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege) {
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			for(FeedHomeHotForum model : modelList){
				long forumId = model.getForumId();
				FeedForum forum = forumService.getInfo(forumId);
				if(forum == null){
					result.setCode(ReturnCode.FORUM_NOT_EXISTS);
					result.setMessage(ReturnMessage.FORUM_NOT_EXISTS);
					return result;
				}
				//设置专区地址
				String prefectureUrl = HttpComponent.getPrefectureUrl(forumId);
				if(!StringUtil.isNullOrEmpty(prefectureUrl)) {
					model.setPrefectureUrl(prefectureUrl);
				}
				/*
				 * 设置礼包地址
				 * 1、通过该game_id判断是否有礼包
				 * 2、如果有礼包，设置礼包地址
				 */
				int gameId = forum.getGameId();
				boolean flag = HttpComponent.checkGift(gameId);
				if(flag){
					Game game = HttpComponent.getGameInfo(gameId);
					if(game != null) {
						model.setGiftUrl(GlobalConfig.GIFT_INFO_URL + game.getName());
					}else{
						model.setGiftUrl("");
					}
				}
			}
			hotForumService.edit(modelList);
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
				Set<Integer> gameIds = new HashSet<Integer>();
				for(FeedHomeHotForum model : list){
					objHotForum = new JSONObject();
					
					long forumId = model.getForumId();
					FeedForum feedForum = forumService.getInfo(forumId);
					if(feedForum == null)
						continue;
					objHotForum.put("forum_id", forumId);
					objHotForum.put("forum_name", feedForum.getName());
					objHotForum.put("game_id", feedForum.getGameId());
					objHotForum.put("icon", feedForum.getIcon());
					objHotForum.put("today_threads", feedForum.getTodayThreads());
					///帖子数=帖子数 + 楼层数 + 评论数 (replies = 楼层数 + 评论数)
					objHotForum.put("total_threads", feedForum.getThreads() + feedForum.getReplies());
					objHotForum.put("prefecture_url", model.getPrefectureUrl());
					objHotForum.put("gift_url", model.getGiftUrl());
					data.put(objHotForum);
					
					gameIds.add(feedForum.getGameId());
				}
				
				///批量获取游戏简介并填充
				Map<Integer, String> map = HttpComponent.getGameCommentByIds(gameIds);
				if(null != map)
				{
					for(int i=0; i<data.length(); i++)
					{
						objHotForum = data.optJSONObject(i);
						int gameId = objHotForum.optInt("game_id", 0);
						if(map.containsKey(gameId))
							objHotForum.put("comment", map.get(gameId));
					}
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

	@Override
	public ResultValue getListByLetterGroup(String letterGroup, int pageNum, int pageSize)
			throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			Page<FeedHomeHotForum> page = hotForumService.getListByLetterGroup(letterGroup, pageNum, pageSize);
			long total = page.getTotal();
			data.put("total", total);
			JSONArray jsonArray = new JSONArray();
			List<FeedHomeHotForum> list= page.getList();
			JSONObject objHotForum = null;
			for(FeedHomeHotForum model : list){
				objHotForum = new JSONObject();
				
				long forumId = model.getForumId();
				FeedForum feedForum = forumService.getInfo(forumId);
				if(feedForum == null)
					continue;
				objHotForum.put("fid", forumId);
				objHotForum.put("name", feedForum.getName());
				objHotForum.put("game_id", feedForum.getGameId());
				objHotForum.put("icon", feedForum.getIcon());
				objHotForum.put("today_threads", feedForum.getTodayThreads());
				objHotForum.put("total_threads", feedForum.getThreads());
				objHotForum.put("prefecture_url", model.getPrefectureUrl());
				objHotForum.put("gift_url", model.getGiftUrl());
				objHotForum.put("download_url", "");
				jsonArray.put(objHotForum);
			}
			data.put("list", jsonArray);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeHotForumLogicImpl.getListByLetterGroup throw an error.", e);
		}
	}

}
