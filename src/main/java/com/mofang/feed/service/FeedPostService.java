package com.mofang.feed.service;

import java.util.Set;

import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.FeedPostAndComment;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedPostService
{
	/**
	 * 添加楼层(回复主题)
	 * @param model 楼层实体
	 * @return
	 * @throws Exception
	 */
	public long add(FeedPost model) throws Exception;
	
	/**
	 * 编辑楼层
	 * @param model 楼层实体
	 * @throws Exception
	 */
	public void edit(FeedPost model) throws Exception;
	
	/**
	 * 将楼层放入回收站
	 * @param model 楼层实体
	 * @throws Exception
	 */
	public void delete(FeedPost model) throws Exception;
	
	/**
	 * 从回收站中还原楼层
	 * @param model 楼层实体
	 * @throws Exception
	 */
	public void restore(FeedPost model) throws Exception;
	
	/**
	 * 从回收站中移除楼层
	 * @param model 楼层实体
	 * @throws Exception
	 */
	public void remove(FeedPost model) throws Exception;
	
	/**
	 * 楼层点赞
	 * @param userId 用户ID
	 * @param postId 楼层ID
	 * @throws Exception
	 */
	public void setRecommend(long userId, long postId) throws Exception;
	
	/**
	 * 楼层取消点赞
	 * @param userId 用户ID
	 * @param postId 楼层ID
	 * @throws Excpetion
	 */
	public void cancelRecommend(long userId, long postId) throws Exception;
	
	/**
	 * 用户是否对该楼层点赞
	 * @param userId 用户ID
	 * @param postId 楼层ID
	 * @return
	 * @throws Exception
	 */
	public boolean existsRecommend(long userId, long postId) throws Exception;
	
	/**
	 * 获取用户点赞的楼层ID集合
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public Set<String> getUserRecommendPostSet(long userId) throws Exception;
	
	/**
	 * 获取楼层信息
	 * @param postId 楼层ID
	 * @param source 数据来源
	 * @return
	 * @throws Exception
	 */
	public FeedPost getInfo(long postId, DataSource source) throws Exception;
	
	/**
	 * 获取1楼内容
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
	 * 获取楼层列表
	 * @param threadId 主题ID(等于0时则不区分主题)
	 * @param status 主题状态
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedPost> getPostList(long threadId, int status, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取主题楼层列表
	 * @param threadId 主题ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedPost> getThreadPostList(long threadId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取主题楼层列表(web端评论使用)
	 * @param threadId 主题ID
	 * @param postId 楼层ID(从当前楼层开始)
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedPost> getThreadPostList(long threadId, long postId, int pageSize) throws Exception;
	
	/**
	 * 获取主题楼层数
	 * @param threadId 主题ID
	 * @return
	 * @throws Exception
	 */
	public long getThreadPostCount(long threadId) throws Exception;
	
	/**
	 * 获取主题楼主楼层列表(只看楼主)
	 * @param threadId 主题ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedPost> getHostPostList(long threadId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户楼层列表
	 * @param userId 用户ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedPost> getUserPostList(long userId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户楼层总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserPostCount(long userId) throws Exception;
	
	/**
	 * 获取用户回复列表(楼层 + 评论)
	 * @param userId 用户ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedPostAndComment> getUserReplyList(long userId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户回复总数(楼层 + 评论)
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserReplyCount(long userId) throws Exception;
	
	/**
	 * 搜索楼层
	 * @param forumId 版块ID
	 * @param forumName 版块名称
	 * @param author 楼层作者
	 * @param keyword 关键字
	 * @param status 楼层状态
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedPost> search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}