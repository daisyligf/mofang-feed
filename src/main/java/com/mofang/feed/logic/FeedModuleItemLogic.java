package com.mofang.feed.logic;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.model.FeedModuleItem;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModuleItemLogic
{
	public ResultValue add(FeedModuleItem model, long operatorId) throws Exception;
	
	public ResultValue edit(FeedModuleItem model, long operatorId) throws Exception;
	
	public ResultValue delete(long itemId, long operatorId) throws Exception;
	
	public ResultValue updateDisplayOrder(long itemId, int displayOrder, long operatorId) throws Exception;
	
	public ResultValue getInfo(long itemId) throws Exception;
	
	public ResultValue getItemList(long moduleId, int pageNum, int pageSize) throws Exception;
	
	public ResultValue getSquareElite(int pageNum, int pageSize, String version) throws Exception;
	
	public ResultValue getSquareImage(int pageNum, int pageSize, String version) throws Exception;
	
	public ResultValue getSquareVideo(int pageNum, int pageSize, String version) throws Exception;
}