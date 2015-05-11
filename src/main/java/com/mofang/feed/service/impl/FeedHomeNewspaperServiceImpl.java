package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeNewspaper;
import com.mofang.feed.mysql.FeedHomeNewspaperDao;
import com.mofang.feed.mysql.impl.FeedHomeNewspaperDaoImpl;
import com.mofang.feed.service.FeedHomeNewspaperService;

/***
 * 
 * @author linjx
 * 
 */
public class FeedHomeNewspaperServiceImpl implements FeedHomeNewspaperService {

	private static final FeedHomeNewspaperServiceImpl SERVICE = new FeedHomeNewspaperServiceImpl();
	private FeedHomeNewspaperDao newsPaperDao = FeedHomeNewspaperDaoImpl
			.getInstance();

	public static FeedHomeNewspaperServiceImpl getInstance() {
		return SERVICE;
	}

	private FeedHomeNewspaperServiceImpl() {
	}

	@Override
	public void update(List<FeedHomeNewspaper> modelList) throws Exception {
		try {
			for (FeedHomeNewspaper model : modelList) {
				newsPaperDao.update(model);
			}
		} catch (Exception e) {
			GlobalObject.ERROR_LOG
					.error("at FeedHomeNewspaperServiceImpl.update throw an error.",
							e);
			throw e;
		}
	}

	@Override
	public List<FeedHomeNewspaper> getList() throws Exception {
		try {
			return newsPaperDao.getList();
		} catch (Exception e) {
			GlobalObject.ERROR_LOG.error(
					"at FeedHomeNewspaperServiceImpl.getList throw an error.",
					e);
			throw e;
		}
	}

}
