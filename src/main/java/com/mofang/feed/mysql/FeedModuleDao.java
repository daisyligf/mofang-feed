package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedModule;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedModuleDao
{
	public void add(FeedModule model) throws Exception;
	
	public void update(FeedModule model) throws Exception;
	
	public void delete(long moduleId) throws Exception;
	
	public FeedModule getInfo(long moduleId) throws Exception;
	
	public List<FeedModule> getList(Operand operand) throws Exception;
	
	public void incrThreads(long moduleId, int threads) throws Exception;
}