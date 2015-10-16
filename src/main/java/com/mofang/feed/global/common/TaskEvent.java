package com.mofang.feed.global.common;

/**
 * 
 * @author zhaodx
 *
 */
public class TaskEvent 
{
	/**
     * 发帖的任务代码
     */
    public final static int ADD_THREAD = 102;
    
    /**
     * 点赞的任务代码
     */
    public final static int RECOMMEND_THREAD = 103;
    
    /**
     * 回复或者评论的任务代码
     */
    public final static int REPLY = 104;
    
    /**
     * 收集赞的任务代码
     */
    public final static int COLLECT_RECOMMENDS = 110;
    
    /**
     * 帖子被加精的任务代码
     */
    public final static int ELITE_THREAD = 116;
    
    /**
     * 回复32个不同的帖子
     */
    public final static int REPLY_32_THREAD = 119;
}