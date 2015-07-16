package com.mofang.feed.data.transfer;

import com.mofang.feed.data.transfer.increment.FeedCommentTransfer;
import com.mofang.feed.data.transfer.increment.FeedPostConflictTransfer;
import com.mofang.feed.data.transfer.increment.FeedPostNonConflictTransfer;
import com.mofang.feed.data.transfer.increment.FeedThreadTransfer;
import com.mofang.feed.data.transfer.increment.FeedUserFavoriteTransfer;

public class TransferIncrementManager
{
	public static void exec()
	{
		FeedTransfer transfer = null;
		long start = System.currentTimeMillis();
		long itemStart = 0L;
		long itemEnd = 0L;
		/*
		itemStart = System.currentTimeMillis();
		transfer = new FeedThreadTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("thread data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		*/
		itemStart = System.currentTimeMillis();
		transfer = new FeedPostConflictTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("post conflict data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedPostNonConflictTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("post nonconflict data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		/*
		itemStart = System.currentTimeMillis();
		transfer = new FeedCommentTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("comment data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedUserFavoriteTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("thread recommend data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		*/
		long end = System.currentTimeMillis();
		System.out.println("data transfer completed. cost time: " + (end - start) + " ms.");
	}
}