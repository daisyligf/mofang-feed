package com.mofang.feed.mysql.impl;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedSysPrivilege;
import com.mofang.feed.mysql.FeedSysPrivilegeDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysPrivilegeDaoImpl extends AbstractMysqlSupport<FeedSysPrivilege> implements FeedSysPrivilegeDao
{
	private final static FeedSysPrivilegeDaoImpl DAO = new FeedSysPrivilegeDaoImpl();
	
	private FeedSysPrivilegeDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedSysPrivilegeDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public void add(FeedSysPrivilege model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedSysPrivilege model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(int privilegeId) throws Exception
	{
		super.deleteByPrimaryKey(privilegeId);
	}

	@Override
	public FeedSysPrivilege getInfo(long privilegeId) throws Exception
	{
		return super.getByPrimaryKey(privilegeId);
	}
}