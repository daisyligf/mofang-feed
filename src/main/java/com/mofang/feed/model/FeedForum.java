package com.mofang.feed.model;

import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.util.MapDecorator;
import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_forum")
public class FeedForum
{
	@PrimaryKey
	@ColumnName(name = "forum_id")
	private long forumId;
	@ColumnName(name = "parent_id")
	private long parentId = 0L;
	@ColumnName(name = "name")
	private String name;
	@ColumnName(name = "name_spell")
	private String nameSpell = "";
	@ColumnName(name = "icon")
	private String icon = "";
	@ColumnName(name = "color")
	private String color = "";
	@ColumnName(name = "type")
	private int type = ForumType.GAME;
	@ColumnName(name = "is_edit")
	private boolean isEdit = true;
	@ColumnName(name = "is_hidden")
	private boolean isHidden = false;
	@ColumnName(name = "threads")
	private int threads = 0;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();
	@ColumnName(name = "update_time")
	private long updateTime = System.currentTimeMillis();
	@ColumnName(name = "game_id")
	private int gameId;
	private int yestodayThreads = 0;
	private int todayThreads = 0;
	
	public FeedForum()
	{}
	
	public FeedForum(Map<String, String> map) throws Exception
	{
		try
		{
			MapDecorator decorator = new MapDecorator(map);
			this.forumId = decorator.optLong("forum_id", 0L);
			this.parentId = decorator.optLong("parent_id", 0L); 
			this.name = decorator.optString("name", ""); 
			this.nameSpell = decorator.optString("name_spell", ""); 
			this.icon = decorator.optString("icon", "");
			this.color = decorator.optString("color", "");
			this.type = decorator.optInt("type", ForumType.GAME);
			this.isEdit = decorator.optBoolean("is_edit", true);
			this.isHidden = decorator.optBoolean("is_hidden", false);
			this.threads = decorator.optInt("threads", 0); 
			this.createTime = decorator.optLong("create_time", System.currentTimeMillis()); 
			this.yestodayThreads = decorator.optInt("yestoday_threads", 0);
			this.todayThreads = decorator.optInt("today_threads", 0);
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameSpell() {
		return nameSpell == null ? "" : nameSpell;
	}

	public void setNameSpell(String nameSpell) {
		this.nameSpell = nameSpell;
	}

	public String getIcon() {
		return icon == null ? "" : icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getColor() {
		return color == null ? "" : color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
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

	public int getYestodayThreads() {
		return yestodayThreads;
	}

	public void setYestodayThreads(int yestodayThreads) {
		this.yestodayThreads = yestodayThreads;
	}

	public int getTodayThreads() {
		return todayThreads;
	}

	public void setTodayThreads(int todayThreads) {
		this.todayThreads = todayThreads;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
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
			decorator.put("parent_id", parentId);
			decorator.put("name", name);
			decorator.put("name_spell", nameSpell);
			decorator.put("icon", icon);
			decorator.put("color", color);
			decorator.put("type", type);
			decorator.put("is_edit", isEdit);
			decorator.put("is_hidden", isHidden);
			decorator.put("threads", threads);
			decorator.put("create_time", createTime);
			decorator.put("yestoday_threads", yestodayThreads);
			decorator.put("today_threads", todayThreads);
			return decorator.toMap();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForum.toMap throw an error.", e);
			return null;
		}
	}
}