package com.mofang.feed.controller.v3.admin.admin;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.admin.FeedAdminUserLogic;
import com.mofang.feed.logic.admin.impl.FeedAdminUserLogicImpl;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "feed/v2/backend/admin/search")
public class AdminSearchAction extends AbstractActionExecutor
{
	private FeedAdminUserLogic logic = FeedAdminUserLogicImpl.getInstance();

	@Override
	protected ResultValue exec(HttpRequestContext context) throws Exception
	{
		ResultValue result = new ResultValue();
		String strUserId = context.getParameters("user_id");
		String strPageNum = context.getParameters("page");
		String strPageSize = context.getParameters("size");
		
		if(!StringUtil.isLong(strUserId))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		long userId = Long.parseLong(strUserId);
		
		int pageNum = 1;
		if(StringUtil.isInteger(strPageNum))
			pageNum = Integer.parseInt(strPageNum);
		
		int pageSize = 50;
		if(StringUtil.isInteger(strPageSize))
			pageSize = Integer.parseInt(strPageSize);
		
		return logic.searchByUserId(userId, pageNum, pageSize);
	}
}