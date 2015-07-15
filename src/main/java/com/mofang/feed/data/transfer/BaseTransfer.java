package com.mofang.feed.data.transfer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 
 * @author zhaodx
 *
 */
public abstract class BaseTransfer
{
	public ResultSet getData(String strSql)
	{
		try
		{
			//Class.forName(Driver);
			Connection conn = MysqlConnectionProvider.ORIGINAL_CONN;
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
			//Class.forName(Driver);
			Connection conn = MysqlConnectionProvider.NEW_CONN;
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			pstmt.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ResultSet query(String strSql)
	{
		try
		{
			//Class.forName(Driver);
			Connection conn = MysqlConnectionProvider.NEW_CONN;
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			return pstmt.executeQuery();
		}
		catch(Exception e)
		{
			System.out.println(strSql);
			e.printStackTrace();
			return null;
		}
	}
}