package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeRecommendGame;
import com.mofang.feed.mysql.FeedHomeRecommendGameDao;
import com.mofang.feed.mysql.impl.FeedHomeRecommendGameDaoImpl;
import com.mofang.feed.service.FeedHomeRecommendGameService;

/***
 * 
 * @author linjix
 *
 */
public class FeedHomeRecommendGameServiceImpl implements
		FeedHomeRecommendGameService {

	private static final FeedHomeRecommendGameServiceImpl SERVICE = new FeedHomeRecommendGameServiceImpl();
	private FeedHomeRecommendGameDao recommendGameDao = FeedHomeRecommendGameDaoImpl.getInstance();
	
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

}
