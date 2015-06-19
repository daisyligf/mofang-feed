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
import com.mofang.feed.model.external.Game;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.StatForumViewHistoryDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.mysql.impl.StatForumViewHistoryDaoImpl;
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
	private FeedHomeHotForumRankService hotForumRankService = FeedHomeHotForumRankServiceImpl.getInstance();
	private FeedHomeRecommendGameRankService recommendGameRankService = FeedHomeRecommendGameRankServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
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
			int oldIndex = model.getDisplayOrder();
			if(model.getForumId() ==  forumId && oldIndex == index){
				return UpDownStatus.EQUAL;
			}else if(model.getForumId() == forumId && oldIndex < index){
				return UpDownStatus.DOWN;
			}else if(model.getForumId() == forumId && oldIndex > index){
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
				model.setForumId(forumId);
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
				int gameId = forum.getGameId();
				model.setDownloadUrl(GlobalConfig.GAME_DOWNLOAD_URL + gameId);
				boolean flag = HttpComponent.checkGift(gameId);
				if(flag){
					Game game = HttpComponent.getGameInfo(gameId);
					if(game != null) {
						model.setGiftUrl(GlobalConfig.GIFT_INFO_URL + game.getName());
					}
				}
			}
			newModelList.add(model);
		}
		recommendGameRankService.edit(newModelList);
	}
	
	@Override
	public void refresh(int type) throws Exception {
		try {
			long startTime = System.currentTimeMillis();
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
			
			/********************获取板块用户关注数 暂时请求http*************************/
			Map<Long, ForumCount> followMap = new HashMap<Long, ForumCount>(size);
			int step = 0;
			int loopCount = size % 100 == 0 ? size / 100 : size / 100 + 1;
			for(int idx = 0; idx < loopCount; idx ++){
				int jdx = step;
				int stepLimit = step + 100;
				if(stepLimit > size) {
					stepLimit = size;
				}
				//分批请求每次100个forumId
				List<Long> list = new ArrayList<Long>(100);
				for(;jdx < stepLimit; jdx ++) {
					list.add(forumOrderList.get(jdx).getForumId());
				}
				Map<Long, ForumCount> map = HttpComponent.getForumFollowCountByTime(list, yesterdayStartTime/1000, yesterdayEndTime/1000);
				if(map != null) {
					followMap.putAll(map);
				}
				step += 100;
			}
			/********************************************************************************/
			//Map<Long, ForumCount> followMap = forumFollowDao.getFollowCount(forumIds, yesterdayStartTime, yesterdayEndTime);
			
			/********************************取点赞数好蛋疼********************************/
			Map<Long, ForumCount> postRecommendMap = forumDao.getPostRecommendCount(type, yesterdayStartTime, yesterdayEndTime);
			Map<Long, ForumCount> threadRecommendMap = forumDao.getThreadRecommendCount(type, yesterdayStartTime, yesterdayEndTime);
			Map<Long, ForumCount> recommendMap = null;
			if(postRecommendMap != null && threadRecommendMap != null ){
				int postRecommendSize = postRecommendMap.size();
				int threadRecommendSize = threadRecommendMap.size();
				if(postRecommendSize >= threadRecommendSize){
					recommendMap = postRecommendMap;
					for(Map.Entry<Long, ForumCount> entry : threadRecommendMap.entrySet()){
						long key = entry.getKey();
						ForumCount forumCount = recommendMap.get(key);
						if(forumCount != null) {
							forumCount.count = forumCount.count + entry.getValue().count;
						}else {
							recommendMap.put(key, forumCount);
						}
					}
				}else{
					recommendMap = threadRecommendMap;
					for(Map.Entry<Long, ForumCount> entry : postRecommendMap.entrySet()){
						long key = entry.getKey();
						ForumCount forumCount = recommendMap.get(key);
						if(forumCount != null) {
							forumCount.count = forumCount.count + entry.getValue().count;
						}else {
							recommendMap.put(key, forumCount);
						}
					}
				}
			}else if(postRecommendMap == null && threadRecommendMap != null){
				recommendMap = threadRecommendMap;
			}else if(threadRecommendMap == null && postRecommendMap != null){
				recommendMap = postRecommendMap;
			}
			/********************************************************************************/
			
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
			if(type == ForumType.ALL){
				doRefreshHotForumRank(forumOrderList);
				//delHotForumListRedis();
				//addHotForumListRedis(forumOrderList);
			}
			else if(type == ForumType.RECOMMEND_GAME){
				doRefreshRecommendGameRank(forumOrderList);
				//delRecommendGameListRedis();
				//addRecommendGameListRedis(forumOrderList);
			}
			long endTime = System.currentTimeMillis();
			if(type == ForumType.ALL)
				GlobalObject.INFO_LOG.info("at HomeRankServiceImpl.refresh,刷新热门板块排行榜，耗时:" + (endTime - startTime) + "毫秒");
			else if(type == ForumType.RECOMMEND_GAME)
				GlobalObject.INFO_LOG.info("at HomeRankServiceImpl.refresh,刷新新游排行榜，耗时:" + (endTime - startTime) + "毫秒");
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at HomeRankServiceImpl.refresh throw an error.", e);
			throw e;
		}
	}
	
//	private void delHotForumListRedis() throws Exception {
//		hotForumListRedis.delete(ForumHelper.ABCDE);
//		hotForumListRedis.delete(ForumHelper.FGHIJ);
//		hotForumListRedis.delete(ForumHelper.KLMNO);
//		hotForumListRedis.delete(ForumHelper.PQRST);
//		hotForumListRedis.delete(ForumHelper.WXYZ);
//		hotForumListRedis.delete(ForumHelper.OTHER);
//	}
//	
//	private void delRecommendGameListRedis() throws Exception {
//		recommendGameListRedis.delete(ForumHelper.ABCDE);
//		recommendGameListRedis.delete(ForumHelper.FGHIJ);
//		recommendGameListRedis.delete(ForumHelper.KLMNO);
//		recommendGameListRedis.delete(ForumHelper.PQRST);
//		recommendGameListRedis.delete(ForumHelper.WXYZ);
//		recommendGameListRedis.delete(ForumHelper.OTHER);
//	}
	
	
}
