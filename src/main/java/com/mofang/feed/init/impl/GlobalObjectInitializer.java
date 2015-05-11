package com.mofang.feed.init.impl;

import com.mofang.feed.init.AbstractInitializer;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;

/**
 * 
 * @author zhaodx
 *
 */
public class GlobalObjectInitializer extends AbstractInitializer
{
	@Override
	public void load() throws Exception
	{
		GlobalObject.initRedisMaster(GlobalConfig.REDIS_MASTER_CONFIG_PATH);
		GlobalObject.initRedisSlave(GlobalConfig.REDIS_SLAVE_CONFIG_PATH);
		GlobalObject.initMysql(GlobalConfig.MYSQL_CONFIG_PATH);
		GlobalObject.initTaskServiceHttpClient(GlobalConfig.HTTP_CLIENT_TASKSERVICE_CONFIG_PATH);
		GlobalObject.initChatServiceHttpClient(GlobalConfig.HTTP_CLIENT_CHATSERVICE_CONFIG_PATH);
		GlobalObject.initUserServiceHttpClient(GlobalConfig.HTTP_CLIENT_USERSERVICE_CONFIG_PATH);
		GlobalObject.initSensitiveWordHttpClient(GlobalConfig.HTTP_CLIENT_SENSITIVEWORD_CONFIG_PATH);
		GlobalObject.initVideoServiceHttpClient(GlobalConfig.HTTP_CLIENT_VIDEOSERVICE_CONFIG_PATH);
		GlobalObject.initGameServiceHttpClient(GlobalConfig.HTTP_CLIENT_GAMESERVICE_CONFIG_PATH);
		GlobalObject.initFahaoServiceHttpClient(GlobalConfig.HTTP_CLIENT_FAHAOSERVICE_CONFIG_PATH);
	}
}