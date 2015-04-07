package com.mofang.feed.controller.admin.moduleitem;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedModuleItemLogic;
import com.mofang.feed.logic.impl.FeedModuleItemLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/piazza/batchSeq")
public class ModuleItemBatchSequenceAction extends AbstractActionExecutor
{
	private FeedModuleItemLogic logic = FeedModuleItemLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("uid");
		if(!StringUtil.isLong(strUserId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		JSONObject json = new JSONObject(postData);
		long operatorId = Long.parseLong(strUserId);
		JSONArray data = json.optJSONArray("data");
		
		///参数检查
		if(null == data || data.length() == 0)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		JSONObject jsonItem = null;
		long itemId = 0L;
		int sequence = 0;
		for(int i=0; i<data.length(); i++)
		{
			jsonItem = data.getJSONObject(i);
			itemId = jsonItem.optLong("id", 0L);
			sequence = jsonItem.optInt("position", -1);
			if(-1 == sequence)
				continue;
			
			logic.updateDisplayOrder(itemId, sequence, operatorId);
		}
		
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		return result;
	}
}