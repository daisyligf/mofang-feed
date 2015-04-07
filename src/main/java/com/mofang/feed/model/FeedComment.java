package com.mofang.feed.model;

import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.util.MapDecorator;
import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_comment")
public class FeedComment
{
	@PrimaryKey
	@ColumnName(name = "comment_id")
	private long commentId;
	@ColumnName(name = "forum_id")
	private long forumId;
	@ColumnName(name = "thread_id")
	private long threadId;
	@ColumnName(name = "post_id")
	private long postId;
	@ColumnName(name = "user_id")
	private long userId;
	@ColumnName(name = "content")
	private String content;
	@ColumnName(name = "content_filter")
	private String contentFilter;
	@ColumnName(name = "content_mark")
	private String contentMark;
	@ColumnName(name = "status")
	private int status = CommentStatus.NORMAL;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();
	@ColumnName(name = "update_time")
	private long updateTime = System.currentTimeMillis();
	
	public FeedComment()
	{}
	
	public FeedComment(Map<String, String> map) throws Exception
	{
		try
		{
			MapDecorator decorator = new MapDecorator(map);
			this.commentId = decorator.optLong("comment_id", 0L);
			this.forumId = decorator.optLong("forum_id", 0L);
			this.threadId = decorator.optLong("thread_id", 0L);
			this.postId = decorator.optLong("post_id", 0L);
			this.userId = decorator.optLong("user_id", 0L);
			this.content = decorator.optString("content", "");
			this.contentFilter = decorator.optString("content_filter", "");
			this.status = decorator.optInt("status", CommentStatus.NORMAL);
			this.createTime = decorator.optLong("create_time", System.currentTimeMillis());
			this.updateTime = decorator.optLong("update_time", System.currentTimeMillis());
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	public long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content == null ? "" : content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentFilter() {
		return contentFilter == null ? "" : contentFilter;
	}

	public void setContentFilter(String contentFilter) {
		this.contentFilter = contentFilter;
	}

	public String getContentMark() {
		return contentMark == null ? "" : contentMark;
	}

	public void setContentMark(String contentMark) {
		this.contentMark = contentMark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 将实体对象转换为redis的hashmap
	 * @return
	 */
	public Map<String, String> toMap()
	{
		try
		{
			MapDecorator decorator = new MapDecorator();
			decorator.put("comment_id", commentId);
			decorator.put("forum_id", forumId);
			decorator.put("thread_id", threadId);
			decorator.put("post_id", postId);
			decorator.put("user_id", userId);
			decorator.put("content", content);
			decorator.put("content_filter", contentFilter);
			decorator.put("status", status);
			decorator.put("create_time", createTime);
			decorator.put("update_time", updateTime);
			return decorator.toMap();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedComment.toMap throw an error.", e);
			return null;
		}
	}
}