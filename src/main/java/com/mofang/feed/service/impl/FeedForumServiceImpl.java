package com.mofang.feed.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.CommentStatus;
import com.mofang.feed.global.common.ForumType;
import com.mofang.feed.global.common.PostStatus;
import com.mofang.feed.global.common.ThreadStatus;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.feed.model.Page;
import com.mofang.feed.model.external.FollowForumCount;
import com.mofang.feed.mysql.FeedCommentDao;
import com.mofang.feed.mysql.FeedForumDao;
import com.mofang.feed.mysql.FeedForumTagDao;
import com.mofang.feed.mysql.FeedHomeHotForumDao;
import com.mofang.feed.mysql.FeedHomeHotForumRankDao;
import com.mofang.feed.mysql.FeedHomeRecommendGameDao;
import com.mofang.feed.mysql.FeedHomeRecommendGameRankDao;
import com.mofang.feed.mysql.FeedPostDao;
import com.mofang.feed.mysql.FeedThreadDao;
import com.mofang.feed.mysql.impl.FeedCommentDaoImpl;
import com.mofang.feed.mysql.impl.FeedForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedForumTagDaoImpl;
import com.mofang.feed.mysql.impl.FeedHomeHotForumDaoImpl;
import com.mofang.feed.mysql.impl.FeedHomeHotForumRankDaoImpl;
import com.mofang.feed.mysql.impl.FeedHomeRecommendGameDaoImpl;
import com.mofang.feed.mysql.impl.FeedHomeRecommendGameRankDaoImpl;
import com.mofang.feed.mysql.impl.FeedPostDaoImpl;
import com.mofang.feed.mysql.impl.FeedThreadDaoImpl;
import com.mofang.feed.redis.FeedForumRedis;
import com.mofang.feed.redis.FeedThreadRedis;
import com.mofang.feed.redis.ForumUrlRedis;
import com.mofang.feed.redis.HotForumListRedis;
import com.mofang.feed.redis.RecommendGameListRedis;
import com.mofang.feed.redis.impl.FeedForumRedisImpl;
import com.mofang.feed.redis.impl.FeedThreadRedisImpl;
import com.mofang.feed.redis.impl.ForumUrlRedisImpl;
import com.mofang.feed.redis.impl.HotForumListRedisImpl;
import com.mofang.feed.redis.impl.RecommendGameListRedisImpl;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.solr.FeedCommentSolr;
import com.mofang.feed.solr.FeedForumSolr;
import com.mofang.feed.solr.FeedPostSolr;
import com.mofang.feed.solr.FeedThreadSolr;
import com.mofang.feed.solr.impl.FeedCommentSolrImpl;
import com.mofang.feed.solr.impl.FeedForumSolrImpl;
import com.mofang.feed.solr.impl.FeedPostSolrImpl;
import com.mofang.feed.solr.impl.FeedThreadSolrImpl;
import com.mofang.feed.util.ForumHelper;
import com.mofang.feed.util.MysqlPageNumber;
import com.mofang.framework.util.ChineseSpellUtil;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedForumServiceImpl implements FeedForumService
{
	private final static FeedForumServiceImpl SERVICE = new FeedForumServiceImpl();
	private FeedForumRedis forumRedis = FeedForumRedisImpl.getInstance();
	private FeedForumDao forumDao = FeedForumDaoImpl.getInstance();
	private FeedThreadRedis threadRedis = FeedThreadRedisImpl.getInstance();
	private FeedThreadDao threadDao = FeedThreadDaoImpl.getInstance();
	private FeedPostDao postDao = FeedPostDaoImpl.getInstance();
	private FeedCommentDao commentDao = FeedCommentDaoImpl.getInstance();
	private FeedForumSolr forumSolr = FeedForumSolrImpl.getInstance();
	private FeedThreadSolr threadSolr = FeedThreadSolrImpl.getInstance();
	private FeedPostSolr postSolr = FeedPostSolrImpl.getInstance();
	private FeedCommentSolr commentSolr = FeedCommentSolrImpl.getInstance();
	private FeedForumTagDao forumTagDao = FeedForumTagDaoImpl.getInstance();
	private ForumUrlRedis forumUrlRedis = ForumUrlRedisImpl.getInstance();
	private FeedHomeHotForumDao hotForumDao = FeedHomeHotForumDaoImpl.getInstance();
	private FeedHomeHotForumRankDao hotForumRankDao = FeedHomeHotForumRankDaoImpl.getInstance();
	private FeedHomeRecommendGameDao recommendGameDao = FeedHomeRecommendGameDaoImpl.getInstance();
	private FeedHomeRecommendGameRankDao recommendGameRankDao = FeedHomeRecommendGameRankDaoImpl.getInstance();
	private RecommendGameListRedis recommendGameListRedis = RecommendGameListRedisImpl.getInstance();
	private HotForumListRedis hotForumListRedis = HotForumListRedisImpl.getInstance();
	private FeedHomeHotForumRankDao forumRankDao = FeedHomeHotForumRankDaoImpl.getInstance();
	
	private FeedForumServiceImpl()
	{}
	
	public static FeedForumServiceImpl getInstance()
	{
		return SERVICE;
	}

	@Override
	public long build(FeedForum model) throws Exception
	{
		try
		{
			long forumId = forumRedis.makeUniqueId();
			String nameSpell = ChineseSpellUtil.GetChineseSpell(model.getName());
			model.setForumId(forumId);
			model.setNameSpell(nameSpell);
			int type = model.getType();
			
			/******************************redis操作******************************/
			///保存版块信息
			forumRedis.save(model);
			///保存字母分组板块信息
			if(!StringUtil.isNullOrEmpty(nameSpell)) 
			{
				nameSpell = nameSpell.substring(0, 1);
				String nameKey = ForumHelper.match(nameSpell);
				long createTime = model.getCreateTime();
				if(type == ForumType.HOT_FORUM) 
					hotForumListRedis.addHotForumList(nameKey, forumId, createTime);
				else if(type == ForumType.RECOMMEND_GAME) 
					recommendGameListRedis.addRecommendGameList(nameKey, forumId, createTime);
			}
			///保存板块url信息
			forumUrlRedis.setUrl(forumId, ForumHelper.buildUrlMap(model));
			
			/******************************数据库操作******************************/
			///保存版块信息
			forumDao.add(model);
			
			/******************************Solr操作******************************/
			///保存到solr(隐藏版块不进入Solr)
			if(!model.isHidden())
				forumSolr.add(model);
			
			/******************************同步产品库版块ID******************************/
			if(model.getGameId() > 0)
				HttpComponent.SyncGameForumId(model.getGameId(), forumId);
			
			return forumId;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.build throw an error.", e);
			throw e;
		}
	}

	@Override
	public void edit(FeedForum model) throws Exception
	{
		try
		{
			String nameSpell = ChineseSpellUtil.GetChineseSpell(model.getName());
			model.setNameSpell(nameSpell);
			
			/******************************redis操作******************************/
			///保存版块信息
			forumRedis.save(model);
			
			/******************************数据库操作******************************/
			///保存版块信息
			forumDao.update(model);
			
			/******************************Solr操作******************************/
			///保存到solr(隐藏版块不进入Solr)
			if(!model.isHidden())
				forumSolr.add(model);
			
			/******************************同步产品库版块ID******************************/
			if(model.getGameId() > 0)
				HttpComponent.SyncGameForumId(model.getGameId(), model.getForumId());
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.edit throw an error.", e);
			throw e;
		}
	}

	@Override
	public void delete(long forumId) throws Exception
	{
		try
		{
			/******************************redis操作******************************/
			FeedForum forum = forumRedis.getInfo(forumId);
			/// 删除新游更多列表或者热游更多列表 版块信息
			if(forum != null) 
			{
				int type = forum.getType();
				String nameSpell = forum.getNameSpell();
				if(!StringUtil.isNullOrEmpty(nameSpell)) 
				{
					nameSpell = nameSpell.substring(0, 1);
					String nameKey = ForumHelper.match(nameSpell);
					if(type == ForumType.HOT_FORUM) 
						hotForumListRedis.delete(nameKey, forumId);
					else if(type == ForumType.RECOMMEND_GAME) 
						recommendGameListRedis.delete(nameKey, forumId);
				}
			}
			
			///删除版块信息
			forumRedis.delete(forumId);
			
			///删除板块对应url信息
			forumUrlRedis.delete(forumId);
			
			///将所属该版块的主题信息(thread_info)删除
			Set<String> threadSet = threadRedis.getForumThreadList(forumId, 0, -1);
			if(null != threadSet && threadSet.size() > 0)
			{
				for(String threadId : threadSet)
					threadRedis.delete(Long.parseLong(threadId));
			}
			///删除版块主题列表
			threadRedis.deleteForumThreadListByForumId(forumId);
			///删除版块置顶主题列表
			threadRedis.deleteForumTopThreadListByForumId(forumId);
			
			/******************************数据库操作******************************/
			///删除版块信息
			forumDao.delete(forumId);
			///删除版块和标签的对应关系
			forumTagDao.deleteByForumId(forumId);
			///删除热游排行榜板块
			hotForumRankDao.delete(forumId);
			///删除热游列表板块
			hotForumDao.delete(forumId);
			///删除新游排行榜板块
			recommendGameRankDao.delete(forumId);
			///删除新游列表板块
			recommendGameDao.delete(forumId);
			
			///将所属该版块的主题的状态值设为0(已删除)
			threadDao.updateStatusByForumId(forumId, ThreadStatus.DELETED);
			///将所属该版块的楼层的状态值设为0(已删除)
			postDao.updateStatusByForumId(forumId, PostStatus.DELETED);
			///将所属该版块的评论的状态值设为0(已删除)
			commentDao.updateStatusByForumId(forumId, CommentStatus.DELETED);
			
			/******************************Solr操作******************************/
			///删除版块索引
			forumSolr.deleteById(forumId);
			///删除版块下所有主题索引
			threadSolr.deleteByForumId(forumId);
			///删除版块下所有楼层索引
			postSolr.deleteByForumId(forumId);
			///删除版块下所有评论索引
			commentSolr.deleteByForumId(forumId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.delete throw an error.", e);
			throw e;
		}
	}

	@Override
	public FeedForum getInfo(long forumId) throws Exception
	{
		try
		{
			return forumRedis.getInfo(forumId);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getInfo throw an error.", e);
			throw e;
		}
	}
	
	@Override
	public FeedForum getInfoWithTags(long forumId) throws Exception
	{
		try
		{
			FeedForum forumInfo = forumRedis.getInfo(forumId);
			Set<Integer> tagSet = forumTagDao.getTagIdListByForumId(forumId);
			if(null != forumInfo)
				forumInfo.setTags(tagSet);
			return forumInfo;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getInfoWithTags throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedForum> getForumList(int type, int pageNum, int pageSize) throws Exception
	{
		try
		{
			long total = forumDao.getForumCount(type);
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int end = pageNumber.getEnd();
			List<FeedForum> list = forumDao.getForumList(type, start, end);
			return new Page<FeedForum>(total, list);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedForum> getForumList(Set<Long> forumIds) throws Exception
	{
		try
		{
			List<FeedForum> list = new ArrayList<FeedForum>();
			FeedForum forumInfo = null;
			for(long forumId : forumIds)
			{
				forumInfo = forumRedis.getInfo(forumId);
				if(null == forumInfo)
					continue;
				
				list.add(forumInfo);
			}
			return list;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getForumList throw an error.", e);
			throw e;
		}
	}

	@Override
	public Page<FeedForum> search(String forumName, int pageNum, int pageSize) throws Exception
	{
		try
		{
			MysqlPageNumber pageNumber = new MysqlPageNumber(pageNum, pageSize);
			int start = pageNumber.getStart();
			int size = pageNumber.getEnd();
			return forumSolr.search(forumName, start, size);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.search throw an error.", e);
			throw e;
		}
	}

	@Override
	public Map<String, String> getUrlMap(long forumId) throws Exception {
		try {
			return forumUrlRedis.getUrl(forumId);
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getUrlMap throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedForum> getForumRecomendList(Set<Long> gameIds)
			throws Exception {
		try {
			Set<Long> forumIds = HttpComponent.getForumIdsByGameIds(gameIds);
			int size = 0;
			if(forumIds == null || forumIds.size() == 0) {
				//没有板块推荐列表，从热门取前3个
				size = 3;
				forumIds = new HashSet<Long>(size);
				
			} else {
				size = 3 - forumIds.size();
			}
			
			if(size > 0) {
				
				List<FeedHomeHotForumRank> hotForumRankList = forumRankDao.getList();
				if(hotForumRankList != null && hotForumRankList.size() > 0) {
					for(int idx = 0; idx < hotForumRankList.size(); idx ++) {
						FeedHomeHotForumRank rank = hotForumRankList.get(idx);
						//可能会重复
						if(forumIds.contains(rank.getForumId()))
							continue;
						if(forumIds.size() == 3)
							break;
						forumIds.add(rank.getForumId());
					}
				}
			}
			
			//获取关注数
			Map<Long, FollowForumCount> map = HttpComponent.getForumFollowCount(forumIds);
			
			List<FeedForum> list = new ArrayList<FeedForum>(size);
			for(long forumId : forumIds) {
				FeedForum forum = forumRedis.getInfo(forumId);
				
				FollowForumCount ffc = map.get(forumId);
				if(ffc != null) 
					forum.setFollows(ffc.getTotalFollows());
				
				if(forum != null)
					list.add(forum);
			}
			return list;
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedForumServiceImpl.getForumRecomendList throw an error.");
			throw e;
		}
		
	}
}