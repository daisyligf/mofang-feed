package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.mysql.FeedHomeHotForumDao;
import com.mofang.feed.mysql.impl.FeedHomeHotForumDaoImpl;
import com.mofang.feed.service.FeedHomeHotForumService;

/***
 * 
 * @author linjx
 *
 */
public class FeedHomeHotForumServiceImpl implements FeedHomeHotForumService {

	private static final FeedHomeHotForumServiceImpl SERVICE = new  FeedHomeHotForumServiceImpl();
	private FeedHomeHotForumDao hotForumDao = FeedHomeHotForumDaoImpl.getInstance();
	
	private FeedHomeHotForumServiceImpl(){}
	
	public static FeedHomeHotForumServiceImpl getInstance(){
		return SERVICE;
	}
	
	@Override
	public void edit(List<FeedHomeHotForum> modelList) throws Exception {
		try {
			for(FeedHomeHotForum model : modelList){
				hotForumDao.edit(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeHotForumServiceImpl.update throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedHomeHotForum> getList() throws Exception {
		try {
			return hotForumDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeHotForumServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

}
