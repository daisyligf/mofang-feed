package com.mofang.feed.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ForumURLKey;
import com.mofang.feed.model.FeedHomeHotForum;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedHomeHotForumDao;
import com.mofang.feed.mysql.impl.FeedHomeHotForumDaoImpl;
import com.mofang.feed.redis.ForumUrlRedis;
import com.mofang.feed.redis.HotForumListRedis;
import com.mofang.feed.redis.impl.ForumUrlRedisImpl;
import com.mofang.feed.redis.impl.HotForumListRedisImpl;
import com.mofang.feed.service.FeedHomeHotForumService;
import com.mofang.feed.util.RedisPageNumber;

/***
 * 
 * @author linjx
 *
 */
public class FeedHomeHotForumServiceImpl implements FeedHomeHotForumService {

	private static final FeedHomeHotForumServiceImpl SERVICE = new  FeedHomeHotForumServiceImpl();
	private FeedHomeHotForumDao hotForumDao = FeedHomeHotForumDaoImpl.getInstance();
	private HotForumListRedis hotForumRedis = HotForumListRedisImpl.getInstance();
	private ForumUrlRedis forumUrlRedis = ForumUrlRedisImpl.getInstance();
	
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

	@Override
	public Page<FeedHomeHotForum> getListByLetterGroup(String letterGroup, int pageNum, int pageSize)
			throws Exception {
		try {
			long total = hotForumRedis.getForumCount(letterGroup);
			RedisPageNumber pageNumber = new RedisPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			Set<String> idSet = hotForumRedis.getList(letterGroup, start, end);
			List<FeedHomeHotForum> list = new ArrayList<FeedHomeHotForum>(idSet.size());
			
			for(String idStr : idSet){
				FeedHomeHotForum model = new FeedHomeHotForum();
				long forumId = Long.parseLong(idStr);
				model.setForumId(forumId);
				Map<String, String> urlMap = forumUrlRedis.getUrl(forumId);
				if(urlMap != null){
					model.setGiftUrl(urlMap.get(ForumURLKey.GIFT_URL_KEY));
					model.setPrefectureUrl(urlMap.get(ForumURLKey.PREFECTURE_URL_KEY));
				}
				list.add(model);
			}
			Page<FeedHomeHotForum> page = new Page<FeedHomeHotForum>(total, list);
			return page;
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeHotForumServiceImpl.getListByLetterGroup throw an error.", e);
			throw e;
		}
	}

}
