package com.mofang.feed.global;

/**
 * 
 * @author zhaodx
 *
 */
public class ReturnCode
{
	/**
	 * 操作成功
	 */
	public final static int SUCCESS = 0;
	
	/**
	 * 无效参数
	 */
	public final static int CLIENT_REQUEST_DATA_IS_INVALID = 400;
	
	/**
	 * 请求参数格式不正确
	 */
	public final static int CLIENT_REQUEST_PARAMETER_FORMAT_ERROR = 401;
	
	/**
	 * 请求缺少必要参数
	 */
	public final static int CLIENT_REQUEST_LOST_NECESSARY_PARAMETER = 402;
	
	/**
	 * 无效操作
	 */
	public final static int INVALID_OPERATION = 403;
	
	/**
	 * 服务器错误
	 */
	public final static int SERVER_ERROR = 500;
	
	/**
	 * 版块不存在
	 */
	public final static int FORUM_NOT_EXISTS = 601;
	
	/**
	 * 主题不存在
	 */
	public final static int THREAD_NOT_EXISTS = 602;
	
	/**
	 * 楼层不存在
	 */
	public final static int POST_NOT_EXISTS = 603;
	
	/**
	 * 评论不存在
	 */
	public final static int COMMENT_NOT_EXISTS = 604;
	
	/**
	 * 权限不足
	 */
	public final static int INSUFFICIENT_PERMISSIONS = 701;
	
	/**
	 * 发帖频率太快
	 */
	public final static int ADD_FREQUENCY_FAST = 702;
	
	/**
	 * 主题已关闭
	 */
	public final static int THREAD_HAS_CLOSED = 703;
	
	/**
	 * 收藏已存在
	 */
	public final static int FAVORITE_HAS_EXISTS = 704;
	
	/**
	 * 模块不存在
	 */
	public final static int MODULE_NOT_EXISTS = 705;
	
	/**
	 * 模块主题不存在
	 */
	public final static int MODULE_ITEM_NOT_EXISTS = 706;
	
	/**
	 * 系统角色不存在
	 */
	public final static int SYS_ROLE_NOT_EXISTS = 707;
	
	/**
	 * 用户角色不存在
	 */
	public final static int USER_ROLE_NOT_EXISTS = 708;
	
	/**
	 * 用户角色已存在
	 */
	public final static int USER_ROLE_EXISTS = 709;
	
	/**
	 * 用户已关注指定版块
	 */
	public final static int USER_FOLLOWED_FORUM = 710;
	
	/**
	 * 用户未关注指定版块
	 */
	public final static int USER_UNFOLLOW_FORUM = 711;
	
	/**
	 * 版主申请记录不存在
	 */
	public final static int MODERATOR_APPLY_NOT_EXISTS = 712;
	
	/**
	 * 版块版主已经满额
	 */
	public final static int FORUM_MODERATOR_IS_FULL = 713;
	
	/**
	 * 版主申请条件不足
	 */
	public final static int MODERATOR_APPLY_CONDITION_INSUFFICIENT = 714;
	
	/**
	 * 管理员已存在
	 */
	public final static int ADMIN_USER_EXISTS = 715;
	
	/**
	 * 管理员不存在
	 */
	public final static int ADMIN_USER_NOT_EXISTS = 716;
	
	/**
	 * 用户已被禁言
	 */
	public final static int USER_HAS_PROHIBITED = 717;
}