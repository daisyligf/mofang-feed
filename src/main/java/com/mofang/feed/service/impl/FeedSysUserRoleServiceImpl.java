package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.model.Page;
import com.mofang.feed.mysql.FeedSysUserRoleDao;
import com.mofang.feed.mysql.impl.FeedSysUserRoleDaoImpl;
import com.mofang.feed.redis.FeedAdminUserRedis;
import com.mofang.feed.redis.FeedSysRoleRedis;
import com.mofang.feed.redis.FeedSysUserRoleRedis;
import com.mofang.feed.redis.impl.FeedAdminUserRedisImpl;
import com.mofang.feed.redis.impl.FeedSysRoleRedisImpl;
import com.mofang.feed.redis.impl.FeedSysUserRoleRedisImpl;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.util.MysqlPageNumber;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysUserRoleServiceImpl implements FeedSysUserRoleService
{
	private final static FeedSysUserRoleServiceImpl SERVICE = new FeedSysUserRoleServiceImpl();
	private FeedSysUserRoleRedis userRoleRedis = FeedSysUserRoleRedisImpl.getInstance();
	private FeedSysUserRoleDao userRoleDao = FeedSysUserRoleDaoImpl.getInstance();
	private FeedSysRoleRedis roleRedis = FeedSysRoleRedisImpl.getInstance();
	private FeedAdminUserRedis adminRedis = FeedAdminUserRedisImpl.getInstance();
	
	private FeedSysUserRoleServiceImpl()
	{}
	
	public static FeedSysUserRoleServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public boolean exists(long forumId, long userId) throws Exception
	{
		try
		{
			return userRoleRedis.exists(forumId, userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean isFull(long forumId) throws Exception
	{
		try
		{
			long count = userRoleDao.getCountByForumId(forumId);
			return count >= GlobalConfig.FORUM_MODERATOR_COUNT;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.isFull throw an error.", e);
			throw e;
		}
	}

	@Override
	public void save(FeedSysUserRole model) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			userRoleRedis.save(model);
			/******************************数据库操作******************************/
			userRoleDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.save throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long forumId, long userId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			userRoleRedis.delete(forumId, userId);
			/******************************数据库操作******************************/
			userRoleDao.delete(forumId, userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public int getRoleId(long forumId, long userId) throws Exception
	{
		try
		{
			return userRoleRedis.getUserRole(forumId, userId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.getRoleId throw an error.", e);
			throw e;
		}
	}

	@Override
	public boolean hasPrivilege(long forumId, long userId, int privilegeId) throws Exception
	{
		try
		{
			///判断是否为管理员
			boolean isAdmin = adminRedis.exists(userId);
			if(isAdmin)
				return true;
			
			int roleId = userRoleRedis.getUserRole(forumId, userId);
			FeedSysRole roleInfo = roleRedis.getInfo(roleId);
			if(null == roleInfo)
				return false;
			
			String privileges = roleInfo.getPrivileges();
			if(StringUtil.isNullOrEmpty(privileges))
				return false;
			
			String[] arrPrivilege = privileges.split(",");
			for(String strPrivilegeId : arrPrivilege)
			{
				if(Integer.parseInt(strPrivilegeId) == privilegeId)
					return true;
			}
			return false;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.hasPrivilege throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedSysUserRole> getUserListByForumId(long forumId) throws Exception
	{
		try 
		{
			return userRoleDao.getListByForumId(forumId);
		}
		catch (Exception e) 
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.getUserListByForumId throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedSysUserRole> getUserList(int pageNum, int pageSize) throws Exception
	{
		try 
		{
			long total = userRoleDao.getUserCount();
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int size = pageNumber.getEnd();
			List<FeedSysUserRole> list = userRoleDao.getUserList(start, size);
			return new Page<FeedSysUserRole>(total, list); 
		} 
		catch (Exception e) 
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.getUserList throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedSysUserRole> getForumListByUserId(long userId) throws Exception
	{
		try 
		{
			return userRoleDao.getForumListByUserId(userId);
		} 
		catch (Exception e) 
		{
			GlobalObject.ERROR_LOG.error("at FeedSysUserRoleServiceImpl.getForumListByUserId throw an error.", e);
			throw e;
		}
	}
}