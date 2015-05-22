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
	
	/*******************************************后台权限*******************************************/
	/**
	 * 编辑主题
	 */
	public final static int EDIT_THREAD = 201;
	
	/**
	 * 还原主题
	 */
	public final static int RESTORE_THREAD = 202;
	
	/**
	 * 删除主题(从回收站中移除)
	 */
	public final static int REMOVE_THREAD = 203;
	
	/**
	 * 编辑楼层
	 */
	public final static int EDIT_POST = 204;
	
	/**
	 * 还原楼层
	 */
	public final static int RESTORE_POST = 205;
	
	/**
	 * 删除楼层(从回收站中移除)
	 */
	public final static int REMOVE_POST = 206;
	
	/**
	 * 还原评论
	 */
	public final static int RESTORE_COMMENT = 207;
	
	/**
	 * 删除评论(从回收站中移除)
	 */
	public final static int REMOVE_COMMENT = 208;
	
	/**
	 * 添加版块
	 */
	public final static int ADD_FORUM = 209;
	
	/**
	 * 编辑版块
	 */
	public final static int EDIT_FORUM = 210;
	
	/**
	 * 删除版块
	 */
	public final static int DELETE_FORUM = 211;
	
	/**
	 * 添加模块(虚拟版块)
	 */
	public final static int ADD_MODULE = 212;
	
	/**
	 * 编辑模块(虚拟版块)
	 */
	public final static int EDIT_MODULE = 213;
	
	/**
	 * 删除模块(虚拟版块)
	 */
	public final static int DELETE_MODULE = 214;
	
	/**
	 * 添加模块主题
	 */
	public final static int ADD_MODULE_ITEM = 215;
	
	/**
	 * 编辑模块主题
	 */
	public final static int EDIT_MODULE_ITEM = 216;
	
	/**
	 * 删除模块主题
	 */
	public final static int DELETE_MODULE_ITEM = 217;
	
	/**
	 * 调整模块主题排序
	 */
	public final static int UPDATE_MODULE_ITEM_DISPLAYORDER = 218;
	
	/**
	 * 添加系统角色
	 */
	public final static int ADD_SYS_ROLE = 219;
	
	/**
	 * 编辑系统角色
	 */
	public final static int EDIT_SYS_ROLE = 220;
	
	/**
	 * 删除系统角色
	 */
	public final static int DELETE_SYS_ROLE = 221;
	
	/**
	 * 添加用户角色
	 */
	public final static int ADD_SYS_USER_ROLE = 222;
	
	/**
	 * 编辑用户角色
	 */
	public final static int EDIT_SYS_USER_ROLE = 223;
	
	/**
	 * 删除用户角色
	 */
	public final static int DELETE_SYS_USER_ROLE = 224;
	
	/**
	 * 审核版主申请
	 */
	public final static int AUDIT_MODERATOR_APPLY = 225;
	
	/**
	 * 添加管理员
	 */
	public final static int ADD_ADMIN_USER = 226;
}