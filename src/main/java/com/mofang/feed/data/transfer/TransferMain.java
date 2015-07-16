package com.mofang.feed.data.transfer;

public class TransferMain
{
	public static void main(String[] args) 
	{
		try
		{
			/*
			thread_id : 938054
			post_id : 6193654
			comment_id : 6193666
			 */
			
			MysqlConnectionProvider.init();
			//TransferManager.exec();
			TransferIncrementManager.exec();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}