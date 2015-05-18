package com.mofang.feed.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.global.common.UpDownStatus;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.feed.model.FeedHomeRecommendGameRank;
import com.mofang.feed.model.external.FeedForumOrder;
import com.mofang.feed.model.external.ForumCount;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedForumFollowDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.StatForumViewHistoryDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedForumFollowDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.mysql.impl.StatForumViewHistoryDaoImpl;
import com.mofang.feed.redis.HotForumListRedis;
import com.mofang.feed.redis.RecommendGameListRedis;
import com.mofang.feed.redis.impl.HotForumListRedisImpl;
import com.mofang.feed.redis.impl.RecommendGameListRedisImpl;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeHotForumRankService;
import com.mofang.feed.service.FeedHomeRecommendGameRankService;
import com.mofang.feed.service.HomeRankService;
import com.mofang.feed.util.TimeUtil;

/***
 * 
 * @author linjx
 *
 */
public class HomeRankServiceImpl implements HomeRankService {

	private static final HomeRankServiceImpl SERVICE = new HomeRankServiceImpl();
	private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private StatForumViewHistoryDao viewHistoryDao = StatForumViewHistoryDaoImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedForumFollowDao forumFollowDao = FeedForumFollowDaoImpl.getInstance();
	private FeedHomeHotForumRankService hotForumRankService = FeedHomeHotForumRankServiceImpl.getInstance();
	private FeedHomeRecommendGameRankService recommendGameRankService = FeedHomeRecommendGameRankServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	private HotForumListRedis hotForumListRedis = HotForumListRedisImpl.getInstance();
	private RecommendGameListRedis recommendGameListRedis = RecommendGameListRedisImpl.getInstance();
	
	private HomeRankServiceImpl(){}
	
	public static HomeRankServiceImpl getInstance(){
		return SERVICE;
	}
	
	private long getCount(Map<Long, ForumCount> map, long fourmId){
		if(map == null){
			return 0;
		}
		ForumCount count = map.get(fourmId);
		return count != null ? count.count : 0;
	}
	
	private long calculate(long uv, long threadCount, long replyCount, long followCount, long recommendCount){
		long number = uv + (threadCount * 10) + (replyCount * 3) + (followCount * 5) + (recommendCount * 2);
		return number;
	}
	
	private int upOrDown(List<FeedHomeHotForumRank> list, long forumId, int index){
		for(int idx = 0; idx < list.size(); idx ++){
			FeedHomeHotForumRank model = list.get(idx);
			if(model.getForumId() ==  forumId && idx == index){
				return UpDownStatus.EQUAL;
			}else if(model.getForumId() == forumId && idx < index){
				return UpDownStatus.DOWN;
			}else if(model.getForumId() == forumId && idx > index){
				return UpDownStatus.UP;
			}
		}
		return UpDownStatus.UP;
	}
	
	/**
	 * 更新 热门游戏 排行榜数据
	 * @param list
	 * @throws Exception
	 */
	private void doRefreshHotForumRank(List<FeedForumOrder> list) throws Exception{
		int size = list.size();
		if(size > 5){
			size = 5;
		}
		List<FeedHomeHotForumRank> oldModelList = hotForumRankService.getList();
		List<FeedHomeHotForumRank> newModelList = new ArrayList<FeedHomeHotForumRank>(size);
		if(oldModelList == null){
			for(int idx = 0; idx < size; idx ++){
				FeedForumOrder order = list.get(idx);
				FeedHomeHotForumRank model = new FeedHomeHotForumRank();
				long forumId = order.getForumId();
				model.setForumId(forumId);
				model.setUpDown(UpDownStatus.UP);
				model.setDisplayOrder(idx + 1);
				newModelList.add(model);
			}
		}else{
			for(int idx = 0; idx < size; idx ++){
				FeedForumOrder order = list.get(idx);
				FeedHomeHotForumRank model = new FeedHomeHotForumRank();
				long forumId = order.getForumId();
				model.setForumId(forumId);
				int index = idx + 1;
				model.setForumId(order.getForumId());
				model.setUpDown(upOrDown(oldModelList, forumId, index));
				model.setDisplayOrder(index);
				newModelList.add(model);
			}
		}
		hotForumRankService.edit(newModelList);
	}
	
	/**
	 * 更新 新游推荐 排行榜数据
	 * @param list
	 * @throws Exception
	 */
	private void doRefreshRecommendGameRank(List<FeedForumOrder> list) throws Exception{
		int size = list.size();
		if(size > 5){
			size = 5;
		}
		List<FeedHomeRecommendGameRank> newModelList = new ArrayList<FeedHomeRecommendGameRank>(size);
		for(int idx = 0; idx < size; idx ++){
			FeedForumOrder order = list.get(idx);
			FeedHomeRecommendGameRank model = new FeedHomeRecommendGameRank();
			long forumId = order.getForumId();
			model.setForumId(forumId);
			model.setDisplayOrder(idx + 1);
			FeedForum forum = forumService.getInfo(forumId);
			if(forum != null){
				model.setDownloadUrl(GlobalConfig.GAME_DOWNLOAD_URL + forum.getName());
				boolean flag = HttpComponent.checkGift(forum.getGameId());
				if(flag){
					model.setGiftUrl(GlobalConfig.GIFT_INFO_URL + forum.getName());
				}
			}
			newModelList.add(model);
		}
		recommendGameRankService.edit(newModelList);
	}
	
	@Override
	public void refresh(int type) throws Exception {
		try {
			List<FeedForumOrder> forumOrderList = forumDao.getForumOrderList(type);
			if(forumOrderList == null){
				return;
			}
			int size = forumOrderList.size();
			Set<Long> forumIds = new HashSet<Long>(size);
			for(FeedForumOrder fo : forumOrderList){
				forumIds.add(fo.getForumId());
			}
			long yesterdayStartTime = TimeUtil.getYesterdyStartTime();
			long yesterdayEndTime = TimeUtil.getYesterdyEndTime();
			
			Map<Long, ForumCount> uvMap = viewHistoryDao.getUV(forumIds, yesterdayStartTime, yesterdayEndTime);
			Map<Long, ForumCount> threadMap = threadDao.getThreadCount(forumIds, yesterdayStartTime, yesterdayEndTime);
			Map<Long, ForumCount> replyMap = postDao.getReplyCount(forumIds, yesterdayStartTime, yesterdayEndTime);
			Map<Long, ForumCount> followMap = forumFollowDao.getFollowCount(forumIds, yesterdayStartTime, yesterdayEndTime);
			Map<Long, ForumCount> recommendMap = forumDao.getRecommendCount(forumIds, yesterdayStartTime, yesterdayEndTime);
			
			for(FeedForumOrder forumOrder : forumOrderList){
				long forumId = forumOrder.getForumId();
				long uv = getCount(uvMap, forumId);
				long threadCount = getCount(threadMap, forumId);
				long replyCount = getCount(replyMap, forumId);
				long followCount = getCount(followMap, forumId);
				long recommendCount = getCount(recommendMap, forumId);
				
				long orderValue = calculate(uv, threadCount, replyCount, followCount, recommendCount);
				forumOrder.setOrderValue(orderValue);
			}
			Collections.sort(forumOrderList);
			if(type == ForumType.HOT_FORUM){
				doRefreshHotForumRank(forumOrderList);
				addHotForumListRedis(forumOrderList);
			}
			else if(type == ForumType.RECOMMEND_GAME){
				doRefreshRecommendGameRank(forumOrderList);
				addRecommendGameListRedis(forumOrderList);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at HomeRankServiceImpl.refresh throw an error.", e);
			throw e;
		}
	}

	private static final String ABCDE = "ABCDE";
	private static final String FGHIJ = "FGHIJ";
	private static final String KLMNO = "KLMNO";
	private static final String PQRST = "PQRST";
	private static final String WXYZ = "WXYZ";
	
	private static String in(String nameSp){
		int idx;
		for(idx = 0; idx < ABCDE.length(); idx ++){
			if(nameSp.toUpperCase().equals(ABCDE.charAt(idx))){
				return ABCDE;
			}
		}
		for(idx = 0; idx < FGHIJ.length(); idx ++){
			if(nameSp.toUpperCase().equals(FGHIJ.charAt(idx))){
				return FGHIJ;
			}
		}
		for(idx = 0; idx < KLMNO.length(); idx ++){
			if(nameSp.toUpperCase().equals(KLMNO.charAt(idx))){
				return KLMNO;
			}
		}
		for(idx = 0; idx < PQRST.length(); idx ++){
			if(nameSp.toUpperCase().equals(PQRST.charAt(idx))){
				return PQRST;
			}
		}
		for(idx = 0; idx < WXYZ.length(); idx ++){
			if(nameSp.toUpperCase().equals(WXYZ.charAt(idx))){
				return WXYZ;
			}
		}
		return null;
	}
	
	/**
	 * 缓存 热门游戏 列表数据
	 * @param list
	 * @throws Exception
	 */
	private void addHotForumListRedis(List<FeedForumOrder> list) throws Exception{
		for(FeedForumOrder model : list){
			long forumId = model.getForumId();
			FeedForum forum = forumService.getInfo(forumId);
			if(forum != null){
				String nameSpell  = forum.getNameSpell();
				nameSpell = nameSpell.substring(0,1);
				String key = in(nameSpell);
				if(key==null)
					continue;
				hotForumListRedis.addHotForumList(key, forumId, model.getCreateTime());
				hotForumListRedis.setUrl(forumId, buildUrlMap(forum));
			}
		}
	}
	
	/**
	 * 缓存 新游推荐 列表数据 
	 * @param list
	 * @throws Exception
	 */
	private void addRecommendGameListRedis(List<FeedForumOrder> list) throws Exception{
		for(FeedForumOrder model : list){
			long forumId = model.getForumId();
			FeedForum forum = forumService.getInfo(forumId);
			if(forum != null){
				String nameSpell  = forum.getNameSpell();
				nameSpell = nameSpell.substring(0,1);
				String key = in(nameSpell);
				if(key==null)
					continue;
				recommendGameListRedis.addRecommendGameList(key, forumId, model.getCreateTime());
				recommendGameListRedis.setUrl(forumId, buildUrlMap(forum));
			}
		}
	}
	
	private Map<String, String> buildUrlMap(FeedForum forum){
		Map<String,String> map = new HashMap<String, String>(3);
		map.put("download_url", GlobalConfig.GAME_DOWNLOAD_URL + forum.getName());
		boolean flag = HttpComponent.checkGift(forum.getGameId());
		if(flag){
			map.put("gift_url", GlobalConfig.GIFT_INFO_URL + forum.getName());
		}else{
			map.put("gift_url", "");
		}
		map.put("prefecture_url", HttpComponent.getPrefectureUrl(forum.getForumId()));
		return map;
	}
	
}
