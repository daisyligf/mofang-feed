package com.mofang.feed.model;

/**
 * 
 * @author zhaodx
 *
 */
public class ModeratorApplyCondition
{
	private boolean followForumIsOK;
	private boolean threadsIsOK;
	private boolean topEliteCountIsOK;
	private boolean timeIntervalIsOK;

	public boolean isFollowForumIsOK() {
		return followForumIsOK;
	}

	public void setFollowForumIsOK(boolean followForumIsOK) {
		this.followForumIsOK = followForumIsOK;
	}

	public boolean isThreadsIsOK() {
		return threadsIsOK;
	}

	public void setThreadsIsOK(boolean threadsIsOK) {
		this.threadsIsOK = threadsIsOK;
	}

	public boolean isTopEliteCountIsOK() {
		return topEliteCountIsOK;
	}

	public void setTopEliteCountIsOK(boolean topEliteCountIsOK) {
		this.topEliteCountIsOK = topEliteCountIsOK;
	}

	public boolean isTimeIntervalIsOK() {
		return timeIntervalIsOK;
	}

	public void setTimeIntervalIsOK(boolean timeIntervalIsOK) {
		this.timeIntervalIsOK = timeIntervalIsOK;
	}
}