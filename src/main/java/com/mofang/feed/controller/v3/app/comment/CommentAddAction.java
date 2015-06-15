package com.mofang.feed.controller.v3.app.comment;

import org.json.JSONObject;

import com.mofang.feed.controller.AbstractActionExecutor;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.LimitConstants;
import com.mofang.feed.logic.app.FeedCommentLogic;
import com.mofang.feed.logic.app.impl.FeedCommentLogicImpl;
import com.mofang.feed.model.FeedComment;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.annotation.Action;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;

/**
 * 
 * @author zhaodx
 *
 */
@Action(url="feed/v3/sendcomment")
public class CommentAddAction extends AbstractActionExecutor
{
	private FeedCommentLogic logic = FeedCommentLogicImpl.getInstance();

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
		
		long userId = Long.parseLong(strUserId);
		JSONObject json = new JSONObject(postData);
		long postId = json.optLong("postid", 0L);
		String content = json.optString("content", "");
		
		///参数检查
		if(postId <= 0 || StringUtil.isNullOrEmpty(content) || content.length() > LimitConstants.COMMENT_CONTENT_LENGTH)
		{
			result.setCode(ReturnCode.CLIENT_REQUEST_DATA_IS_INVALID);
			result.setMessage(ReturnMessage.CLIENT_REQUEST_DATA_IS_INVALID);
			return result;
		}
		
		///构造Comment实体对象
		FeedComment commentInfo = new FeedComment();
		commentInfo.setUserId(userId);
		commentInfo.setPostId(postId);
		commentInfo.setContent(content);
		
		return logic.add(commentInfo);
	}
}