package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedSysRole;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.mysql.FeedSysRoleDao;
import com.mofang.feed.mysql.FeedSysUserRoleDao;
import com.mofang.feed.mysql.impl.FeedSysRoleDaoImpl;
import com.mofang.feed.mysql.impl.FeedSysUserRoleDaoImpl;
import com.mofang.feed.redis.FeedSysRoleRedis;
import com.mofang.feed.redis.FeedSysUserRoleRedis;
import com.mofang.feed.redis.impl.FeedSysRoleRedisImpl;
import com.mofang.feed.redis.impl.FeedSysUserRoleRedisImpl;
import com.mofang.feed.service.FeedSysRoleService;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedSysRoleServiceImpl implements FeedSysRoleService
{
	private final static FeedSysRoleServiceImpl SERVICE = new FeedSysRoleServiceImpl();
	private FeedSysRoleRedis roleRedis = FeedSysRoleRedisImpl.getInstance();
	private FeedSysRoleDao roleDao = FeedSysRoleDaoImpl.getInstance();
	private FeedSysUserRoleRedis userRoleRedis = FeedSysUserRoleRedisImpl.getInstance();
	private FeedSysUserRoleDao userRoleDao = FeedSysUserRoleDaoImpl.getInstance();
	
	private FeedSysRoleServiceImpl()
	{}
	
	public static FeedSysRoleServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public void add(FeedSysRole model) throws Exception
	{
		try
		{
			int roleId = roleRedis.makeUniqueId();
			model.setRoleId(roleId);
			/******************************redis操作******************************/
			///保存到redis中
			roleRedis.save(model);
			/******************************数据库操作******************************/
			///保存到数据库中
			roleDao.add(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleServiceImpl.add throw an error.", e);
			throw e;
		}
	}

	@Override
	public void update(FeedSysRole model) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///保存到redis中
			roleRedis.save(model);
			/******************************数据库操作******************************/
			///保存到数据库中
			roleDao.update(model);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleServiceImpl.update throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(int roleId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			///保存到redis中
			roleRedis.delete(roleId);
			///删除redis中用户和角色的对应关系
			List<FeedSysUserRole> userRoleList = userRoleDao.getListByRoleId(roleId);
			if(null != userRoleList)
			{
				for(FeedSysUserRole userRole : userRoleList)
				{
					userRoleRedis.delete(userRole.getForumId(), userRole.getUserId());
				}
			}
			/******************************数据库操作******************************/
			///保存到数据库中
			roleDao.delete(roleId);
			///删除数据库中用户和角色的对应关系
			userRoleDao.deleteByRoleId(roleId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedSysRole getInfo(int roleId) throws Exception
	{
		try
		{
			return roleRedis.getInfo(roleId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedSysRole> getList() throws Exception
	{
		try
		{
			return roleDao.getList(null);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRoleServiceImpl.getList throw an error.", e);
			throw e;
		}
	}
}