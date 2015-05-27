package com.mofang.feed.logic.impl;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedModeratorApplyLogic;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.ModeratorApplyCondition;
import com.mofang.feed.service.FeedModeratorApplyService;
import com.mofang.feed.service.impl.FeedModeratorApplyServiceImpl;

public class FeedModeratorApplyLogicImpl implements FeedModeratorApplyLogic {
	
	private static final FeedModeratorApplyLogicImpl LOGIC = new FeedModeratorApplyLogicImpl();
	private FeedModeratorApplyService applyService = FeedModeratorApplyServiceImpl.getInstance();

	private FeedModeratorApplyLogicImpl(){}
	
	public static FeedModeratorApplyLogicImpl getInstance(){
		return LOGIC;
	}
	
	@Override
	public ResultValue apply(FeedModeratorApply model) throws Exception {
		try {
			ResultValue result = new ResultValue();
			ModeratorApplyCondition condition = applyService.checkCondition(model.getUserId(), model.getForumId(), false);
			if(condition.isFollowForumIsOK() && condition.isThreadsIsOK() && condition.isTimeIntervalIsOK()
					 && condition.isTopEliteCountIsOK()){
				
				applyService.add(model);
				
				result.setCode(ReturnCode.SUCCESS);
				result.setMessage(ReturnMessage.SUCCESS);
				return result;
			}
			
			result.setCode(ReturnCode.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
			result.setMessage(ReturnMessage.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
			return result;
		} catch (Exception e) {
			throw new Exception("at FeedModeratorApplyLogicImpl.apply.apply throw an error.", e);
		}
	}

}
