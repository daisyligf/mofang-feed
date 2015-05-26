package com.mofang.feed.record;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.model.FeedPost;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.model.StatForumViewHistory;
import com.mofang.feed.mysql.impl.StatForumViewHistoryDaoImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;

/***
 * 用户浏览 记录器
 * @author linjx
 *
 */
public class StatForumViewHistoryRecorder {

	private static class RecordTask implements Runnable{
		private long forumId;
		private long threadId;
//		private long postId;
		private long userId;
		
		public void setThreadId(long threadId) {
			this.threadId = threadId;
		}
		public void setForumId(long forumId) {
			this.forumId = forumId;
		}
		public void setUserId(long userId) {
			this.userId = userId;
		}
//		public void setPostId(long postId) {
//			this.postId = postId;
//		}
		
		@Override
		public void run() {
			try{
				if(forumId == 0 && threadId != 0){
					FeedThread threadInfo = FeedThreadServiceImpl.getInstance().getInfo(threadId, DataSource.REDIS);
					if(threadInfo == null){
						return;
					}
					forumId = threadInfo.getForumId();
				}
//				else if(forumId == 0 && postId != 0){
//					FeedPost postInfo = FeedPostServiceImpl.getInstance().getInfo(postId, DataSource.REDIS);
//					if(postInfo == null){
//						return;
//					}
//					forumId = postInfo.getForumId();
//				}
				
				if(forumId !=0 && userId != 0) {
					StatForumViewHistory viewHistory = new StatForumViewHistory();
					viewHistory.setForumId(forumId);
					viewHistory.setUserId(userId);
					viewHistory.setCreateTime(System.currentTimeMillis());
					StatForumViewHistoryDaoImpl.getInstance().add(viewHistory);
				}
			} catch (Exception e) {
				GlobalObject.ERROR_LOG.error("at StatForumViewHistoryRecorder.RecordTask.run throw an error.", e);
			}
		}
		
	}
	
	
	public static void recordInThreadLogic(long forumId, long userId){
		RecordTask task = new RecordTask();
		task.setForumId(forumId);
		task.setUserId(userId);
		GlobalObject.ASYN_DAO_EXECUTOR.execute(task);
	}
	
	public static void recordInPostLogic(long threadId, long userId){
		RecordTask task = new RecordTask();
		task.setThreadId(threadId);
		task.setUserId(userId);
		GlobalObject.ASYN_DAO_EXECUTOR.execute(task);
	}
	
//	public static void recordInCommentLogic(long postId, long userId){
//		RecordTask task = new RecordTask();
//		task.setPostId(postId);
//		task.setUserId(userId);
//		GlobalObject.ASYN_DAO_EXECUTOR.execute(task);
//	}
	
}
