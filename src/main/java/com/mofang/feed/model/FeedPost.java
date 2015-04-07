package com.mofang.feed.model;

import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.util.MapDecorator;
import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name="feed_post")
public class FeedPost
{
	@PrimaryKey
	@ColumnName(name = "post_id")
	private long postId;
	@ColumnName(name = "forum_id")
	private long forumId;
	@ColumnName(name = "thread_id")
	private long threadId;
	@ColumnName(name = "user_id")
	private long userId;
	@ColumnName(name = "content")
	private String content;
	@ColumnName(name = "content_filter")
	private String contentFilter;
	@ColumnName(name = "content_mark")
	private String contentMark;
	@ColumnName(name = "html_content")
	private String htmlContent;
	@ColumnName(name = "html_content_filter")
	private String htmlContentFilter;
	@ColumnName(name = "html_content_mark")
	private String htmlContentMark;
	@ColumnName(name = "pictures")
	private String pictures;
	@ColumnName(name = "video_id")
	private long videoId;
	@ColumnName(name = "thumbnail")
	private String thumbnail;
	@ColumnName(name = "duration")
	private int duration = 0;
	@ColumnName(name = "position")
	private int position;
	@ColumnName(name = "comments")
	private int comments;
	@ColumnName(name = "recommends")
	private int recommends;
	@ColumnName(name = "status")
	private int status = PostStatus.NORMAL;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();
	@ColumnName(name = "update_time")
	private long updateTime = System.currentTimeMillis();
	
	public FeedPost()
	{}
	
	public FeedPost(Map<String, String> map) throws Exception
	{
		try
		{
			MapDecorator decorator = new MapDecorator(map);
			this.postId = decorator.optLong("post_id", 0L);
			this.forumId = decorator.optLong("forum_id", 0L);
			this.threadId = decorator.optLong("thread_id", 0L);
			this.userId = decorator.optLong("user_id", 0L);
			this.content = decorator.optString("content", "");
			this.contentFilter = decorator.optString("content_filter", "");
			this.htmlContent = decorator.optString("html_content", "");
			this.htmlContentFilter = decorator.optString("html_content_filter", "");
			this.pictures = decorator.optString("pictures", ""); 
			this.videoId = decorator.optLong("video_id", 0L);
			this.thumbnail = decorator.optString("thumbnail", "");
			this.duration = decorator.optInt("duration", 0);
			this.position = decorator.optInt("position", 0); 
			this.comments = decorator.optInt("comments", 0); 
			this.recommends = decorator.optInt("recommends", 0);
			this.status = decorator.optInt("status", PostStatus.NORMAL);
			this.createTime = decorator.optLong("create_time", System.currentTimeMillis());
			this.updateTime = decorator.optLong("update_time", System.currentTimeMillis());
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	public long getPostId() {
		return postId;
	}

	public void setPostId(long postId) {
		this.postId = postId;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content == null  ? "" : content;
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

	public String getHtmlContent() {
		return htmlContent == null ? "" : htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getHtmlContentFilter() {
		return htmlContentFilter == null ? "" : htmlContentFilter;
	}

	public void setHtmlContentFilter(String htmlContentFilter) {
		this.htmlContentFilter = htmlContentFilter;
	}

	public String getHtmlContentMark() {
		return htmlContentMark == null ? "" : htmlContentMark;
	}

	public void setHtmlContentMark(String htmlContentMark) {
		this.htmlContentMark = htmlContentMark;
	}

	public String getPictures() {
		return pictures == null ? "" : pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public long getVideoId() {
		return videoId;
	}

	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}

	public String getThumbnail() {
		return thumbnail == null ? "" : thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public int getRecommends() {
		return recommends;
	}

	public void setRecommends(int recommends) {
		this.recommends = recommends;
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
			decorator.put("post_id", postId);
			decorator.put("forum_id", forumId);
			decorator.put("thread_id", threadId);
			decorator.put("user_id", userId);
			decorator.put("content", content);
			decorator.put("content_filter", contentFilter);
			decorator.put("html_content", htmlContent);
			decorator.put("html_content_filter", htmlContentFilter);
			decorator.put("pictures", pictures);
			decorator.put("video_id", videoId);
			decorator.put("thumbnail", thumbnail);
			decorator.put("duration", duration);
			decorator.put("position", position);
			decorator.put("comments", comments);
			decorator.put("recommends", recommends);
			decorator.put("status", status);
			decorator.put("create_time", createTime);
			decorator.put("update_time", updateTime);
			return decorator.toMap();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedPost.toMap throw an error.", e);
			return null;
		}
	}
}