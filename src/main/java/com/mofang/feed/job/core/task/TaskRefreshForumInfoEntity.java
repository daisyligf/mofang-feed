package com.mofang.feed.job.core.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.job.core.TaskEntity;
import com.mofang.feed.model.external.Game;
import com.mofang.feed.model.external.Pair;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedHomeHotForumDao;
import com.mofang.feed.mysql.FeedHomeRecommendGameDao;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedHomeHotForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedHomeRecommendGameDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.ForumUrlRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.ForumUrlRedisImpl;
import com.mofang.feed.util.TimeUtil;

/***
 * 每天 定时刷新 板块 最新礼包url和icon
 * 
 * @author ljx
 */
public class TaskRefreshForumInfoEntity extends TaskEntity {

	private class Task implements Runnable {
		
		private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
		private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
		private FeedHomeHotForumDao hotForumDao = FeedHomeHotForumDaoImpl.getInstance();
		private FeedHomeRecommendGameDao recommendGameDao = FeedHomeRecommendGameDaoImpl.getInstance();
		private ForumUrlRedis forumUrlRedis = ForumUrlRedisImpl.getInstance();

		@Override
		public void run() {
			try {
				
				List<Pair<Long, Integer>> forumIdGameIdPairList = forumDao.getForumIdGameIdPairList(ForumType.ALL);
				if(forumIdGameIdPairList == null)
					return;
				
				Map<Long, Game> gameMap = new HashMap<Long, Game>(forumIdGameIdPairList.size());
				for(Pair<Long, Integer> pair : forumIdGameIdPairList) {
					int gameId = pair.right;
					boolean flag = HttpComponent.checkGift(gameId);
					if(flag) {
						Game game = HttpComponent.getGameInfo(gameId);
						if(game != null) 
							gameMap.put(pair.left, game);
					}
				}
				
				Set<Long> hotForumIdSet = hotForumDao.getForumIdSet();
				Set<Long> recommendForumIdSet = recommendGameDao.getForumIdSet();
				
				for(Map.Entry<Long, Game> entry : gameMap.entrySet()) {
					//mysql 更新 feed_forum 表 icon 字段
					long forumId  = entry.getKey();
					Game game = entry.getValue();
					String giftUrl = GlobalConfig.GIFT_INFO_URL + game.getName();
					
					 forumDao.updateForumIcon(forumId, game.getIcon());

					 //redis 更新 forum_info key icon 字段
					 forumRedis.updateIcon(forumId, game.getIcon());
				
					 //mysql 更新 feed_home_hot_forum_list  表 gift_url 字段
					 if(hotForumIdSet != null && hotForumIdSet.contains(forumId))
						 hotForumDao.updateGiftUrl(forumId, giftUrl);

					 //mysql 更新 feed_home_recommend_list 表 gift_url 字段
					 if(recommendForumIdSet != null && recommendForumIdSet.contains(forumId))
						 recommendGameDao.updateGiftUrl(forumId, giftUrl);
					 
					 //redis 更新 forum_extend key gift_url 字段
					 forumUrlRedis.setUrl(forumId, giftUrl);
				}
				
				GlobalObject.INFO_LOG.info("at TaskRefreshForumInfoEntity.Task, 刷新板块最新礼包地址和icon完成");
			} catch (Exception e) {
				GlobalObject.ERROR_LOG
						.error("at TaskRefreshForumInfoEntity.Task.run throw an error.",
								e);
			}

		}

	}

	public TaskRefreshForumInfoEntity() {
		super.setTask(new Task());
		if (GlobalConfig.TIME_TASK_DELAY_TIME == 1)
			super.setInitialDelay(10000l);
		else
			super.setInitialDelay(TimeUtil.getInitDelay(24));
		super.setPeriod(24 * 60 * 60 * 1000l);
		super.setUnit(TimeUnit.MILLISECONDS);
	}

}
