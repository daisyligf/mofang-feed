package com.mofang.feed.init.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.init.AbstractInitializer;

/**
 * 
 * @author zhaodx
 *
 */
public class ReturnMessageInitializer extends AbstractInitializer
{
	@Override
	public void load() throws Exception
	{
		Properties configurations = loadConfig(GlobalConfig.RETURN_MESSAGE_CONFIG_PATH);
		ReturnMessage.SUCCESS = configurations.getProperty("SUCCESS");
		ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID = configurations.getProperty("CLIENT_REQUEST_DATA_IS_INVALID");
		ReturnMessage.CLIENT_REQUEST_PARAMETER_FORMAT_ERROR = configurations.getProperty("CLIENT_REQUEST_PARAMETER_FORMAT_ERROR");
		ReturnMessage.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER = configurations.getProperty("CLIENT_REQUEST_LOST_NECESSARY_PARAMETER");
		ReturnMessage.INVALID_OPERATION = configurations.getProperty("INVALID_OPERATION");
		ReturnMessage.SERVER_ERROR = configurations.getProperty("SERVER_ERROR");
		ReturnMessage.FORUM_NOT_EXISTS = configurations.getProperty("FORUM_NOT_EXISTS");
		ReturnMessage.THREAD_NOT_EXISTS = configurations.getProperty("THREAD_NOT_EXISTS");
		ReturnMessage.POST_NOT_EXISTS = configurations.getProperty("POST_NOT_EXISTS");
		ReturnMessage.COMMENT_NOT_EXISTS = configurations.getProperty("COMMENT_NOT_EXISTS");
		ReturnMessage.INSUFFICIENT_PERMISSIONS = configurations.getProperty("INSUFFICIENT_PERMISSIONS");
		ReturnMessage.ADD_FREQUENCY_FAST = configurations.getProperty("ADD_FREQUENCY_FAST");
		ReturnMessage.THREAD_HAS_CLOSED = configurations.getProperty("THREAD_HAS_CLOSED");
		ReturnMessage.FAVORITE_HAS_EXISTS = configurations.getProperty("FAVORITE_HAS_EXISTS");
		ReturnMessage.SYS_ROLE_NOT_EXISTS = configurations.getProperty("SYS_ROLE_NOT_EXISTS");
		ReturnMessage.USER_ROLE_NOT_EXISTS = configurations.getProperty("USER_ROLE_NOT_EXISTS");
		ReturnMessage.USER_ROLE_EXISTS = configurations.getProperty("USER_ROLE_EXISTS");
		ReturnMessage.USER_FOLLOWED_FORUM = configurations.getProperty("USER_FOLLOWED_FORUM");
		ReturnMessage.USER_UNFOLLOW_FORUM = configurations.getProperty("USER_UNFOLLOW_FORUM");
		ReturnMessage.MODERATOR_APPLY_NOT_EXISTS = configurations.getProperty("MODERATOR_APPLY_NOT_EXISTS");
		ReturnMessage.FORUM_MODERATOR_IS_FULL = configurations.getProperty("FORUM_MODERATOR_IS_FULL");
		ReturnMessage.MODERATOR_APPLY_CONDITION_INSUFFICIENT = configurations.getProperty("MODERATOR_APPLY_CONDITION_INSUFFICIENT");
		ReturnMessage.ADMIN_USER_EXISTS = configurations.getProperty("ADMIN_USER_EXISTS");
		ReturnMessage.ADMIN_USER_NOT_EXISTS = configurations.getProperty("ADMIN_USER_NOT_EXISTS");
		ReturnMessage.USER_HAS_PROHIBITED = configurations.getProperty("USER_HAS_PROHIBITED");
		ReturnMessage.USER_NOT_EXISTS = configurations.getProperty("USER_NOT_EXISTS");
	}
	
	private static Properties loadConfig(String configPath) throws Exception
	{
		Properties configurations = new Properties();
        File file = new File(configPath);
        try
        {
	        	configurations.load(new FileInputStream(file));
	        	return configurations;
        }
        catch(Exception e)
        {
        		throw e;
        }
	}
}