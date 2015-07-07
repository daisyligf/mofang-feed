package com.mofang.feed.init.impl;

import java.io.IOException;

import com.mofang.feed.init.AbstractInitializer;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.framework.util.IniParser;

/**
 * 
 * @author zhaodx
 *
 */
public class GlobalConfigInitializer extends AbstractInitializer
{
	private String configPath;
	
	public GlobalConfigInitializer(String configPath)
	{
		this.configPath = configPath;
	}
	
	@Override
	public void load() throws IOException 
	{
		IniParser config = new IniParser(configPath);
		GlobalConfig.SERVER_PORT = config.getInt("common", "server_port");
		GlobalConfig.CONN_TIMEOUT = config.getInt("common", "conn_timeout");
		GlobalConfig.READ_TIMEOUT = config.getInt("common", "read_timeout");
		GlobalConfig.SERVER_SECRET = config.get("common", "server_secret");
		GlobalConfig.SERVER_APPID = config.get("common", "server_appid");
		
		GlobalConfig.SCAN_PACKAGE_PATH = config.get("conf", "scan_package_path");
		GlobalConfig.MYSQL_CONFIG_PATH = config.get("conf", "mysql_config_path");
		GlobalConfig.REDIS_MASTER_CONFIG_PATH = config.get("conf", "redis_master_config_path");
		GlobalConfig.REDIS_SLAVE_CONFIG_PATH = config.get("conf", "redis_slave_config_path");
		GlobalConfig.LOG4J_CONFIG_PATH = config.get("conf", "log4j_config_path");
		GlobalConfig.HTTP_CLIENT_TASKSERVICE_CONFIG_PATH = config.get("conf", "http_client_taskservice_config_path");
		GlobalConfig.HTTP_CLIENT_CHATSERVICE_CONFIG_PATH = config.get("conf", "http_client_chatservice_config_path");
		GlobalConfig.HTTP_CLIENT_USERSERVICE_CONFIG_PATH = config.get("conf", "http_client_userservice_config_path");
		GlobalConfig.HTTP_CLIENT_SENSITIVEWORD_CONFIG_PATH = config.get("conf", "http_client_sensitiveword_config_path");
		GlobalConfig.HTTP_CLIENT_VIDEOSERVICE_CONFIG_PATH = config.get("conf", "http_client_videoservice_config_path");
		GlobalConfig.HTTP_CLIENT_GAMESERVICE_CONFIG_PATH = config.get("conf", "http_client_gameservice_config_path");
		GlobalConfig.HTTP_CLIENT_FAHAOSERVICE_CONFIG_PATH = config.get("conf", "http_client_fahaoservice_config_path");
		GlobalConfig.HTTP_CLIENT_VIPERSERVICE_CONFIG_PATH = config.get("conf", "http_client_viperservice_config_path");
		GlobalConfig.RETURN_MESSAGE_CONFIG_PATH = config.get("conf", "return_messsage_config_path");
		GlobalConfig.THREAD_REPLIES_REWARD_CONFIG_PATH = config.get("conf", "thread_replies_reward_config_path");
		
		GlobalConfig.CHAT_SERVICE_URL = config.get("api", "chat_service_url");
		GlobalConfig.TASK_EXEC_URL = config.get("api", "task_exec_url");
		GlobalConfig.USER_INFO_URL = config.get("api", "user_info_url");
		GlobalConfig.BATCH_USER_INFO_URL = config.get("api", "batch_user_info_url");
		GlobalConfig.SENSITIVE_WORD_URL = config.get("api", "sensitive_word_url");
		GlobalConfig.VIDEO_INFO_URL = config.get("api", "video_info_url");
		GlobalConfig.GAME_INFO_URL = config.get("api", "game_info_url");
		GlobalConfig.USER_FOLLOW_FORUM_URL = config.get("api", "user_follow_forum_url");
		GlobalConfig.USER_FOLLOW_FORUM_DAYS_URL = config.get("api", "user_follow_forum_days_url");
		GlobalConfig.RECOMMEND_GAME_URL = config.get("api", "recommend_game_url");
		GlobalConfig.FORUM_PARTITION_URL = config.get("api", "forum_partition_url");
		GlobalConfig.GIFT_LIST_URL = config.get("api", "gift_list_url");
		GlobalConfig.FORUM_FOLLOW_COUNT_BYTIME_URL = config.get("api", "forum_follow_count_bytime_url");
		GlobalConfig.FORUM_FOLLOW_COUNT_URL = config.get("api", "forum_follow_count_url");
		GlobalConfig.SYNC_GAME_FORUMID_URL = config.get("api", "sync_game_forumid_url");
		GlobalConfig.UPDATE_USER_STATUS_URL = config.get("api", "update_user_status_url");
		GlobalConfig.VIPER_URL = config.get("api", "viper_url");
		
		GlobalConfig.FEED_DETAIL_URL = config.get("link", "feed_detail_url");
		GlobalConfig.FORUM_DETAIL_URL = config.get("link", "forum_detail_url");
		GlobalConfig.AREA_DETAIL_URL = config.get("link", "area_detail_url");
		GlobalConfig.FEED_VIDEO_URL = config.get("link", "feed_video_url");
		GlobalConfig.IMAGE_PREFIX_URL = config.get("link", "image_prefix_url");
		GlobalConfig.GAME_DOWNLOAD_URL = config.get("link", "game_download_url");
		GlobalConfig.GIFT_INFO_URL = config.get("link", "gift_info_url");
		
		GlobalConfig.SOLR_SERVER_HOST = config.get("solr", "server_host");
		GlobalConfig.SOLR_CORE_FORUM = config.get("solr", "core_forum");
		GlobalConfig.SOLR_CORE_THREAD = config.get("solr", "core_thread");
		GlobalConfig.SOLR_CORE_POST = config.get("solr", "core_post");
		GlobalConfig.SOLR_CORE_COMMENT = config.get("solr", "core_comment");
		
		GlobalConfig.POST_INTERVAL_SECONDS = config.getInt("param", "post_interval_seconds");
		GlobalConfig.USER_EXPIRE_SECONDS = config.getInt("param", "user_expire_seconds");
		GlobalConfig.FORUM_TOP_THREADS_COUNT = config.getInt("param", "forum_top_threads_count");
		GlobalConfig.FORUM_MODERATOR_COUNT = config.getInt("param", "forum_moderator_count");
		
		GlobalConfig.MODERATOR_APPLY_FOLLOWFORUMDAYS = config.getInt("param", "moderator_apply_followforumdays");
		GlobalConfig.MODERATOR_APPLY_TOPELITECOUNT = config.getInt("param", "moderator_apply_topelitecount");
		GlobalConfig.MODERATOR_APPLY_NEWTHREADS = config.getInt("param", "moderator_apply_newthreads");
		GlobalConfig.MODERATOR_APPLY_TIMEINTERVAL = config.getInt("param", "moderator_apply_timeinterval");
		
		GlobalConfig.COLLECT_RECOMMEND_COUNT = config.getInt("param", "collect_recommend_count");
		GlobalConfig.SQUARE_BUTTONS_OLD = config.get("param", "square_buttons_old");
		GlobalConfig.SQUARE_BUTTONS_NEW = config.get("param", "square_buttons_new");
		GlobalConfig.SQUARE_ELITE_MODULE_ID = config.getInt("param", "square_elite_module_id");
		GlobalConfig.SQUARE_ELITE_ROLL_ID = config.getInt("param", "square_elite_roll_id");
		GlobalConfig.SQUARE_VIDEO_MODULE_ID = config.getInt("param", "square_video_module_id");
		GlobalConfig.SQUARE_VIDEO_ROLL_ID = config.getInt("param", "square_video_roll_id");
		GlobalConfig.SQUARE_IMAGE_MODULE_ID = config.getInt("param", "square_image_module_id");
		GlobalConfig.HOME_PREFECTURE_IDS = config.get("param", "home_prefecture_ids");
		GlobalConfig.SIGN_IN_DAY_EXP = config.getInt("param", "sign_in_day_exp");
		GlobalConfig.SIGN_IN_DAY_MAX_EXP = config.getInt("param", "sign_in_day_max_exp");
		
		GlobalConfig.LOAD_DATA = config.getBoolean("load", "load_data");
		GlobalConfig.LOAD_LIST =config.get("load", "load_list");
	}
}