package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.feed.mysql.FeedHomeHotForumRankDao;
import com.mofang.feed.mysql.impl.FeedHomeHotForumRankDaoImpl;
import com.mofang.feed.service.FeedHomeHotForumRankService;

/***
 * 
 * @author linjx
 *
 */
public class FeedHomeHotForumRankServiceImpl implements FeedHomeHotForumRankService {

	private static final FeedHomeHotForumRankServiceImpl SERVICE = new FeedHomeHotForumRankServiceImpl();
	private FeedHomeHotForumRankDao forumRankDao = FeedHomeHotForumRankDaoImpl.getInstance();
	
	private FeedHomeHotForumRankServiceImpl(){}
	
	public static FeedHomeHotForumRankServiceImpl getInstance(){
		return SERVICE;
	}
	
	@Override
	public void edit(List<FeedHomeHotForumRank> modelList) throws Exception {
		try {
			for(FeedHomeHotForumRank model : modelList){
				forumRankDao.edit(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeForumRankServiceImpl.update throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedHomeHotForumRank> getList() throws Exception {
		try {
			return forumRankDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeForumRankServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

}
