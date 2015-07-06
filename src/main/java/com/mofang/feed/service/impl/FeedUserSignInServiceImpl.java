package com.mofang.feed.service.impl;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedUserSignIn;
import com.mofang.feed.model.external.UserSignIn;
import com.mofang.feed.mysql.FeedUserSignInDao;
import com.mofang.feed.mysql.impl.FeedUserSignInDaoImpl;
import com.mofang.feed.redis.FeedUserSignInRedis;
import com.mofang.feed.redis.impl.FeedUserSignInRedisImpl;
import com.mofang.feed.service.FeedUserSignInService;
import com.mofang.feed.util.TimeUtil;

public class FeedUserSignInServiceImpl implements FeedUserSignInService {

	private static final FeedUserSignInServiceImpl REDIS = new FeedUserSignInServiceImpl();
	private FeedUserSignInDao signInDao = FeedUserSignInDaoImpl.getInstance();
	private FeedUserSignInRedis signInRedis = FeedUserSignInRedisImpl.getInstance();
	
	private FeedUserSignInServiceImpl(){}
	
	public static FeedUserSignInServiceImpl getInstance(){
		return REDIS;
	}
	
	@Override
	public void sign(long userId) throws Exception {
		try {
			UserSignIn userSignIn = signInRedis.getInfo(userId);
			long now = System.currentTimeMillis();
			boolean isMax = false;
			boolean add = false;
			if(userSignIn == null) {
				userSignIn = new UserSignIn();
				userSignIn.lastSignInTime = now;
				userSignIn.days = 1;
			}else {
				long lastSignInTime = userSignIn.lastSignInTime;
				int intervalDay =(int) (now - lastSignInTime)/1000/60/60/24;
				
				userSignIn.lastSignInTime = now;
				if(intervalDay == 0 || intervalDay == 1) {
					
					boolean isSame = TimeUtil.isSameDay(now, lastSignInTime);
					if(!isSame) {
						
						if(userSignIn.days == GlobalConfig.SIGN_IN_DAY_MAX_EXP) {
							isMax = true;
						}else {
							userSignIn.days++;
						}
						
						add = true;
					}
				}  else if(intervalDay > 1) {
					userSignIn.days = 1;
					
					add = true;
				}
				
			}

			if(add) {
				
				signInRedis.update(userId, userSignIn.lastSignInTime, userSignIn.days);
				
				FeedUserSignIn model = new FeedUserSignIn();
				model.setCreateTime(now);
				model.setUserId(userId);
				signInDao.add(model);
				
				if(isMax) {
					HttpComponent.addExp(userId, GlobalConfig.SIGN_IN_DAY_MAX_EXP);
				}else {
					HttpComponent.addExp(userId, GlobalConfig.SIGN_IN_DAY_EXP);
				}
				
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedUserSignInServiceImpl.sign throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean isSignIned(long userId) throws Exception {
		try {
			UserSignIn userSignIn = signInRedis.getInfo(userId);
			boolean flag = false;
			if(userSignIn != null) {
				flag = TimeUtil.isSameDay(System.currentTimeMillis(), userSignIn.lastSignInTime);
			}
			return flag;
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedUserSignInServiceImpl.isSignIned throw an error.", e);
			throw e;
		}
	}

}
