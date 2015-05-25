package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_role_change_history")
public class FeedRoleChangeHistory
{
	@PrimaryKey
	@ColumnName(name = "history_id")
	private int historyId;
	@ColumnName(name = "user_id")
	private long userId = 0L;
	@ColumnName(name = "nick_name")
	private String nickName;
	@ColumnName(name = "original_role_id")
	private int originalRoleId = 0;
	@ColumnName(name = "original_role_name")
	private String originalRoleName;
	@ColumnName(name = "current_role_id")
	private int currentRoleId = 0;
	@ColumnName(name = "current_role_name")
	private String currentRoleName;
	@ColumnName(name = "forum_id")
	private long forumId = 0L;
	@ColumnName(name = "forum_name")
	private String forumName;
	@ColumnName(name = "operate_reason")
	private String operateReason;
	@ColumnName(name = "operate_id")
	private long operatorId = 0L;
	@ColumnName(name = "operate_name")
	private String operatorName;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();

	public int getHistoryId() {
		return historyId;
	}

	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getOriginalRoleId() {
		return originalRoleId;
	}

	public void setOriginalRoleId(int originalRoleId) {
		this.originalRoleId = originalRoleId;
	}

	public String getOriginalRoleName() {
		return originalRoleName;
	}

	public void setOriginalRoleName(String originalRoleName) {
		this.originalRoleName = originalRoleName;
	}

	public int getCurrentRoleId() {
		return currentRoleId;
	}

	public void setCurrentRoleId(int currentRoleId) {
		this.currentRoleId = currentRoleId;
	}

	public String getCurrentRoleName() {
		return currentRoleName;
	}

	public void setCurrentRoleName(String currentRoleName) {
		this.currentRoleName = currentRoleName;
	}

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public String getOperateReason() {
		return operateReason;
	}

	public void setOperateReason(String operateReason) {
		this.operateReason = operateReason;
	}

	public long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}