package com.mofang.feed.mysql;

import java.util.List;

import com.mofang.feed.model.FeedSysRole;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;

/**
 * 
 * @author zhaodx
 *
 */
public interface FeedSysRoleDao
{
	public void add(FeedSysRole model) throws Exception;
	
	public void update(FeedSysRole model) throws Exception;
	
	public void delete(int roleId) throws Exception;
	
	public FeedSysRole getInfo(int roleId) throws Exception;
	
	public List<FeedSysRole> getList(Operand operand) throws Exception;
	
	public void updatePrivileges(int roleId, String privileges) throws Exception;
}