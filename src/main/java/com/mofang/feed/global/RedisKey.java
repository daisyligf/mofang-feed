package com.mofang.feed.global;

/**
 * 
 * @author zhaodx
 *
 */
public class RedisKey
{
	/**
	 * 版块自增ID
	 * String结构：incr forum_increment_id
	 */
	public final static String FORUM_INCREMENT_ID_KEY = "forum_increment_id";
	
	/**
	 * 主题自增ID
	 * String结构：incr thread_increment_id
	 */
	public final static String THREAD_INCREMENT_ID_KEY = "thread_increment_id";
	
	/**
	 * 楼层自增ID
	 * String结构：incr post_increment_id 
	 */
	public final static String POST_INCREMENT_ID_KEY = "post_increment_id";
	
	/**
	 * 评论自增ID
	 * String结构：incr comment_increment_id
	 */
	public final static String COMMENT_INCREMENT_ID_KEY = "comment_increment_id";
	
	/**
	 * 角色自增ID
	 */
	public final static String ROLE_INCREMENT_ID_KEY = "role_increment_id";
	
	/**
	 * 板块标签自增ID
	 */
	public final static String TAG_INCREMENT_ID_KEY = "tag_increment_id";
	
	/**
	 * 标签名字key前缀
	 * 结构: String
	 * 示例: set forum_tag_{tag_id} name
	 */
	public final static String TAG_NAME_KEY_PREFIX = "tag_";
	
	/**
	 * 楼层数key前缀
	 * 结构: String
	 * 示例: incr post_position_${thread_id}
	 */
	public final static String POST_POSITION_KEY_PREFIX = "post_position_";
	
	/**
	 * 版块信息key前缀
	 * 结构: Hash
	 * 示例: hset forum_info_${forum_id} name dota
	 */
	public final static String FORUM_INFO_KEY_PREFIX = "forum_info_";
	
	/**
	 * 主题信息key前缀
	 * 结构: Hash
	 * 示例: hset thread_info_${thread_id} subject test
	 */
	public final static String THREAD_INFO_KEY_PREFIX = "thread_info_";
	
	/**
	 * 版块主题列表key前缀
	 * 结构: SortedSet
	 * 说明:
	 *          score: 置顶位置 + 主题最后回复时间
	 *          value: 主题ID
	 * 示例: zadd forum_thread_list_${forum_id} score ${thread_id}
	 */
	public final static String FORUM_THREAD_LIST_KEY_PREFIX = "forum_thread_list_";
	
	/**
	 * 版块置顶主题列表key前缀
	 * 结构: SortedSet
	 * 说明:
	 *          score: 主题置顶时间
	 *          value: 主题ID
	 * 示例: zadd forum_top_thread_list_${forum_id} score ${thread_id}
	 */
	public final static String FORUM_TOP_THREAD_LIST_KEY_PREFIX = "forum_top_thread_list_";
	
	/**
	 * 楼层信息key前缀
	 * 结构: Hash
	 * 示例: hset post_info_${post_id} message test
	 */
	public final static String POST_INFO_KEY_PREFIX = "post_info_";
	
	/**
	 * 主题楼层列表key前缀
	 * 结构: SortedSet
	 * 说明:
	 *          score: 楼层position 
	 *          value: 楼层ID
	 * 示例: zadd thread_post_list_${thread_id} score ${post_id}
	 */
	public final static String THREAD_POST_LIST_KEY_PREFIX = "thread_post_list_";
	
	/**
	 * 楼主的楼层列表key前缀(只看楼主)
	 * 结构: SortedSet
	 * 说明:
	 *          score: 楼层position 
	 *          value: 楼层ID
	 * 示例: zadd host_post_list_${thread_id} score ${post_id}
	 */
	public final static String HOST_POST_LIST_KEY_PREFIX = "host_post_list_";
	
	/**
	 * 评论信息key前缀
	 * 结构: Hash
	 * 示例: hset comment_info_${comment_id} message test
	 */
	public final static String COMMENT_INFO_KEY_PREFIX = "comment_info_";
	
	/**
	 * 楼层评论列表key前缀
	 * 结构: SortedSet
	 * 说明:
	 *          score: 评论发表时间
	 *          value: 评论ID
	 * 示例: zadd post_comment_list_${post_id} score ${comment_id}
	 */
	public final static String POST_COMMENT_LIST_KEY_PREFIX = "post_comment_list_";
	
	/**
	 * 用户点赞主题列表key前缀
	 * 结构: Set
	 * 示例: sadd user_recommend_thread_list_${user_id} ${thread_id}
	 */
	public final static String USER_RECOMMEND_THREAD_LIST_KEY_PREFIX = "user_recommend_thread_list_";
	
	/**
	 * 用户点赞楼层列表key前缀
	 * 结构: Set
	 * 示例: sadd user_recommend_post_list_${user_id} ${post_id}
	 */
	public final static String USER_RECOMMEND_POST_LIST_KEY_PREFIX = "user_recommend_post_list_";
	
	/**
	 * 用户最后回帖时间key前缀(用于发帖间隔控制)
	 * 结构: String
	 * 说明:
	 *          value: 最后回帖时间戳	
	 * 示例: set user_last_post_time_${user_id} 142358672211
	 */
	public final static String USER_LAST_POST_TIME_KEY_PREFIX = "user_last_post_time_";
	
	/**
	 * 角色信息key前缀
	 * 结构: Hash
	 * 示例: hset role_info_${role_id} name test
	 */
	public final static String ROLE_INFO_KEY_PREFIX = "role_info_";
	
	/**
	 * 版块角色列表key前缀
	 * 结构: Hash
	 * 示例: hset forum_role_list_${forum_id} ${user_id} ${role_id}
	 */
	public final static String FORUM_ROLE_LIST_KEY_PREFIX = "forum_role_list_";
	
	/**
	 * 版块黑名单用户列表
	 * 结构: Set
	 * 示例: sadd forum_black_list_${forum_id} ${user_id}
	 */
	public final static String FORUM_BLACK_LIST_KEY_PREFIX = "forum_black_list_";

	/**
	 * 用户信息key前缀(缓存)
	 * String结构：set cache_user_info_${userid} ${user_info}
	 */
	public final static String CACHE_USER_KEY_PREFIX = "cache_user_info_";
	
	/***
	 * 新游推荐 列表
	 * 结构: SortedSet
	 * 说明:
	 *          score: 板块创建时间
	 *          value: 板块ID
	 * 示例: zadd recommend_game_list_${ABCDE} score ${forum_id}
	 */
	public final static String RECOMMEND_GAME_LIST_KEY_PREFIX = "recommend_game_list_";
	
	/***
	 * 热门游戏 列表
	 * 说明:
	 *          score: 板块创建时间
	 *          value: 板块ID
	 * 示例: zadd hot_forum_list_${ABCDE} score ${forum_id}}
	 */
	public final static String HOT_FORUM_LIST_KEY_PREFIX = "hot_forum_list_";
	
	/***
	 * 板块 重复覆盖内容的 结构
	 * 结构: Hash
	 * 示例: hset forum_extend_${forumId} ${download_url} ${xx}
	 *                                                           gift_url
	 *                                                           prefecture_url
	 */
	public final static String FORUM_EXTEND_KEY_PREFIX = "forum_extend_";
	
	/***
	 * 首页默认 搜索关键词
	 * 结构: String
	 * 示例：set home_key_word xxx
	 */
	public final static String HOME_DEFAULT_KEY_WORD_KEY = "home_key_word";
	
	/***
	 * 板块下回复最高 7条帖子
	 * 结构: Set
	 * 示例: sadd thread_reply_highest_list_${forum_id}  ${thread_id}
	 */
	public final static String REPLYHIGHEST_THREAD_KEY_PREFIX = "forum_replyhighest_thread_list_";
	
	/***
	 * 管理员列表
	 * 结构: Set
	 * 示例: sadd admin_user_list ${user_id}
	 */
	public final static String ADMIN_USER_LIST_KEY = "admin_user_list";
	
	/**
	 * 签到
	 * 结构: Hash
	 * 示例: hset user_sign_in_${user_id} name test
	 * 字段名： last_sign_in_time  上次签到时间
	 * 				 days                     累计签到天数
	 */
	public final static String  USER_SIGN_IN_KEY_PREFIX = "user_sign_in_";
	
	/**
	 * 已签到列表
	 * 结构: ZSet
	 * 示例: zadd  sigin_in_member_list score userId
	 */
	public final static String SIGN_IN_MEMBER_LIST_KEY = "sign_in_member_list";
	
	/**
	 * 构建Redis Key
	 * @param prefix  前缀
	 * @param parameter 参数
	 * @return
	 */
	public static String buildRedisKey(String prefix, Object parameter)
	{
		return prefix.concat(parameter.toString());
	}
}