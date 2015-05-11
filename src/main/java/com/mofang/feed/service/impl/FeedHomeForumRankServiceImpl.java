package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeForumRank;
import com.mofang.feed.mysql.FeedHomeForumRankDao;
import com.mofang.feed.mysql.impl.FeedHomeForumRankDaoImpl;
import com.mofang.feed.service.FeedHomeForumRankService;

/***
 * 
 * @author linjx
 *
 */
public class FeedHomeForumRankServiceImpl implements FeedHomeForumRankService {

	private static final FeedHomeForumRankServiceImpl SERVICE = new FeedHomeForumRankServiceImpl();
	private FeedHomeForumRankDao forumRankDao = FeedHomeForumRankDaoImpl.getInstance();
	
	private FeedHomeForumRankServiceImpl(){}
	
	public static FeedHomeForumRankServiceImpl getInstance(){
		return SERVICE;
	}
	
	@Override
	public void update(List<FeedHomeForumRank> modelList) throws Exception {
		try {
			for(FeedHomeForumRank model : modelList){
				forumRankDao.update(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeForumRankServiceImpl.update throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedHomeForumRank> getList() throws Exception {
		try {
			return forumRankDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeForumRankServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

}
