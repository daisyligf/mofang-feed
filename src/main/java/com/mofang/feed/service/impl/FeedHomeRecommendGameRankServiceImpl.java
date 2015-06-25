package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeRecommendGameRank;
import com.mofang.feed.mysql.FeedHomeRecommendGameRankDao;
import com.mofang.feed.mysql.impl.FeedHomeRecommendGameRankDaoImpl;
import com.mofang.feed.service.FeedHomeRecommendGameRankService;

/***
 * 
 * @author linjx
 *
 */
public class FeedHomeRecommendGameRankServiceImpl implements
		FeedHomeRecommendGameRankService {
	
	private static final FeedHomeRecommendGameRankServiceImpl SERVICE = new FeedHomeRecommendGameRankServiceImpl();
	private FeedHomeRecommendGameRankDao recommendGameRankDao = FeedHomeRecommendGameRankDaoImpl.getInstance();
	
	private FeedHomeRecommendGameRankServiceImpl(){
	}
	
	public static FeedHomeRecommendGameRankServiceImpl getInstance(){
		return SERVICE;
	}

	@Override
	public void edit(List<FeedHomeRecommendGameRank> modelList)
			throws Exception {
		try {
			recommendGameRankDao.deleteAll();
			for(FeedHomeRecommendGameRank model : modelList){
				recommendGameRankDao.add(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeRecommendGameRankServiceImpl.update throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedHomeRecommendGameRank> getList() throws Exception {
		try {
			return recommendGameRankDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeRecommendGameRankServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

}
