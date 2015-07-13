package com.mofang.feed.service.impl;

import java.util.List;

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
	private FeedThreadRepliesRewardDao rewardDao = FeedThreadRepliesRewardDaoImpl
			.getInstance();
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
					if (thread != null) {
						long userId = thread.getUserId();
						int replies = thread.getReplies();
						FeedThreadRepliesReward model = rewardDao
								.getModel(threadId);
						List<ThreadRepliesRewardConfig> configList = ThreadRepliesRewardConstant.CONFIGS;
						int rewardIndex = -1;
						boolean add = false;
						int size = configList.size();
						if (model == null) {
							for (int idx = 0; idx < size; idx++) {
								ThreadRepliesRewardConfig config = configList
										.get(idx);
								if (replies >= config.repliesRangeMin
										&& replies <= config.repliesRangeMax)
									rewardIndex = idx;
							}
							add = true;
						} else {
							for (int idx = 0; idx < size; idx++) {
								ThreadRepliesRewardConfig config = configList
										.get(idx);
								if (model.getLevel() == config.level
										&& replies >= config.repliesRangeMin
										&& replies <= config.repliesRangeMax)
									break;
								if (model.getLevel() != config.level
										&& replies >= config.repliesRangeMin
										&& replies <= config.repliesRangeMax)
									rewardIndex = idx;
							}
						}

						if (rewardIndex != -1) {
							ThreadRepliesRewardConfig config = configList
									.get(rewardIndex);

							int exp = 0;

							if (config.exp != 0) {
								exp = config.exp;
							} else if (config.randomMin != 0
									&& config.randomMax != 0) {
								exp = RandomUtil.randomInt(config.randomMin,
										config.randomMax);
							}

							//mysql
							if (add) {
								FeedThreadRepliesReward rewardModel = new FeedThreadRepliesReward();
								rewardModel.setThreadId(threadId);
								rewardModel.setExp(exp);
								rewardModel.setLevel(config.level);
								rewardDao.add(rewardModel);
							} else {
								rewardDao.update(threadId, config.level, exp);
							}

							//http
							HttpComponent.addExp(userId, exp);
						}

					}

				} catch (Exception e) {
					GlobalObject.ERROR_LOG
							.error("at FeedThreadRepliesRewardServiceImpl.rewordUser.task.run throw an error.",
									e);
				}

			}
		};
		GlobalObject.ASYN_DAO_EXECUTOR.execute(task);
	}

}
