package com.mofang.feed.service;

import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.model.FeedComment;
import com.mofang.feed.model.Page;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedCommentService
{
	/**
	 * 添加评论
	 * @param model 评论实体
	 * @throws Exception
	 */
	public long add(FeedComment model) throws Exception;
	
	/**
	 * 将评论放入回收站
	 * @param model 评论实体
	 * @throws Exception
	 */
	public void delete(FeedComment model) throws Exception;
	
	/**
	 * 从回收站中还原评论
	 * @param model 评论实体
	 * @throws Exception
	 */
	public void restore(FeedComment model) throws Exception;
	
	/**
	 * 从回收站中移除评论
	 * @param model 评论实体
	 * @throws Exception
	 */
	public void remove(FeedComment model) throws Exception;
	
	/**
	 * 获取评论信息
	 * @param commentId 评论ID
	 * @param source 数据来源
	 * @return
	 * @throws Exception
	 */
	public FeedComment getInfo(long commentId, DataSource source) throws Exception;
	
	/**
	 * 获取评论列表
	 * @param postId 楼层ID(等于0时则不区分楼层)
	 * @param status 评论状态
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedComment> getCommentList(long postId, int status, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取楼层评论列表
	 * @param postId 楼层ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedComment> getPostCommentList(long postId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户评论列表
	 * @param userId 用户ID
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedComment> getUserCommentList(long userId, int pageNum, int pageSize) throws Exception;
	
	/**
	 * 获取用户评论总数
	 * @param userId 用户ID
	 * @return
	 * @throws Exception
	 */
	public long getUserCommentCount(long userId) throws Exception;
	
	/**
	 * 搜索评论
	 * @param forumId 版块ID
	 * @param forumName 版块名称
	 * @param author 评论作者
	 * @param keyword 关键字
	 * @param status 评论状态
	 * @param pageNum 页数
	 * @param pageSize 每页记录数
	 * @return
	 * @throws Exception
	 */
	public Page<FeedComment> search(long forumId, String forumName, String author, String keyword, int status, int pageNum, int pageSize) throws Exception;
}