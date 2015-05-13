package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedTag;
import com.mofang.feed.mysql.FeedTagDao;
import com.mofang.feed.mysql.impl.FeedTagDaoImpl;
import com.mofang.feed.service.FeedTagService;

/***
 * 
 * @author linjx
 *
 */
public class FeedTagServiceImpl implements FeedTagService {
	
	private static final FeedTagServiceImpl SERVICE = new FeedTagServiceImpl();
	private FeedTagDao tagDao = FeedTagDaoImpl.getInstance();
	
	private FeedTagServiceImpl(){}
	
	public static FeedTagServiceImpl getInstance(){
		return SERVICE;
	}

	@Override
	public List<FeedTag> getList() throws Exception {
		try {
			return tagDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedTagServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(List<Integer> tagIdList) throws Exception {
		try {
			tagDao.delete(tagIdList);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedTagServiceImpl.delete throw an error.", e);
			throw e;
		}

	}

	@Override
	public void add(FeedTag tag) throws Exception {
		try {
			tagDao.add(tag);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedTagServiceImpl.add throw an error.", e);
			throw e;
		}
	}

}
