package com.mofang.feed.data.transfer;

import com.mofang.feed.data.transfer.handler.FeedBlackListTransfer;
import com.mofang.feed.data.transfer.handler.FeedCommentTransfer;
import com.mofang.feed.data.transfer.handler.FeedForumTransfer;
import com.mofang.feed.data.transfer.handler.FeedModeratorApplyTransfer;
import com.mofang.feed.data.transfer.handler.FeedModuleItemTransfer;
import com.mofang.feed.data.transfer.handler.FeedModuleTransfer;
import com.mofang.feed.data.transfer.handler.FeedOperateHistoryTransfer;
import com.mofang.feed.data.transfer.handler.FeedPostRecommendTransfer;
import com.mofang.feed.data.transfer.handler.FeedPostTransfer;
import com.mofang.feed.data.transfer.handler.FeedRoleChangeHistoryTransfer;
import com.mofang.feed.data.transfer.handler.FeedSysRoleTransfer;
import com.mofang.feed.data.transfer.handler.FeedSysUserRoleTransfer;
import com.mofang.feed.data.transfer.handler.FeedThreadRecommendTransfer;
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
		transfer = new FeedModuleTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("module data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");

		itemStart = System.currentTimeMillis();
		transfer = new FeedModuleItemTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("module item data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");

		itemStart = System.currentTimeMillis();
		transfer = new FeedSysRoleTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("role data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");

		itemStart = System.currentTimeMillis();
		transfer = new FeedSysUserRoleTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("user role data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");

		itemStart = System.currentTimeMillis();
		transfer = new FeedModeratorApplyTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("moderator apply data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedOperateHistoryTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("operate history data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");

		itemStart = System.currentTimeMillis();
		transfer = new FeedRoleChangeHistoryTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("role change history data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		itemStart = System.currentTimeMillis();
		transfer = new FeedBlackListTransfer();
		transfer.exec();
		itemEnd = System.currentTimeMillis();
		System.out.println("black list data transfer completed. cost time: " + (itemEnd - itemStart) + " ms.");
		
		long end = System.currentTimeMillis();
		System.out.println("data transfer completed. cost time: " + (end - start) + " ms.");
	}
}