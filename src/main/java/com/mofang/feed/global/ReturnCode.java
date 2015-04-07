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
}