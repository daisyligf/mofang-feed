package com.mofang.feed.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ForumURLKey;
import com.mofang.feed.model.FeedHomeRecommendGame;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedHomeRecommendGameDao;
import com.mofang.feed.mysql.impl.FeedHomeRecommendGameDaoImpl;
import com.mofang.feed.redis.RecommendGameListRedis;
import com.mofang.feed.redis.impl.RecommendGameListRedisImpl;
import com.mofang.feed.service.FeedHomeRecommendGameService;
import com.mofang.feed.util.RedisPageNumber;

/***
 * 
 * @author linjix
 *
 */
public class FeedHomeRecommendGameServiceImpl implements
		FeedHomeRecommendGameService {

	private static final FeedHomeRecommendGameServiceImpl SERVICE = new FeedHomeRecommendGameServiceImpl();
	private FeedHomeRecommendGameDao recommendGameDao = FeedHomeRecommendGameDaoImpl.getInstance();
	private RecommendGameListRedis recommendGameRedis = RecommendGameListRedisImpl.getInstance();
	
	public static FeedHomeRecommendGameServiceImpl getInstance(){
		return SERVICE;
	}
	
	private FeedHomeRecommendGameServiceImpl(){}
	
	@Override
	public void edit(List<FeedHomeRecommendGame> modelList) throws Exception {
		try {
			for(FeedHomeRecommendGame model : modelList){
				recommendGameDao.edit(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeRecommendGameServiceImpl.update throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedHomeRecommendGame> getList() throws Exception {
		try {
			return recommendGameDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeRecommendGameServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedHomeRecommendGame> getListByLetterGroup(
			String letterGroup, int pageNum, int pageSize) throws Exception {
		try {
			long total = recommendGameRedis.getForumCount(letterGroup);
			RedisPageNumber pageNumber = new RedisPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			Set<String> idSet = recommendGameRedis.getList(letterGroup, start, end);
			List<FeedHomeRecommendGame> list = new ArrayList<FeedHomeRecommendGame>(idSet.size());
			
			for(String idStr : idSet){
				FeedHomeRecommendGame model = new FeedHomeRecommendGame();
				long forumId = Long.parseLong(idStr);
				model.setForumId(forumId);
				Map<String, String> urlMap = recommendGameRedis.getUrl(forumId);
				if(urlMap != null){
					model.setDownloadUrl(urlMap.get(ForumURLKey.DOWNLOAD_URL_KEY));
					model.setGiftUrl(urlMap.get(ForumURLKey.GIFT_URL_KEY));
				}
				list.add(model);
			}
			Page<FeedHomeRecommendGame> page = new Page<FeedHomeRecommendGame>(total, list);
			return page;
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeRecommendGameServiceImpl.getListByLetterGroup throw an error.", e);
			throw e;
		}
	}

}
