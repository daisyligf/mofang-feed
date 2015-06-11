package com.mofang.feed.global.common;

/**
 * 
 * @author zhaodx
 *
 */
public class OperateBehavior
{
	/**
	 * 删除主题
	 */
	public final static int DELETE_THREAD = 1;

	/**
	 * 删除楼层
	 */
    public final static int DELETE_POST = 2;

    /**
     * 删除评论
     */
    public final static int DELETE_COMMENT = 3;
    
    /**
     * 还原主题
     */
    public final static int RESTORE_THREAD = 4;
    
    /**
     * 还原楼层
     */
    public final static int RESTORE_POST = 5;
    
    /**
     * 还原评论
     */
    public final static int RESTORE_COMMENT = 6;

    /**
     * 设置精华帖
     */
    public final static int ELITE_THREAD = 7;

    /**
     * 取消精华帖
     */
    public final static int CANCEL_ELITE_THREAD = 8;

    /**
     * 置顶
     */
    public final static int TOP_THREAD = 9;

    /**
     * 取消置顶
     */
    public final static int CANCEL_TOP_THREAD = 10;
    
    /**
     * 关闭主题(锁帖)
     */
    public final static int CLOSE_THREAD = 11;
    
    /**
     * 打开主题(解锁)
     */
    public final static int OPEN_THREAD = 12;

    /**
     * 禁言用户
     */
    public final static int FORBID_USER = 13;

    /**
     * 解禁用户
     */
    public final static int RELIEVE_USER = 14;
    
    /**
     * 冻结用户
     */
    public final static int FREEZE_USER = 15;
    
    /**
     * 解冻用户
     */
    public final static int UNFREEZE_USER = 16;
}
