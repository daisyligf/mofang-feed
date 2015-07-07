package com.mofang.feed.init.impl;

import org.apache.log4j.PropertyConfigurator;

import com.mofang.feed.init.AbstractInitializer;
import com.mofang.feed.init.Initializer;
import com.mofang.feed.global.GlobalConfig;

/**
 * 
 * @author zhaodx
 *
 */
public class MainInitializer extends AbstractInitializer
{
	private String configPath;
	
	public MainInitializer(String configPath)
	{
		this.configPath = configPath;
	}
	
	@Override
	public void load() throws Exception
	{
		Initializer globalConf = new GlobalConfigInitializer(configPath);
		globalConf.init();
		
		Initializer returnMsg = new ReturnMessageInitializer();
		returnMsg.init();
		
		Initializer threadRepliesRewardConf  = new ThreadRepliesRewardConfigInitializer();
		threadRepliesRewardConf.init();
		
		PropertyConfigurator.configure(GlobalConfig.LOG4J_CONFIG_PATH);
		
		Initializer globalObject = new GlobalObjectInitializer();
		globalObject.init();
	}
}
