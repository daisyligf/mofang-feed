package com.mofang.feed.model;

import com.mofang.feed.global.common.ModeratorApplyStatus;
import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_moderator_apply")
public class FeedModeratorApply
{
	@PrimaryKey
	@ColumnName(name = "apply_id")
	private int applyId;
	@ColumnName(name = "forum_id")
	private long forumId = 0L;
	@ColumnName(name = "forum_name")
	private String forumName;
	@ColumnName(name = "user_id")
	private long userId = 0L;
	@ColumnName(name = "nick_name")
	private String nickName;
	@ColumnName(name = "register_time")
	private long registerTime = System.currentTimeMillis();
	@ColumnName(name = "contact_qq")
	private String contactQQ;
	@ColumnName(name = "contact_mobile")
	private String contactMobile;
	@ColumnName(name = "reason")
	private String reason;
	@ColumnName(name = "status")
	private int status = ModeratorApplyStatus.UNHANDLE;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();
	@ColumnName(name = "update_time")
	private long updateTime = System.currentTimeMillis();

	public int getApplyId() {
		return applyId;
	}

	public void setApplyId(int applyId) {
		this.applyId = applyId;
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

	public long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public String getContactQQ() {
		return contactQQ;
	}

	public void setContactQQ(String contactQQ) {
		this.contactQQ = contactQQ;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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
}