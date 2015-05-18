package com.mofang.feed.logic.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.RedisFaster;
import com.mofang.feed.global.RedisKey;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedTagLogic;
import com.mofang.feed.model.FeedTag;
import com.mofang.feed.service.FeedTagService;
import com.mofang.feed.service.impl.FeedTagServiceImpl;

public class FeedTagLogicImpl implements FeedTagLogic {

	private static final FeedTagLogicImpl LOGIC = new FeedTagLogicImpl();
	private FeedTagService tagService = FeedTagServiceImpl.getInstance();

	private FeedTagLogicImpl() {
	}

	public static FeedTagLogicImpl getInstance() {
		return LOGIC;
	}

	@Override
	public ResultValue getList() throws Exception {
		try {
			ResultValue result = new ResultValue();
			JSONArray data = new JSONArray();
			List<FeedTag> list = tagService.getList();
			if (list != null) {
				JSONObject objTag = null;
				for (FeedTag model : list) {
					objTag = new JSONObject();

					objTag.put("tag_id", model.getTagId());
					objTag.put("tag_name", model.getTagName());
					data.put(objTag);
				}
			}
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedTagLogicImpl.getList throw an error.",
					e);
		}
	}

	@Override
	public ResultValue delete(List<Integer> tagIdList) throws Exception {
		try {
			ResultValue result = new ResultValue();
			tagService.delete(tagIdList);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedTagLogicImpl.delete throw an error.", e);
		}
	}

	@Override
	public ResultValue add(FeedTag model) throws Exception {
		try {
			ResultValue result = new ResultValue();

			int tagId = (int)RedisFaster.makeUniqueId(RedisKey.TAG_INCREMENT_ID_KEY);
			model.setTagId(tagId);
			
			tagService.add(model);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedTagLogicImpl.add throw an error.", e);
		}
	}

}
