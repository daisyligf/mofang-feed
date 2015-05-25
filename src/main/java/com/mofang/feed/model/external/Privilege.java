package com.mofang.feed.model.external;

/**
 * 
 * @author zhaodx
 *
 */
public class Privilege
{
	private long userId;
	private long forumId;
	private int privilegeCode;
	
	public Privilege(long userId, long forumId, int privilegeCode)
	{
		this.userId = userId;
		this.forumId = forumId;
		this.privilegeCode = privilegeCode;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getForumId() {
		return forumId;
	}

	public void setForumId(long forumId) {
		this.forumId = forumId;
	}

	public int getPrivilegeCode() {
		return privilegeCode;
	}

	public void setPrivilegeCode(int privilegeCode) {
		this.privilegeCode = privilegeCode;
	}
}