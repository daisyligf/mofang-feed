package com.mofang.feed.component;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.mofang.feed.global.GlobalObject;

/***
 * 
 * @author linjx
 * 
 */
public class RankComponent {

	/***
	 * 每天凌晨刷新 热门游戏 排行榜
	 */
	private static void refreshHotForumRank() {
		try {
			//uv+发帖*10+回复*3+关注*5+赞*2

			// 根据设计提供的算法 排序列表

			// 获取前10数据 更新排行榜

		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at RankComponent.refreshHotForumRank throw an error.", e);
		}
	}

	/***
	 * 每天凌晨刷新 新游推荐 排行榜
	 */
	private static void refreshRecommendGameRank() {
		try {
			// 判断redis是否有新游数据，如果没有获取新游列表

			// 根据设计提供的算法 排序列表

			// 获取前10数据 更新排行榜

		} catch (Exception e) {
			GlobalObject.ERROR_LOG
					.error("at RankComponent.refreshRecommendGameRank throw an error.",
							e);
		}

	}

	public static void execRankTask() {
		Runnable task = new Runnable() {

			@Override
			public void run() {
				refreshHotForumRank();
				refreshRecommendGameRank();
			}
		};
		GlobalObject.SCHED_EXECUTOR.scheduleAtFixedRate(task, getInitDelay(),
				24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
	}

	/***
	 * 当前时间与凌晨时间的毫秒差值
	 * 
	 * @return
	 */
	private static long getInitDelay() {
		Calendar cl = Calendar.getInstance();
		cl.get(Calendar.HOUR_OF_DAY);
		cl.set(Calendar.HOUR_OF_DAY, 24);
		return cl.getTimeInMillis() - System.currentTimeMillis();
	}

}
