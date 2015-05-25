package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedTag;
import com.mofang.feed.mysql.FeedForumTagDao;
import com.mofang.feed.mysql.FeedTagDao;
import com.mofang.feed.mysql.impl.FeedForumTagDaoImpl;
import com.mofang.feed.mysql.impl.FeedTagDaoImpl;
import com.mofang.feed.redis.FeedTagRedis;
import com.mofang.feed.redis.impl.FeedTagRedisImpl;
import com.mofang.feed.service.FeedTagService;

/***
 * 
 * @author linjx
 *
 */
public class FeedTagServiceImpl implements FeedTagService {
	
	private static final FeedTagServiceImpl SERVICE = new FeedTagServiceImpl();
	private FeedTagDao tagDao = FeedTagDaoImpl.getInstance();
	private FeedForumTagDao forumTagDao = FeedForumTagDaoImpl.getInstance();
	private FeedTagRedis tagRedis =  FeedTagRedisImpl.getInstance();
	
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
			for(Integer tagId : tagIdList){
				forumTagDao.deleteByTagId(tagId);
			}
			
			/*****************redis*****************/
			for(Integer tagId : tagIdList){
				tagRedis.delete(tagId);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedTagServiceImpl.delete throw an error.", e);
			throw e;
		}

	}

	@Override
	public void add(FeedTag tag) throws Exception {
		try {
			tagRedis.set(tag.getTagId(), tag.getTagName());
			tagDao.add(tag);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedTagServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public String getTagName(int tagId) throws Exception {
		try{
			String name = tagRedis.get(tagId);
			return name;
		} catch(Exception e){
			GlobalObject.ERROR_LOG.error("at FeedTagService.getTagName throw an error.", e);
			throw e;
		}
	}

}
