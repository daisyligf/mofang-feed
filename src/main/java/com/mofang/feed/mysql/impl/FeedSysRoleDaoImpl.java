package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.mysql.FeedSysRoleDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.meta.ResultData;
import com.mofang.framework.data.mysql.core.meta.RowData;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysRoleDaoImpl extends AbstractMysqlSupport<FeedSysRole> implements FeedSysRoleDao
{
	private final static FeedSysRoleDaoImpl DAO = new FeedSysRoleDaoImpl();
	
	private FeedSysRoleDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedSysRoleDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public int getMaxId() throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("select max(role_id) from feed_sys_role ");
		ResultData result = super.executeQuery(strSql.toString());
		if(null == result)
			return 0;
		
		List<RowData> rows = result.getQueryResult();
		if(null == rows || rows.size() == 0)
			return 0;
		
		return rows.get(0).getInteger(0);
	}

	@Override
	public void add(FeedSysRole model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedSysRole model) throws Exception
	{
		super.updateByPrimaryKey(model);
	}

	@Override
	public void delete(int roleId) throws Exception
	{
		super.deleteByPrimaryKey(roleId);
	}

	@Override
	public FeedSysRole getInfo(int roleId) throws Exception
	{
		return super.getByPrimaryKey(roleId);
	}

	@Override
	public void updatePrivileges(int roleId, String privileges) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_sys_role set privileges = " + privileges + " where role_id=" + roleId);
		super.execute(strSql.toString());
	}
}