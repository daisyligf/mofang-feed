package com.mofang.feed.service.impl;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.ThreadRepliesRewardConstant;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.FeedThreadRepliesReward;
import com.mofang.feed.model.external.ThreadRepliesRewardConfig;
import com.mofang.feed.mysql.FeedThreadRepliesRewardDao;
import com.mofang.feed.mysql.impl.FeedThreadRepliesRewardDaoImpl;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.service.FeedThreadRepliesRewardService;
import com.mofang.feed.util.RandomUtil;

public class FeedThreadRepliesRewardServiceImpl implements
		FeedThreadRepliesRewardService {

	private static final FeedThreadRepliesRewardServiceImpl SERVICE = new FeedThreadRepliesRewardServiceImpl();
	private FeedThreadRepliesRewardDao rewardDao = FeedThreadRepliesRewardDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	
	private FeedThreadRepliesRewardServiceImpl() {
	}
	
	public static FeedThreadRepliesRewardServiceImpl getInstance() {
		return SERVICE;
	}
	
	@Override
	public void rewordUser(final long threadId) throws Exception {
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				try {
					FeedThread thread = threadRedis.getInfo(threadId);
					if(thread != null) {
						long userId = thread.getUserId();
						int replies = thread.getReplies();
						FeedThreadRepliesReward model = rewardDao.getModel(threadId);
						int rewardIndex = -1;
						if(model == null) {
							rewardIndex = ThreadRepliesRewardConstant.CONFIGS.size() - 1;
						} else {
							for(int idx = 0; idx < ThreadRepliesRewardConstant.CONFIGS.size(); idx ++) {
								ThreadRepliesRewardConfig config = ThreadRepliesRewardConstant.CONFIGS.get(idx);
								if(model.getLevel() == config.level && replies >= config.repliesRangeMin && replies <= config.repliesRangeMax)
									break;
								if(model.getLevel() != config.level && replies >= config.repliesRangeMin && replies <= config.repliesRangeMax)
									rewardIndex = idx;
							}
						}
						
						if(rewardIndex != -1) {
							ThreadRepliesRewardConfig config = ThreadRepliesRewardConstant.CONFIGS.get(rewardIndex);
							
							
							int exp = 0;
							
							if(config.exp != 0) {
								exp = config.exp;
							} else if(config.randomMin !=0 && config.randomMax != 0) {
								exp = RandomUtil.randomInt(config.randomMin, config.randomMax);
							}
							
							rewardDao.update(threadId, config.level, exp);
							
							HttpComponent.addExp(userId, exp);
						}
						
					}
					
				} catch (Exception e) {
					GlobalObject.ERROR_LOG.error("at FeedThreadRepliesRewardServiceImpl.rewordUser.task.run throw an error.", e);
				}
				
			}
		};
		GlobalObject.ASYN_DAO_EXECUTOR.execute(task);
	}

}