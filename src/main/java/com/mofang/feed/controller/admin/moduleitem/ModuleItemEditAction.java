package com.mofang.feed.controller.admin.moduleitem;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.logic.FeedModuleItemLogic;
import com.mofang.feed.logic.impl.FeedModuleItemLogicImpl;
import com.mofang.feed.model.FeedModuleItem;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url = "backend/piazza/modifyPiazza")
public class ModuleItemEditAction extends AbstractActionExecutor
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
		long itemId = json.optLong("id", 0L);
		String title = json.optString("title", "");
		String picUrl = json.optString("pic_url", "");
		String subTitle = json.optString("subtitle", "");
		String strOnlineTime = json.optString("online_time", "");
		
		///参数检查
		if(itemId <= 0 || !StringUtil.isDate(strOnlineTime, "yyyy-MM-dd HH:mm:ss"))
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		FeedModuleItem itemInfo = new FeedModuleItem();
		itemInfo.setItemId(itemId);
		itemInfo.setTitle(title);
		itemInfo.setSubTitle(subTitle);
		itemInfo.setPicUrl(picUrl);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date onlineTime = dateFormat.parse(strOnlineTime);
		itemInfo.setOnlineTime(onlineTime.getTime());
		logic.edit(itemInfo, operatorId);
		
		result.setCode(ReturnCode.SUCCESS);
		result.setMessage(ReturnMessage.SUCCESS);
		return result;
	}
}