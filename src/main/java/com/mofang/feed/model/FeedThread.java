package com.mofang.feed.model;

import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.global.common.ThreadType;
import com.mofang.feed.global.common.ThreadUpDown;
import com.mofang.feed.util.MapDecorator;
import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_thread")
public class FeedThread
{
	@PrimaryKey
	@ColumnName(name = "thread_id")
	private long threadId;
	@ColumnName(name = "forum_id")
	private long forumId;
	@ColumnName(name = "user_id")
	private long userId = 0L;
	@ColumnName(name = "subject")
	private String subject;
	@ColumnName(name = "subject_filter")
	private String subjectFilter;
	@ColumnName(name = "subject_mark")
	private String subjectMark;
	@ColumnName(name = "link_url")
	private String linkUrl;
	@ColumnName(name = "type")
	private int type = ThreadType.NORMAL;
	@ColumnName(name = "status")
	private int status = ThreadStatus.NORMAL;
	@ColumnName(name = "is_top")
	private boolean isTop = false;
	@ColumnName(name = "is_closed")
	private boolean isClosed = false;
	@ColumnName(name = "is_elite")
	private boolean isElite = false;
	@ColumnName(name = "is_mark")
	private boolean isMark = false;
	@ColumnName(name = "is_video")
	private boolean isVideo = false;
	@ColumnName(name = "replies")
	private int replies = 0;
	@ColumnName(name = "recommends")
	private int recommends = 0;
	@ColumnName(name = "share_times")
	private int shareTimes = 0;
	@ColumnName(name = "page_view")
	private int pageView = 0;
	@ColumnName(name = "last_post_uid")
	private long lastPostUid = 0L;
	@ColumnName(name = "updown")
	private int updown = ThreadUpDown.NORMAL;
	@ColumnName(name = "last_post_time")
	private long lastPostTime = System.currentTimeMillis();
	@ColumnName(name = "top_time")
	private long topTime = System.currentTimeMillis();
	@ColumnName(name = "updown_time")
	private long updownTime = System.currentTimeMillis();
	@ColumnName(name = "game_id")
	private int gameId = 0;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();
	@ColumnName(name = "update_time")
	private long updateTime = System.currentTimeMillis();
	
	private FeedPost post = null;
	
	public FeedThread()
	{}
	
	public FeedThread(Map<String, String> map) throws Exception
	{
		try
		{
			MapDecorator decorator = new MapDecorator(map);
			this.threadId = decorator.optLong("thread_id", 0L); 
			this.forumId = decorator.optLong("forum_id", 0L);
			this.userId = decorator.optLong("user_id", 0L);
			this.subject = decorator.optString("subject", ""); 
			this.subjectFilter = decorator.optString("subject_filter", "");
			this.linkUrl = decorator.optString("link_url", ""); 
			this.type = decorator.optInt("type", ThreadType.NORMAL); 
			this.status = decorator.optInt("status", ThreadStatus.NORMAL);
			this.isTop = decorator.optBoolean("is_top", false);
			this.isClosed = decorator.optBoolean("is_closed", false);
			this.isElite = decorator.optBoolean("is_elite", false);
			this.isMark = decorator.optBoolean("is_mark", false);
			this.isVideo = decorator.optBoolean("is_video", false);
			this.replies = decorator.optInt("replies", 0); 
			this.recommends = decorator.optInt("recommends", 0); 
			this.shareTimes = decorator.optInt("share_times", 0); 
			this.pageView = decorator.optInt("page_view", 0); 
			this.lastPostUid = decorator.optLong("last_post_uid", 0L);
			this.updown = decorator.optInt("updown", ThreadUpDown.NORMAL);
			this.lastPostTime = decorator.optLong("last_post_time", System.currentTimeMillis()); 
			this.topTime = decorator.optLong("top_time", System.currentTimeMillis()); 
			this.updownTime = decorator.optLong("updown_time", System.currentTimeMillis());
			this.gameId = decorator.optInt("game_id", 0);
			this.createTime = decorator.optLong("create_time", System.currentTimeMillis());
			this.updateTime = decorator.optLong("update_time", System.currentTimeMillis());
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getSubject() {
		return subject == null ? "" : subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubjectFilter() {
		return subjectFilter == null ? "" : subjectFilter ;
	}

	public void setSubjectFilter(String subjectFilter) {
		this.subjectFilter = subjectFilter;
	}

	public String getSubjectMark() {
		return subjectMark == null ? "" : subjectMark;
	}

	public void setSubjectMark(String subjectMark) {
		this.subjectMark = subjectMark;
	}

	public String getLinkUrl() {
		return linkUrl == null ? "" : linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public boolean isElite() {
		return isElite;
	}

	public void setElite(boolean isElite) {
		this.isElite = isElite;
	}

	public boolean isMark() {
		return isMark;
	}

	public void setMark(boolean isMark) {
		this.isMark = isMark;
	}

	public boolean isVideo() {
		return isVideo;
	}

	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public int getRecommends() {
		return recommends;
	}

	public void setRecommends(int recommends) {
		this.recommends = recommends;
	}

	public int getShareTimes() {
		return shareTimes;
	}

	public void setShareTimes(int shareTimes) {
		this.shareTimes = shareTimes;
	}

	public int getPageView() {
		return pageView;
	}

	public void setPageView(int pageView) {
		this.pageView = pageView;
	}

	public long getLastPostUid() {
		return lastPostUid;
	}

	public void setLastPostUid(long lastPostUid) {
		this.lastPostUid = lastPostUid;
	}

	public int getUpdown() {
		return updown;
	}

	public void setUpdown(int updown) {
		this.updown = updown;
	}

	public long getLastPostTime() {
		return lastPostTime;
	}

	public void setLastPostTime(long lastPostTime) {
		this.lastPostTime = lastPostTime;
	}

	public long getTopTime() {
		return topTime;
	}

	public void setTopTime(long topTime) {
		this.topTime = topTime;
	}

	public long getUpdownTime() {
		return updownTime;
	}

	public void setUpdownTime(long updownTime) {
		this.updownTime = updownTime;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
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
	
	public FeedPost getPost() {
		return post;
	}

	public void setPost(FeedPost post) {
		this.post = post;
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
			decorator.put("forum_id", forumId);
			decorator.put("thread_id", threadId);
			decorator.put("user_id", userId);
			decorator.put("subject", subject);
			decorator.put("subject_filter", subjectFilter);
			decorator.put("link_url", linkUrl);
			decorator.put("type", type);
			decorator.put("status", status);
			decorator.put("is_top", isTop);
			decorator.put("is_closed", isClosed);
			decorator.put("is_elite", isElite);
			decorator.put("is_mark", isMark);
			decorator.put("is_video", isVideo);
			decorator.put("replies", replies);
			decorator.put("recommends", recommends);
			decorator.put("share_times", shareTimes);
			decorator.put("page_view", pageView);
			decorator.put("last_post_uid", lastPostUid);
			decorator.put("updown", updown);
			decorator.put("last_post_time", lastPostTime);
			decorator.put("top_time", topTime);
			decorator.put("updown_time", updownTime);
			decorator.put("game_id", gameId);
			decorator.put("create_time", createTime);
			decorator.put("update_time", updateTime);
			return decorator.toMap();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedThread.toMap throw an error.", e);
			return null;
		}
	}
}