package com.mofang.feed.service.impl;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedUserSignIn;
import com.mofang.feed.model.external.SignInResult;
import com.mofang.feed.model.external.UserSignIn;
import com.mofang.feed.mysql.FeedUserSignInDao;
import com.mofang.feed.mysql.impl.FeedUserSignInDaoImpl;
import com.mofang.feed.redis.FeedUserSignInRedis;
import com.mofang.feed.redis.impl.FeedUserSignInRedisImpl;
import com.mofang.feed.service.FeedUserSignInService;
import com.mofang.feed.util.TimeUtil;

public class FeedUserSignInServiceImpl implements FeedUserSignInService {

	private static final FeedUserSignInServiceImpl SERVICE = new FeedUserSignInServiceImpl();
	private FeedUserSignInDao signInDao = FeedUserSignInDaoImpl.getInstance();
	private FeedUserSignInRedis signInRedis = FeedUserSignInRedisImpl
			.getInstance();

	private FeedUserSignInServiceImpl() {
	}

	public static FeedUserSignInServiceImpl getInstance() {
		return SERVICE;
	}

	@Override
	public SignInResult sign(long userId) throws Exception {
		try {
			UserSignIn userSignIn = signInRedis.getInfo(userId);
			long now = System.currentTimeMillis();
			boolean isMax = false;
			boolean add = false;
			if (userSignIn == null) {
				userSignIn = new UserSignIn();
				userSignIn.lastSignInTime = now;
				userSignIn.days = 1;
				add = true;
			} else {
				long lastSignInTime = userSignIn.lastSignInTime;
				int intervalDay = (int) (now - lastSignInTime) / 1000 / 60 / 60
						/ 24;

				userSignIn.lastSignInTime = now;
				if (intervalDay == 0 || intervalDay == 1) {

					boolean isSame = TimeUtil.isSameDay(now, lastSignInTime);
					if (!isSame) {

						if (userSignIn.days >= GlobalConfig.SIGN_IN_DAY_MAX_EXP) {
							isMax = true;
						}

						userSignIn.days++;
						add = true;
					}
				} else if (intervalDay > 1) {
					userSignIn.days = 1;

					add = true;
				}

			}

			SignInResult result = new SignInResult();

			if (add) {

				// 更新签到信息
				signInRedis.update(userId, userSignIn.lastSignInTime,
						userSignIn.days);

				// post请求添加经验
				if (isMax) {
					HttpComponent.addExp(userId,
							GlobalConfig.SIGN_IN_DAY_MAX_EXP);
				} else {
					HttpComponent.addExp(userId, GlobalConfig.SIGN_IN_DAY_EXP);
				}

				// 记录到签到成员列表
				if (!signInRedis.exists()) {
					signInRedis.addSignInfoAndExpire(userId, now);
				} else {
					signInRedis.addSignInfo(userId, now);
				}

				result = signInRedis.getResult(userId);
				result.days = userSignIn.days;
				result.isSignIn = true;

				// 添加签到流水
				FeedUserSignIn model = new FeedUserSignIn();
				model.setCreateTime(now);
				model.setUserId(userId);
				signInDao.add(model);
			}

			return result;
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at FeedUserSignInServiceImpl.sign throw an error.", e);
			throw e;
		}
	}

	@Override
	public SignInResult getResult(long userId) throws Exception {
		try {
			UserSignIn userSignIn = signInRedis.getInfo(userId);
			SignInResult result = null;

			if (signInRedis.exists()) {
				result = signInRedis.getResult(userId);
			}

			if (result == null) {
				result = new SignInResult();
			}

			boolean flag = false;
			if (userSignIn != null) {
				flag = TimeUtil.isSameDay(System.currentTimeMillis(),
						userSignIn.lastSignInTime);
				result.days = userSignIn.days;
			} else {
				result.days = 0;
			}

			result.isSignIn = flag;
			return result;
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at FeedUserSignInServiceImpl.isSignIned throw an error.",
					e);
			throw e;
		}
	}

}
