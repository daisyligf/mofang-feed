package com.mofang.feed.data.transfer;

import com.mofang.feed.data.transfer.handler.FeedBlackListTransfer;
import com.mofang.feed.data.transfer.handler.FeedCommentTransfer;
import com.mofang.feed.data.transfer.handler.FeedForumThreadsUpdateTransfer;
import com.mofang.feed.data.transfer.handler.FeedForumTransfer;
import com.mofang.feed.data.transfer.handler.FeedPostCommentsUpdateTransfer;
import com.mofang.feed.data.transfer.handler.FeedPostRecommendTransfer;
import com.mofang.feed.data.transfer.handler.FeedPostTransfer;
import com.mofang.feed.data.transfer.handler.FeedThreadRecommendTransfer;
import com.mofang.feed.data.transfer.handler.FeedThreadReplyUpdateTransfer;
import com.mofang.feed.data.transfer.handler.FeedThreadTransfer;
import com.mofang.feed.data.transfer.handler.FeedUserFavoriteTransfer;

/**
 * 
 * @author zhaodx
 *
 */
public class TransferManager
{
	public static void exec()
	{
		FeedTransfer transfer = null;
		long start = System.currentTimeMillis();
		long itemStart = 0L;
		long itemEnd = 0L;
		/*
		itemStart = System.currentTimeMillis();
		transfer = new FeedForumTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("forum data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedThreadTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("thread data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedPostTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("post data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedCommentTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("comment data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		*/
		itemStart = System.currentTimeMillis();
		transfer = new FeedThreadRecommendTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("thread recommend data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedPostRecommendTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("psot recommend data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedUserFavoriteTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("user favorite data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedBlackListTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("black list data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedForumThreadsUpdateTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("forum threads update completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedThreadReplyUpdateTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("thread replies update completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedPostCommentsUpdateTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("post comments update completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		long end = System.currentTimeMillis();
		System.out.println("data transfer completed. cost time: " + (end - start) + " ms.");
	}
}