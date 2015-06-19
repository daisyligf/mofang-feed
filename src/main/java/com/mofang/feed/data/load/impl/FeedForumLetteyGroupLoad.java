package com.mofang.feed.data.load.impl;

import java.util.List;

import com.mofang.feed.data.load.FeedLoad;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.external.FeedForumOrder;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.redis.ForumUrlRedis;
import com.mofang.feed.redis.HotForumListRedis;
import com.mofang.feed.redis.RecommendGameListRedis;
import com.mofang.feed.redis.impl.ForumUrlRedisImpl;
import com.mofang.feed.redis.impl.HotForumListRedisImpl;
import com.mofang.feed.redis.impl.RecommendGameListRedisImpl;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.util.ForumHelper;

/**
 * 字母分组板块信息
 * @author linjx
 *
 */
public class FeedForumLetteyGroupLoad implements FeedLoad {

	private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
	private HotForumListRedis hotForumListRedis = HotForumListRedisImpl.getInstance();
	private RecommendGameListRedis recommendGameListRedis = RecommendGameListRedisImpl.getInstance();
	private ForumUrlRedis forumUrlRedis = ForumUrlRedisImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	/**
	 * 缓存 热门游戏 列表数据
	 * @param list
	 * @throws Exception
	 */
	private void addHotForumListRedis(List<FeedForumOrder> list) throws Exception{
		for(FeedForumOrder model : list){
			long forumId = model.getForumId();
			FeedForum forum = forumService.getInfo(forumId);
			if(forum != null){
				if(forum.getType() != ForumType.HOT_FORUM)
					continue;
				String nameSpell  = forum.getNameSpell();
				nameSpell = nameSpell.substring(0,1);
				String key = ForumHelper.match(nameSpell);
				hotForumListRedis.addHotForumList(key, forumId, model.getCreateTime());
				forumUrlRedis.setUrl(forumId, ForumHelper.buildUrlMap(forum));
			}
		}
	}
	
	/**
	 * 缓存 新游推荐 列表数据 
	 * @param list
	 * @throws Exception
	 */
	private void addRecommendGameListRedis(List<FeedForumOrder> list) throws Exception{
		for(FeedForumOrder model : list){
			long forumId = model.getForumId();
			FeedForum forum = forumService.getInfo(forumId);
			if(forum != null){
				if(forum.getType() != ForumType.RECOMMEND_GAME)
					continue;
				String nameSpell  = forum.getNameSpell();
				nameSpell = nameSpell.substring(0,1);
				String key = ForumHelper.match(nameSpell);
				recommendGameListRedis.addRecommendGameList(key, forumId, model.getCreateTime());
				forumUrlRedis.setUrl(forumId, ForumHelper.buildUrlMap(forum));
			}
		}
	}
	
	@Override
	public void exec() {		
		try {
			//暂时判断第一个分组
			String hotFourmKey = RedisKey.HOT_FORUM_LIST_KEY_PREFIX + ForumHelper.ABCDE;
			String recommendFourmKey = RedisKey.RECOMMEND_GAME_LIST_KEY_PREFIX + ForumHelper.ABCDE;
			
			if(!RedisFaster.exists(hotFourmKey) || !RedisFaster.exists(recommendFourmKey)) {
				return;
			}
			
			List<FeedForumOrder> forumOrderList = forumDao.getForumOrderList(ForumType.ALL);
			if(forumOrderList == null){
				return;
			}
			this.addHotForumListRedis(forumOrderList);
			this.addRecommendGameListRedis(forumOrderList);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedForumLetteyGroupLoad.exec throw an error.", e);
		}
	}

}
