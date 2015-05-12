package com.mofang.feed.service.impl;

import java.util.List;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.FeedHomeTicker;
import com.mofang.feed.mysql.FeedHomeTickerDao;
import com.mofang.feed.mysql.impl.FeedHomeTickerDaoImpl;
import com.mofang.feed.service.FeedHomeTickerService;

/***
 * 
 * @author linjx
 * 
 */
public class FeedHomeTickerServiceImpl implements FeedHomeTickerService {

	private static final FeedHomeTickerServiceImpl SERVICE = new FeedHomeTickerServiceImpl();
	private FeedHomeTickerDao newsPaperDao = FeedHomeTickerDaoImpl
			.getInstance();

	public static FeedHomeTickerServiceImpl getInstance() {
		return SERVICE;
	}

	private FeedHomeTickerServiceImpl() {
	}

	@Override
	public void edit(List<FeedHomeTicker> modelList) throws Exception {
		try {
			for (FeedHomeTicker model : modelList) {
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
	public List<FeedHomeTicker> getList() throws Exception {
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
