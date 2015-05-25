package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_module")
public class FeedModule
{
	@PrimaryKey
	@ColumnName(name = "module_id")
	private long moduleId;
	@ColumnName(name = "name")
	private String name;
	@ColumnName(name = "icon")
	private String icon = "";
	@ColumnName(name = "threads")
	private int threads = 0;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public String getName() {
		return name == null ? "" : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon == null ? "" : icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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
}