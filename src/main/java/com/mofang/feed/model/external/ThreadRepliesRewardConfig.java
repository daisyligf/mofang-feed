package com.mofang.feed.model.external;

public class ThreadRepliesRewardConfig implements Comparable<ThreadRepliesRewardConfig>{
	//级别
	public int level;
	//回复数min
	public int repliesRangeMin;
	//回复数max
	public int repliesRangeMax;
	//奖励的经验值
	public int exp;
	//随机值begin
	public int randomMin;
	//随机值end
	public int randomMax;
	
	@Override
	public int compareTo(ThreadRepliesRewardConfig o) {
		return level > o.level ? -1 : level == o.level ? 0 : 1;
	}
	
}
