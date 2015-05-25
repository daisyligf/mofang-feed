package com.mofang.feed.redis;

import java.util.List;
import java.util.Set;

import com.mofang.feed.model.FeedComment;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedCommentRedis
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
	public void initUniqueId(long commentId) throws Exception;
	
	/**
	 * 保存评论信息
	 * @param model 评论实体信息
	 * @return
	 * @throws Exception
	 */
	public void save(FeedComment model) throws Exception;
	
	/**
	 * 删除评论
	 * @param commentId 评论ID
	 * @return
	 * @throws Exception
	 */
	public void delete(long commentId) throws Exception;
	
	/**
	 * 获取评论信息
	 * @param commentId 评论ID
	 * @return
	 * @throws Exception
	 */
	public FeedComment getInfo(long commentId) throws Exception;
	
	/**
	 * 将评论ID添加到楼层评论列表
	 * @param postId 楼层ID
	 * @param commentId 评论ID
	 * @param score 评论发表时间
	 * @return
	 * @throws Exception
	 */
	public void addPostCommentList(long postId, long commentId, long score) throws Exception;
	
	/**
	 * 将评论ID从楼层评论列表中删除
	 * @param postId 楼层ID
	 * @param commentId 评论ID
	 * @return
	 * @throws Exception
	 */
	public void deleteFromPostCommentList(long postId, long commentId) throws Exception;
	
	/**
	 * 获取楼层评论列表
	 * @param postId 楼层ID
	 * @param start 记录起始位置
	 * @param end 记录截止位置
	 * @return
	 * @throws Exception
	 */
	public Set<String> getPostCommentList(long postId, int start, int end) throws Exception;
	
	/**
	 * 获取楼层评论总数
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public long getPostCommentCount(long postId) throws Exception;
	
	/**
	 * 删除楼层评论列表
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public void deletePostCommentListByPostId(long postId) throws Exception;
	
	/**
	 * 将Set转换成实体列表
	 * @param idSet id集合
	 * @return
	 * @throws Exception
	 */
	public List<FeedComment> convertEntityList(Set<String> idSet) throws Exception;
	
	/**
	 * 将List转换成实体列表
	 * @param idList id列表
	 * @return
	 * @throws Exception
	 */
	public List<FeedComment> convertEntityList(List<Long> idList) throws Exception;
}