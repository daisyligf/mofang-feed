package com.mofang.feed.data.transfer;

import java.sql.Connection;
import java.sql.DriverManager;

public class MysqlConnectionProvider
{
	private final static String Driver = "com.mysql.jdbc.Driver";
	public static Connection ORIGINAL_CONN = null;
	
	public static Connection NEW_CONN = null;
	
	public static void init()
	{
		try
		{
			Class.forName(Driver);
			String url;
			url="jdbc:mysql://10.6.16.194:3306/feed?user=feedbbs&password=UcCCTzAe&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
			//url="jdbc:mysql://192.168.1.61:3306/feed_ol?user=root&password=mofang888&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
			ORIGINAL_CONN = DriverManager.getConnection(url);
			
			url="jdbc:mysql://10.6.16.194:3306/feed_v3?user=feedbbs&password=UcCCTzAe&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
			//url="jdbc:mysql://192.168.1.61:3306/feed_test?user=root&password=mofang888&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
			NEW_CONN = DriverManager.getConnection(url);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}