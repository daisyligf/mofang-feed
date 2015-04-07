package com.mofang.feed.controller.web.thread;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.ThreadTag;
import com.mofang.feed.logic.FeedThreadLogic;
import com.mofang.feed.logic.impl.FeedThreadLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/threads/tag/cancel")
public class ThreadCancelTagAction extends AbstractActionExecutor
{
	private FeedThreadLogic logic = FeedThreadLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		
		String postData = context.getPostData();
		if(StringUtil.isNullOrEmpty(postData))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		JSONObject json = new JSONObject(postData);
		long operatorId = json.optLong("operator_id", 0L);
		String threadIds = json.optString("tids", "");
		int tagId = json.optInt("tag_id", 0);
		String reason = json.optString("reason", "");
		
		///参数检查
		if(StringUtil.isNullOrEmpty(threadIds))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		if(tagId != ThreadTag.ELITE && tagId != ThreadTag.MARK)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		String[] arrThreadIds = threadIds.split(",");
		for(String strThreadId : arrThreadIds)
		{
			if(tagId == ThreadTag.ELITE)
				result = logic.cancelElite(Long.parseLong(strThreadId), operatorId, reason);
			else if(tagId == ThreadTag.MARK)
				result = logic.cancelMark(Long.parseLong(strThreadId), operatorId, reason);
			
			result = logic.delete(Long.parseLong(strThreadId), operatorId, reason);
			if(result.getCode() != ReturnCode.SUCCESS)
				return result;
		}
		
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		return result;
	}
}