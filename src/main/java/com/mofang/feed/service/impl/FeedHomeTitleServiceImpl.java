package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeTitle;
import com.mofang.feed.mysql.FeedHomeTitleDao;
import com.mofang.feed.mysql.impl.FeedHomeTitleDaoImpl;
import com.mofang.feed.service.FeedHomeTitleService;

/***
 * 
 * @author linjx
 * 
 */
public class FeedHomeTitleServiceImpl implements FeedHomeTitleService {

	private final static FeedHomeTitleServiceImpl SERVICE = new FeedHomeTitleServiceImpl();
	private FeedHomeTitleDao homeTitleDao = FeedHomeTitleDaoImpl.getInstance();

	private FeedHomeTitleServiceImpl(){}
	
	public static FeedHomeTitleServiceImpl getInstance() {
		return SERVICE;
	}

	@Override
	public List<FeedHomeTitle> getList() throws Exception {
		try {
			return homeTitleDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at FeedHomeTitleServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

	@Override
	public void edit(List<FeedHomeTitle> modelList) throws Exception {
		try {
			homeTitleDao.deleteAll();
			for(FeedHomeTitle model : modelList){
				homeTitleDao.add(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at FeedHomeTitleServiceImpl.update throw an error.", e);
			throw e;
		}
	}

}
