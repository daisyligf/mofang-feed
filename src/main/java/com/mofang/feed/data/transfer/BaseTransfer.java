package com.mofang.feed.data.transfer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 
 * @author zhaodx
 *
 */
public abstract class BaseTransfer
{
	private final static String Driver = "com.mysql.jdbc.Driver";
	
	public ResultSet getData(String strSql)
	{
		try
		{
			Class.forName(Driver);
			String url="jdbc:mysql://192.168.1.61:3306/feed_ol?user=root&password=mofang888&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
			Connection conn = DriverManager.getConnection(url);
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			ResultSet rs = pstmt.executeQuery();
			return rs;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void execute(String strSql)
	{
		try
		{
			Class.forName(Driver);
			//String url="jdbc:mysql://192.168.1.61:3306/feed?user=root&password=mofang888&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
			String url="jdbc:mysql://127.0.0.1:3306/feed?user=root&password=root&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
			Connection conn = DriverManager.getConnection(url);
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			pstmt.execute();
		}
		catch(Exception e)
		{
			System.out.println(strSql);
			e.printStackTrace();
		}
	}
}