package com.mofang.feed.mysql;

import java.util.Date;
import java.util.List;

import com.mofang.feed.model.FeedModuleItem;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModuleItemDao
{
	public void add(FeedModuleItem model) throws Exception;
	
	public void update(FeedModuleItem model) throws Exception;
	
	public void delete(long itemId) throws Exception;
	
	public void deleteByThreadId(long threadId) throws Exception;
	
	public FeedModuleItem getInfo(long itemId) throws Exception;
	
	public List<FeedModuleItem> getList(Operand operand) throws Exception;
	
	public void updateDisplayOrder(long itemId, int displayOrder) throws Exception;
	
	public void updateStauts(long itemId, int status) throws Exception;
	
	public void updateOnlineTime(long itemId, Date onlineTime) throws Exception;
	
	public List<FeedModuleItem> getItemList(long moduleId, int start, int end) throws Exception;
	
	public long getItemCount(long moduleId) throws Exception;
}