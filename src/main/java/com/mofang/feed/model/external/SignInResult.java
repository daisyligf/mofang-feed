package com.mofang.feed.model.external;

public class SignInResult {
	//签到状态
	public boolean isSignIn = false;
	//连续签到天数
	public int days;
	//第几个签到
	public int rank;
	//已签到总人数
	public int totalMember;
	//重复签到状态
	public boolean repeat = false;
}
