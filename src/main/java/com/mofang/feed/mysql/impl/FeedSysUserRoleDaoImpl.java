package com.mofang.feed.mysql.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.mysql.FeedSysUserRoleDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.GreaterThanOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.LimitOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByEntry;
import com.mofang.framework.data.mysql.core.criterion.operand.OrderByOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;
import com.mofang.framework.data.mysql.core.criterion.type.SortType;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysUserRoleDaoImpl extends AbstractMysqlSupport<FeedSysUserRole> implements FeedSysUserRoleDao
{
	private final static FeedSysUserRoleDaoImpl DAO = new FeedSysUserRoleDaoImpl();
	
	private FeedSysUserRoleDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch(Exception e)
		{}
	}
	
	public static FeedSysUserRoleDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public boolean exists(long forumId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(userEqual);
		List<FeedSysUserRole> list = super.getList(where);
		return (null != list && list.size() > 0);
	}

	@Override
	public void add(FeedSysUserRole model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void update(FeedSysUserRole model) throws Exception
	{
		StringBuilder strSql = new StringBuilder();
		strSql.append("update feed_sys_user_role set role_id = " + model.getRoleId() + " where user_id=" + model.getUserId() + " and forum_id=" + model.getForumId());
		super.execute(strSql.toString());
	}

	@Override
	public void delete(long forumId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(userEqual);
		super.deleteByWhere(where);
	}

	@Override
	public void deleteByRoleId(int roleId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand roleEqual = new EqualOperand("role_id", roleId);
		where.append(roleEqual);
		super.deleteByWhere(where);
	}

	@Override
	public FeedSysUserRole getInfo(long forumId, long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand userEqual = new EqualOperand("user_id", userId);
		Operand and = new AndOperand();
		Operand limit = new LimitOperand(0L, 1L);
		where.append(forumEqual).append(and).append(userEqual).append(limit);
		List<FeedSysUserRole> list = super.getList(where);
		if(null == list || list.size() == 0)
			return null;
		return list.get(0);
	}

	@Override
	public List<FeedSysUserRole> getListByRoleId(int roleId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand roleEqual = new EqualOperand("role_id", roleId);
		where.append(roleEqual);
		return super.getList(where);
	}

	@Override
	public long getCountByForumId(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		where.append(forumEqual);
		return super.getCount(where);
	}

	@Override
	public List<FeedSysUserRole> getListByForumId(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		where.append(forumEqual);
		return super.getList(where);
	}

	@Override
	public List<FeedSysUserRole> getUserList(int start, int end) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumGreat = new GreaterThanOperand("forum_id", 0L);
		OrderByEntry entry = new OrderByEntry("create_time", SortType.Desc);
		Operand orderby = new OrderByOperand(entry);
		Operand limit = new LimitOperand(Integer.valueOf(start).longValue(), Integer.valueOf(end).longValue());
		where.append(forumGreat).append(orderby).append(limit);
		return super.getList(where);
	}

	@Override
	public long getUserCount() throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumGreat = new GreaterThanOperand("forum_id", 0L);
		where.append(forumGreat);
		return super.getCount(where);
	}

	@Override
	public List<FeedSysUserRole> getForumListByUserId(long userId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand userEqual = new EqualOperand("user_id", userId);
		where.append(userEqual);
		return super.getList(where);
	}
}