package com.mofang.feed.mysql.impl;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedForumTag;
import com.mofang.feed.mysql.FeedForumTagDao;
import com.mofang.framework.data.mysql.AbstractMysqlSupport;
import com.mofang.framework.data.mysql.core.criterion.operand.AndOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.EqualOperand;
import com.mofang.framework.data.mysql.core.criterion.operand.Operand;
import com.mofang.framework.data.mysql.core.criterion.operand.WhereOperand;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumTagDaoImpl extends AbstractMysqlSupport<FeedForumTag> implements FeedForumTagDao
{
	private final static FeedForumTagDaoImpl DAO = new FeedForumTagDaoImpl();
	
	private FeedForumTagDaoImpl()
	{
		try
		{
			super.setMysqlPool(GlobalObject.MYSQL_CONNECTION_POOL);
		}
		catch (Exception e) 
		{}
	}
	
	public static FeedForumTagDaoImpl getInstance()
	{
		return DAO;
	}

	@Override
	public boolean exists(long forumId, int tagId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand tagEqual = new EqualOperand("tag_id", tagId);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(tagEqual);
		long count = super.getCount(where);
		return count > 0L;
	}

	@Override
	public void add(FeedForumTag model) throws Exception
	{
		super.insert(model);
	}

	@Override
	public void delete(long forumId, long tagId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		Operand tagEqual = new EqualOperand("tag_id", tagId);
		Operand and = new AndOperand();
		where.append(forumEqual).append(and).append(tagEqual);
		super.deleteByWhere(where);
	}

	@Override
	public void deleteByTagId(int tagId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand tagEqual = new EqualOperand("tag_id", tagId);
		where.append(tagEqual);
		super.deleteByWhere(where);
	}

	@Override
	public void deleteByForumId(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand tagEqual = new EqualOperand("forum_id", forumId);
		where.append(tagEqual);
		super.deleteByWhere(where);
	}

	@Override
	public List<Integer> getTagIdListByForumId(long forumId) throws Exception
	{
		Operand where = new WhereOperand();
		Operand forumEqual = new EqualOperand("forum_id", forumId);
		where.append(forumEqual);
		List<FeedForumTag> list = super.getList(where);
		if(null == list)
			return null;
		
		List<Integer> tagList = new ArrayList<Integer>();
		for(FeedForumTag model : list)
			tagList.add(model.getTagId());
		return tagList;
	}
}