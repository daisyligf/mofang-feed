package com.mofang.feed.global.common;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedPrivilege
{
	/*******************************************版主权限*******************************************/
	/**
	 * 删除主题
	 */
	public final static int DELETE_THREAD = 101;
	
	/**
	 * 设置/取消主题置顶
	 */
	public final static int TOP_THREAD = 102;
	
	/**
	 * 设置/取消精华主题
	 */
	public final static int ELITE_THREAD = 103;
	
	/**
	 * 设置/取消禁言(黑名单)
	 */
	public final static int BLACK_LIST = 106;
	
	/**
	 * 删除楼层
	 */
	public final static int DELETE_POST = 107;
	
	/**
	 * 删除评论
	 */
	public final static int DELETE_COMMENT = 108;
	
	/**
	 * 奖励
	 */
	public final static int REWARD = 109;
	
	/**
	 * 标红
	 */
	public final static int MARK_THREAD = 110;
	
	/**
	 * 移动主题
	 */
	public final static int MOVE_THREAD = 111;
	
	/**
	 * 上升下沉
	 */
	public final static int UPDOWN = 112;
	
	/**
	 * 关闭主题(禁止回复)
	 */
	public final static int CLOSE_THREAD = 113;
}