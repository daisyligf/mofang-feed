package com.mofang.feed.model;

import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_operate_history")
public class FeedOperateHistory
{
	@PrimaryKey
	@ColumnName(name = "history_id")
	private long historyId;
	@ColumnName(name = "user_id")
	private long userId = 0L;
	@ColumnName(name = "nick_name")
	private String nickName;
	@ColumnName(name = "forum_id")
	private long forumId = 0L;
	@ColumnName(name = "forum_name")
	private String forumName;
	@ColumnName(name = "privilege_id")
	private int privilegeId = 0;
	@ColumnName(name = "source_type")
	private int sourceType;
	@ColumnName(name = "source_id")
	private long sourceId = 0;
	@ColumnName(name = "operate_behavior")
	private int operateBehavior = 0;
	@ColumnName(name = "operate_reason")
	private String operateReason;
	@ColumnName(name = "operator_id")
	private long operatorId;
	@ColumnName(name = "operator_name")
	private String operatorName;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();

	public long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName == null ? "" : nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public String getForumName() {
		return forumName == null ? "" : forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public int getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(int privilegeId) {
		this.privilegeId = privilegeId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public int getOperateBehavior() {
		return operateBehavior;
	}

	public void setOperateBehavior(int operateBehavior) {
		this.operateBehavior = operateBehavior;
	}

	public String getOperateReason() {
		return operateReason == null ? "" : operateReason;
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
		return operatorName == null ? "" : operatorName;
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