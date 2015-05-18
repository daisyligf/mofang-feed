package com.mofang.feed.global;

/**
 * 
 * @author zhaodx
 *
 */
public class GlobalConfig
{
	public static int SERVER_PORT;
	
	public static int CONN_TIMEOUT;
	
	public static int READ_TIMEOUT;
	
	public static String SCAN_PACKAGE_PATH;
	
	public static String MYSQL_CONFIG_PATH;
	
	public static String REDIS_MASTER_CONFIG_PATH;
	
	public static String REDIS_SLAVE_CONFIG_PATH;
	
	public static String LOG4J_CONFIG_PATH;
	
	public static String HTTP_CLIENT_TASKSERVICE_CONFIG_PATH;
	
	public static String HTTP_CLIENT_CHATSERVICE_CONFIG_PATH;
	
	public static String HTTP_CLIENT_USERSERVICE_CONFIG_PATH;
	
	public static String HTTP_CLIENT_SENSITIVEWORD_CONFIG_PATH;
	
	public static String HTTP_CLIENT_VIDEOSERVICE_CONFIG_PATH;
	
	public static String HTTP_CLIENT_GAMESERVICE_CONFIG_PATH;
	
	public static String HTTP_CLIENT_FAHAOSERVICE_CONFIG_PATH;
	
	public static String CHAT_SERVICE_URL;
	
	public static String TASK_EXEC_URL;
	
	public static String USER_INFO_URL;
	
	public static String BATCH_USER_INFO_URL;
	
	public static String VIDEO_INFO_URL;
	
	public static String GAME_INFO_URL;
	
	public static String USER_FLLOW_FORUM_URL;
	
	public static String RECOMMEND_GAME_URL;
	
	public static String FORUM_PARTITION_URL;
	
	public static String GIFT_LIST_URL;
	
	public static String SENSITIVE_WORD_URL;
	
	public static String FEED_DETAIL_URL;
	
	public static String FORUM_DETAIL_URL;
	
	public static String AREA_DETAIL_URL;
	
	public static String FEED_VIDEO_URL;
	
	public static String IMAGE_PREFIX_URL;
	
	public static String GAME_DOWNLOAD_URL;
	
	public static String GIFT_INFO_URL;
	
	public static String SOLR_SERVER_HOST;
	
	public static String SOLR_CORE_FORUM;
	
	public static String SOLR_CORE_THREAD;
	
	public static String SOLR_CORE_POST;
	
	public static String SOLR_CORE_COMMENT;
	
	public static int POST_INTERVAL_SECONDS = 10;
	
	public static int USER_EXPIRE_SECONDS = 1800;
	
	public static int FORUM_TOP_THREADS_COUNT = 3;
	
	public static int FORUM_MODERATOR_COUNT = 9;
	
	public static int COLLECT_RECOMMEND_COUNT = 11;
	
	public static String SQUARE_BUTTONS_OLD;
	
	public static String SQUARE_BUTTONS_NEW;
	
	public static long SQUARE_ELITE_MODULE_ID;
	
	public static long SQUARE_ELITE_ROLL_ID;
	
	public static long SQUARE_VIDEO_MODULE_ID;
	
	public static long SQUARE_VIDEO_ROLL_ID;
	
	public static long SQUARE_IMAGE_MODULE_ID;
	
	public static String HOME_PREFECTURE_IDS;
	
	public static boolean LOAD_DATA = false;
	
	public static String LOAD_LIST;
}