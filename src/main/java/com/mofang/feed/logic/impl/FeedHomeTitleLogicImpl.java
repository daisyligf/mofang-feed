package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.DataSource;
import com.mofang.feed.logic.FeedHomeTitleLogic;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.FeedHomeTitle;
import com.mofang.feed.model.FeedThread;
import com.mofang.feed.service.FeedForumService;
import com.mofang.feed.service.FeedHomeTitleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedForumServiceImpl;
import com.mofang.feed.service.impl.FeedHomeTitleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;

public class FeedHomeTitleLogicImpl implements FeedHomeTitleLogic {

	private static final FeedHomeTitleLogicImpl LOGIC = new FeedHomeTitleLogicImpl();
	private FeedHomeTitleService homeTitleService = FeedHomeTitleServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedForumService forumService = FeedForumServiceImpl.getInstance();
	
	private FeedHomeTitleLogicImpl(){}
	
	public static FeedHomeTitleLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue update(List<FeedHomeTitle> modelList) throws Exception {
		try {
			ResultValue result = new ResultValue();
			for(FeedHomeTitle model : modelList){
				FeedThread threadInfo = threadService.getInfo(model.getThreadId(), DataSource.REDIS);
				if(threadInfo == null){
					result.setCode(ReturnCode.THREAD_NOT_EXISTS);
					result.setMessage(ReturnMessage.THREAD_NOT_EXISTS);
					return result;
				}
			}
			homeTitleService.update(modelList);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeTitleLogicImpl.update throw an error.", e);
		}
	}

	@Override
	public ResultValue getList() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedHomeTitle> list = homeTitleService.getList();
			if(list != null){
				JSONObject objTitle = null;
				for(FeedHomeTitle model : list){
					objTitle =  new JSONObject();
					
					long threadId = model.getThreadId();
					int displayOrder = model.getDisplayOrder();
					
					FeedThread thread = threadService.getFullInfo(threadId);
					//String subject = thread.getSubjectFilter();
					String content = thread.getPost().getContentFilter();
					String linkUrl = thread.getLinkUrl();
					
					FeedForum forum = forumService.getInfo(thread.getForumId());
					String forumName = forum.getName();
					
					objTitle.put("forum_name", forumName);
					objTitle.put("thread_id", threadId);
					objTitle.put("thread_name", model.getThreadName());
					objTitle.put("content", content);
					objTitle.put("display_order", displayOrder);
					objTitle.put("link_url", linkUrl);
					data.put(objTitle);
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedHomeTitleLogicImpl.getList throw an error.", e);
		}
	}

}