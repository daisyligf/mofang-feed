package com.mofang.feed;

import com.mofang.feed.data.load.LoadManager;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.init.Initializer;
import com.mofang.feed.init.impl.MainInitializer;
import com.mofang.framework.web.server.action.ActionResolve;
import com.mofang.framework.web.server.action.impl.DefaultHttpActionResolve;
import com.mofang.framework.web.server.conf.ChannelConfig;
import com.mofang.framework.web.server.main.WebServer;
import com.mofang.framework.web.server.reactor.parse.PostDataParserType;

/**
 * 
 * @author zhaodx
 *
 */
public class Server
{
	public static void main(String[] args)
	{
		//String configpath = "/Users/milo/document/workspace/mofang.feed/src/main/resources/config.ini";
		
		if(args.length <= 0)
		{
			System.out.println("usage:java -server -Xms1024m -Xmx1024m -jar mofang-chat-task.jar configpath");
			System.exit(1);
		}
		String configpath = args[0];
		
		try
		{
			///服务器初始化
			System.out.println("prepare to initializing config......");
			Initializer initializer = new MainInitializer(configpath);
			initializer.init();
			System.out.println("initialize config completed!");
			
			///初始化redis数据
			if(GlobalConfig.LOAD_DATA)
				LoadManager.execute(GlobalConfig.LOAD_LIST);
			
			///启动服务器
			ActionResolve httpActionResolve = new DefaultHttpActionResolve();
			int port = GlobalConfig.SERVER_PORT;
			WebServer server = new WebServer(port, PostDataParserType.Json);
			
			///channel 配置
			ChannelConfig channelConfig = new ChannelConfig();
			channelConfig.setConnTimeout(GlobalConfig.CONN_TIMEOUT);
			channelConfig.setSoTimeout(GlobalConfig.READ_TIMEOUT);
			
			server.setChannelConfig(channelConfig);
			server.setScanPackagePath(GlobalConfig.SCAN_PACKAGE_PATH);
			server.setHttpActionResolve(httpActionResolve);
			try
			{
				System.out.println("Feed Server Start on " + GlobalConfig.SERVER_PORT);
				server.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			System.out.println("feed server start error. message:");
			e.printStackTrace();
		}
	}
}