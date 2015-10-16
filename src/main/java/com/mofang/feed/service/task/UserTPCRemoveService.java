package com.mofang.feed.service.task;

/***
 * 删除用户 所有帖子，楼层，评论
 * @author linjx
 *
 */
public interface UserTPCRemoveService {

	public void delete(long userId) throws Exception;
	
}
