package com.mofang.feed.data.load;

import com.mofang.feed.data.load.impl.FeedAdminUserLoad;
import com.mofang.feed.data.load.impl.FeedBlackListLoad;
import com.mofang.feed.data.load.impl.FeedCommentLoad;
import com.mofang.feed.data.load.impl.FeedForumLoad;
import com.mofang.feed.data.load.impl.FeedModuleItemLoad;
import com.mofang.feed.data.load.impl.FeedPostLoad;
import com.mofang.feed.data.load.impl.FeedPostRecommendLoad;
import com.mofang.feed.data.load.impl.FeedSysRoleLoad;
import com.mofang.feed.data.load.impl.FeedSysUserRoleLoad;
import com.mofang.feed.data.load.impl.FeedTagLoad;
import com.mofang.feed.data.load.impl.FeedThreadLoad;
import com.mofang.feed.data.load.impl.FeedThreadRecommendLoad;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class LoadFactory
{
	public static FeedLoad getInstance(String loadName)
	{
		FeedLoad load = null;
		if(StringUtil.isNullOrEmpty(loadName))
			return load;
		
		loadName = loadName.toLowerCase();
		if("forum".equals(loadName))
			load = new FeedForumLoad();
		else if("thread".equals(loadName))
			load = new FeedThreadLoad();
		else if("post".equals(loadName))
			load = new FeedPostLoad();
		else if("comment".equals(loadName))
			load = new FeedCommentLoad();
		else if("moduleitem".equals(loadName))
			load = new FeedModuleItemLoad();
		else if("threadrecommend".equals(loadName))
			load = new FeedThreadRecommendLoad();
		else if("postrecommend".equals(loadName))
			load = new FeedPostRecommendLoad();
		else if("sysrole".equals(loadName))
			load = new FeedSysRoleLoad();
		else if("sysuserrole".equals(loadName))
			load = new FeedSysUserRoleLoad();
		else if("blacklist".equals(loadName))
			load = new FeedBlackListLoad();
		else if("tag".equals(loadName))
			load = new FeedTagLoad();
		else if("admin".equals(loadName))
			load = new FeedAdminUserLoad();
		
		return load;
	}
}