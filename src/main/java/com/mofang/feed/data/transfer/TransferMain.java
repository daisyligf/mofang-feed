package com.mofang.feed.data.transfer;

public class TransferMain
{
	public static void main(String[] args) 
	{
		try
		{
			//MysqlPoolProvider.initMysql();
			MysqlConnectionProvider.init();
			TransferManager.exec();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}