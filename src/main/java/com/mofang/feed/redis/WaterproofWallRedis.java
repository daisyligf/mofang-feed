package com.mofang.feed.redis;

/**
 * 防水墙
 * @author zhaodx
 *
 */
public interface WaterproofWallRedis
{
	/**
	 * 更新用户最后发帖时间(包括主题、楼层、评论)
	 * @param userId 用户ID
	 * @param lastPostTime 最后回帖时间
	 * @throws Exception
	 */
	public void updateUserLastPostTime(long userId, long lastPostTime) throws Exception;
	
	/**
	 * 判断是否为恶意灌水
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public boolean isSpam(long userId) throws Exception;
}