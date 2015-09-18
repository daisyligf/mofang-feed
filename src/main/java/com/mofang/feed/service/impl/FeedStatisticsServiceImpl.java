package com.mofang.feed.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.external.ForumStatisticsInfo;
import com.mofang.feed.mysql.FeedStatisticsDao;
import com.mofang.feed.mysql.impl.FeedStatisticsDaoImpl;
import com.mofang.feed.service.FeedStatisticsService;

public class FeedStatisticsServiceImpl implements FeedStatisticsService {

	private static final FeedStatisticsServiceImpl SERVICE = new FeedStatisticsServiceImpl();
	
	private FeedStatisticsDao statisticsDao = FeedStatisticsDaoImpl.getInstance();
	
	private FeedStatisticsServiceImpl(){}
	
	public static FeedStatisticsServiceImpl getInstance() {
		return SERVICE;
	}
	
	@Override
	public Map<Long, ForumStatisticsInfo> forumStatisticsInfos(
			Set<Long> forumIdSet, long startTime, long endTime)
			throws Exception {
		 try {
			List<Object[]> forumNameList = statisticsDao.forumNameList(forumIdSet);
			if(forumNameList == null) throw new Exception("板块id列表可能在数据库不存在");
			int size = forumNameList.size();
			
			Map<Long, Integer> forumThreadCount = statisticsDao.forumThreadCount(forumIdSet, startTime, endTime);
			
			Map<Long, Integer> forumPostCount = statisticsDao.forumPostCount(forumIdSet, startTime, endTime);
			
			Map<Long, Integer> forumCommentCount = statisticsDao.forumCommentCount(forumIdSet, startTime, endTime);
			
			Map<Long, ForumStatisticsInfo> statisticsInfoMap = new LinkedHashMap<Long, ForumStatisticsInfo>(size);
			for(int idx = 0; idx < size; idx ++) {
				Object[] objArr = forumNameList.get(idx);
				ForumStatisticsInfo info = new ForumStatisticsInfo();
				info.forumId = (Long)objArr[0];
				info.name = (String)objArr[1];
				info.type = (Integer)objArr[2];
				
				//主题
				if(forumThreadCount == null || forumThreadCount.get(info.forumId) == null) info.threadCount = 0;
				else info.threadCount = forumThreadCount.get(info.forumId);
				
				//楼层
				if(forumPostCount == null || forumPostCount.get(info.forumId) == null) info.postCount = 0;
				else info.postCount = forumPostCount.get(info.forumId);
				
				//评论
				if(forumCommentCount == null || forumCommentCount.get(info.forumId) == null) info.commentCount = 0;
				else info.commentCount = forumCommentCount.get(info.forumId);
				
				statisticsInfoMap.put(info.forumId, info);
			}
			return statisticsInfoMap;
		 }catch(Exception e){
				GlobalObject.ERROR_LOG.error("at FeedStatisticsServiceImpl.forumStatisticsInfos throw an error.", e);
				throw e;
		}
	}

}
