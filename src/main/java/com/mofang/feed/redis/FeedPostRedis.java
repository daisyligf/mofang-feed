package com.mofang.feed.redis;

import java.util.List;
import java.util.Set;

import com.mofang.feed.model.FeedPost;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostRedis
{
	/**
	 * 生成主键ID
	 * @return
	 * @throws Exception
	 */
	public long makeUniqueId() throws Exception;
	
	/**
	 * 初始化主键ID
	 * @throws Exception
	 */
	public void initUniqueId(long postId) throws Exception;
	
	/**
	 * 保存楼层信息
	 * @param model 楼层实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedPost model) throws Exception;
	
	/**
	 * 删除楼层
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void delete(long postId) throws Exception;
	
	/**
	 * 获取楼层信息
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public FeedPost getInfo(long postId) throws Exception;
	
	/**
	 * 初始化楼层数
	 * @param threadId 主题ID
	 * @param position 楼层数
	 * @throws Exception
	 */
	public void initPosition(long threadId, int position) throws Exception;
	
	/**
	 * 递增楼层数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public int incrPosition(long threadId) throws Exception;
	
	/**
	 * 递增楼层评论数
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void incrComments(long postId) throws Exception;
	
	/**
	 * 递减楼层评论数
	 * @param postId
	 * @return
	 * @throws Exception
	 */
	public void decrComments(long postId) throws Exception;
	
	/**
	 * 递增楼层点赞数
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void incrRecommends(long postId) throws Exception;
	
	/**
	 * 递减楼层点赞数
	 * @param postId 楼层ID
	 * @throws Exception
	 */
	public void decrRecommends(long postId) throws Exception;
	
	/**
	 * 获取1楼楼层实体信息
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public FeedPost getStartPost(long threadId) throws Exception;
	
	/**
	 * 获取楼层所在的位置(非position)
	 * @param threadId 主题ID
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public int getRank(long threadId, long postId) throws Exception;
	
	/**
	 * 将楼层ID添加到主题楼层列表
	 * @param threadId 主题ID
	 * @param postId 楼层ID
	 * @param score 楼层position
	 * @return
	 * @throws Exception
	 */
	public void addThreadPostList(long threadId, long postId, long score) throws Exception;
	
	/**
	 * 将楼层ID从主题楼层列表中删除
	 * @param threadId 主题ID
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromThreadPostList(long threadId, long postId) throws Exception;
	
	/**
	 * 获取主题楼层列表
	 * @param threadId 主题ID
	 * @param start 记录起始位置
	 * @param end 记录截止位置
	 * @return
	 * @throws Exception
	 */
	public Set<String> getThreadPostList(long threadId, int start, int end) throws Exception;
	
	/**
	 * 获取主题楼层总数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public long getThreadPostCount(long threadId) throws Exception;
	
	/**
	 * 删除主题楼层列表
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void deleteThreadPostListByThreadId(long threadId) throws Exception;
	
	/**
	 * 将楼层ID添加到楼主楼层列表中(只看楼主)
	 * @param threadId 主题ID
	 * @param postId 楼层ID
	 * @param score 楼层position
	 * @return
	 * @throws Exception
	 */
	public void addHostPostList(long threadId, long postId, long score) throws Exception;
	
	/**
	 * 将楼层ID从楼主楼层列表中删除
	 * @param threadId 主题ID
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromHostPostList(long threadId, long postId) throws Exception;
	
	/**
	 * 获取楼主楼层列表
	 * @param threadId 主题ID
	 * @param start 记录起始位置
	 * @param end 记录截止位置
	 * @return
	 * @throws Exception
	 */
	public Set<String> getHostPostList(long threadId, int start, int end) throws Exception;
	
	/**
	 * 获取楼主楼层总数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public long getHostPostCount(long threadId) throws Exception;
	
	/**
	 * 删除楼主楼层列表
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public void deleteHostPostListByThreadId(long threadId) throws Exception;
	
	/**
	 * 将楼层ID添加到用户点赞楼层列表
	 * @param userId 用户ID
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void addUserRecommendPostList(long userId, long postId) throws Exception;
	
	/**
	 * 将楼层ID从用户点赞楼层列表中删除
	 * @param userId 用户ID
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromUserRecommendPostList(long userId, long postId) throws Exception;
	
	/**
	 * 判断楼层是否存在用户点赞楼层列表
	 * @param userId 用户ID
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public boolean existsUserRecommendPost(long userId, long postId) throws Exception;
	
	/**
	 * 获取用户点赞楼层ID集合
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public Set<String> getUserRecommendPostSet(long userId) throws Exception;
	
	/**
	 * 将Set转换成实体列表
	 * @param idSet id集合
	 * @return
	 * @throws Exception
	 */
	public List<FeedPost> convertEntityList(Set<String> idSet) throws Exception;
	
	/**
	 * 将List转换成实体列表
	 * @param idList id列表
	 * @return
	 * @throws Exception
	 */
	public List<FeedPost> convertEntityList(List<Long> idList) throws Exception;
}