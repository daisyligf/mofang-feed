package com.mofang.feed.model;

import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.ModuleItemStatus;
import com.mofang.feed.util.MapDecorator;
import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_module_item")
public class FeedModuleItem
{
	@PrimaryKey
	@ColumnName(name = "item_id")
	private long itemId;
	@ColumnName(name = "module_id")
	private long moduleId = 0;
	@ColumnName(name = "thread_id")
	private long threadId = 0L;
	@ColumnName(name = "title")
	private String title;
	@ColumnName(name = "subtitle")
	private String subTitle;
	@ColumnName(name = "pic_url")
	private String picUrl = "";
	@ColumnName(name = "display_order")
	private int displayOrder = 0;
	@ColumnName(name = "status")
	private int status = ModuleItemStatus.UNCONFIG;
	@ColumnName(name = "online_time")
	private long onlineTime = System.currentTimeMillis();
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();
	@ColumnName(name = "update_time")
	private long updateTime = System.currentTimeMillis();
	
	private FeedThread thread;
	
	public FeedModuleItem()
	{}
	
	public FeedModuleItem(Map<String, String> map) throws Exception
	{
		try
		{
			MapDecorator decorator = new MapDecorator(map);
			this.itemId = decorator.optLong("item_id", 0L);
			this.moduleId = decorator.optLong("module_id", 0L); 
			this.threadId = decorator.optLong("thread_id", 0L); 
			this.title = decorator.optString("title", "");
			this.subTitle = decorator.optString("subtitle", ""); 
			this.picUrl = decorator.optString("pic_url", ""); 
			this.displayOrder = decorator.optInt("display_order", 0); 
			this.status = decorator.optInt("status", ModuleItemStatus.UNCONFIG); 
			this.onlineTime = decorator.optLong("online_time", System.currentTimeMillis()); 
			this.createTime = decorator.optLong("create_time", System.currentTimeMillis()); 
			this.updateTime = decorator.optLong("update_time", System.currentTimeMillis()); 
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getTitle() {
		return title == null ? "" : title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle == null ? "" : subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getPicUrl() {
		return picUrl == null ? "" : picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(long onlineTime) {
		this.onlineTime = onlineTime;
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

	public FeedThread getThread() {
		return thread;
	}

	public void setThread(FeedThread thread) {
		this.thread = thread;
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
			decorator.put("item_id", itemId);
			decorator.put("module_id", moduleId);
			decorator.put("thread_id", threadId);
			decorator.put("title", title);
			decorator.put("subtitle", subTitle);
			decorator.put("pic_url", picUrl);
			decorator.put("display_order", displayOrder);
			decorator.put("status", status);
			decorator.put("online_time", onlineTime);
			decorator.put("create_time", createTime);
			decorator.put("update_time", updateTime);
			return decorator.toMap();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedModuleItem.toMap throw an error.", e);
			return null;
		}
	}
}