package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedSysPrivilege;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysPrivilegeDao
{
	public void add(FeedSysPrivilege model) throws Exception;
	
	public void update(FeedSysPrivilege model) throws Exception;
	
	public void delete(int privilegeId) throws Exception;
	
	public FeedSysPrivilege getInfo(long privilegeId) throws Exception;
	
	public List<FeedSysPrivilege> getList(Operand operand) throws Exception;
}