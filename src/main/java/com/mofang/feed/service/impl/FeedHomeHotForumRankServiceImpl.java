package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.common.UpDownStatus;
import com.mofang.feed.model.FeedHomeHotForumRank;
import com.mofang.feed.mysql.FeedHomeHotForumRankDao;
import com.mofang.feed.mysql.impl.FeedHomeHotForumRankDaoImpl;
import com.mofang.feed.service.FeedHomeHotForumRankService;

/***
 * 
 * @author linjx
 *
 */
public class FeedHomeHotForumRankServiceImpl implements FeedHomeHotForumRankService {

	private static final FeedHomeHotForumRankServiceImpl SERVICE = new FeedHomeHotForumRankServiceImpl();
	private FeedHomeHotForumRankDao forumRankDao = FeedHomeHotForumRankDaoImpl.getInstance();
	
	private FeedHomeHotForumRankServiceImpl(){}
	
	public static FeedHomeHotForumRankServiceImpl getInstance(){
		return SERVICE;
	}
	
	private int upOrDown(List<FeedHomeHotForumRank> list, long forumId, int index){
		for(int idx = 0; idx < list.size(); idx ++){
			FeedHomeHotForumRank model = list.get(idx);
			int oldIndex = model.getDisplayOrder();
			if(model.getForumId() ==  forumId && oldIndex == index){
				return UpDownStatus.EQUAL;
			}else if(model.getForumId() == forumId && oldIndex < index){
				return UpDownStatus.DOWN;
			}else if(model.getForumId() == forumId && oldIndex > index){
				return UpDownStatus.UP;
			}
		}
		return UpDownStatus.UP;
	}

	
	@Override
	public void edit(List<FeedHomeHotForumRank> modelList) throws Exception {
		try {
			List<FeedHomeHotForumRank> oldModelList = forumRankDao.getList();
			if(oldModelList != null && oldModelList.size() == modelList.size()) {
				for(int idx = 0; idx < modelList.size(); idx ++) {
					FeedHomeHotForumRank model = modelList.get(idx);
					model.setUpDown(upOrDown(oldModelList, model.getForumId(), idx + 1));
				}
			}
			
			for(FeedHomeHotForumRank model : modelList){
				forumRankDao.edit(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeForumRankServiceImpl.update throw an error.", e);
			throw e;
		}
	}

	@Override
	public List<FeedHomeHotForumRank> getList() throws Exception {
		try {
			return forumRankDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error("at FeedHomeForumRankServiceImpl.getList throw an error.", e);
			throw e;
		}
	}

}
