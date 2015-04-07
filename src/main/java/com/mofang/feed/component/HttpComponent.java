package com.mofang.feed.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.external.FeedRecommendNotify;
import com.mofang.feed.model.external.PostReplyNotify;
import com.mofang.feed.model.external.SensitiveWord;
import com.mofang.feed.model.external.SysMessageNotify;
import com.mofang.feed.model.external.Task;
import com.mofang.feed.model.external.User;
import com.mofang.feed.model.external.Video;
import com.mofang.framework.net.http.HttpClientSender;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class HttpComponent
{
	/**
	 * 发送系统消息通知
	 * @param model
	 */
	public static void pushSysMessageNotify(SysMessageNotify model)
	{
		JSONObject json = model.toJson();
		if(null == json)
			return;
		
		post(GlobalObject.HTTP_CLIENT_CHATSERVICE, GlobalConfig.CHAT_SERVICE_URL, json.toString());
	}
	
	/**
	 * 发送Feed点赞通知
	 * @param model
	 */
	public static void pushFeedRecommendNotify(FeedRecommendNotify model)
	{
		JSONObject json = model.toJson();
		if(null == json)
			return;
		
		post(GlobalObject.HTTP_CLIENT_CHATSERVICE, GlobalConfig.CHAT_SERVICE_URL, json.toString());
	}
	
	/**
	 * 发送帖子回复通知
	 * @param model
	 */
	public static void pushPostReplyNotify(PostReplyNotify model)
	{
		JSONObject json = model.toJson();
		if(null == json)
			return;
		
		post(GlobalObject.HTTP_CLIENT_CHATSERVICE, GlobalConfig.CHAT_SERVICE_URL, json.toString());
	}
	
	/**
	 * 执行任务
	 * @param model
	 */
	public static void execTask(Task model)
	{
		JSONObject json = model.toJson();
		if(null == json)
			return;
		
		post(GlobalObject.HTTP_CLIENT_TASKSERVICE, GlobalConfig.TASK_EXEC_URL, json.toString());
	}
	
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public static User getUserInfo(long userId)
	{
		String requestUrl = GlobalConfig.USER_INFO_URL + "?user=" + userId + "&user_type=1&more=1";
		String result = get(GlobalObject.HTTP_CLIENT_USERSERVICE, requestUrl);
		if(StringUtil.isNullOrEmpty(result))
			return null;
		
		try
		{
			JSONObject json = new JSONObject(result);
			int code = json.optInt("code", -1);
			if(0 != code)
				return null;
			
			JSONObject data = json.optJSONObject("data");
			if(null == data)
				return null;
			
			User user = new User();
			user.setUserId(userId);
			user.setNickName(data.optString("nickname", ""));
			user.setAvatar(data.optString("avatar", ""));
			user.setLevel(data.optInt("level", 1));
			user.setExp(data.optInt("exp", 0));
			user.setCoin(data.optInt("coin", 0));
			user.setDiamond(data.optInt("diamond", 0));
			user.setUpgradeExp(data.optInt("upgrade_exp", 0));
			user.setGainedExp(data.optInt("gained_exp", 0));
			user.setBadges(data.optJSONArray("badge"));
			return user;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at HttpComponent.getUserInfo throw an error.", e);
			return null;
		}
	}
	
	public static Map<Long, User> getUserInfoByIds(Set<Long> userIds)
	{
		if(null == userIds || userIds.size() == 0)
			return null;
		
		String uids = "";
		for(Long userId : userIds)
			uids += "," + userId;
		
		if(uids.length() > 0)
			uids = uids.substring(1);
		
		String requestUrl = GlobalConfig.BATCH_USER_INFO_URL + "?uids=" + uids + "&more=1";
		String result = get(GlobalObject.HTTP_CLIENT_USERSERVICE, requestUrl);
		if(StringUtil.isNullOrEmpty(result))
			return null;
		
		try
		{
			JSONObject json = new JSONObject(result);
			int code = json.optInt("code", -1);
			if(0 != code)
				return null;
			
			JSONArray data = json.optJSONArray("data");
			if(null == data)
				return null;
			
			Map<Long, User> map = new HashMap<Long, User>();
			User user = null;
			JSONObject jsonUser = null;
			for(int i=0; i<data.length(); i++)
			{
				jsonUser = data.getJSONObject(i);
				user = new User();
				long userId = jsonUser.optLong("uid", 0L);
				user.setUserId(userId);
				user.setNickName(jsonUser.optString("nickname", ""));
				user.setAvatar(jsonUser.optString("avatar", ""));
				user.setLevel(jsonUser.optInt("level", 1));
				user.setExp(jsonUser.optInt("exp", 0));
				user.setCoin(jsonUser.optInt("coin", 0));
				user.setDiamond(jsonUser.optInt("diamond", 0));
				user.setUpgradeExp(jsonUser.optInt("upgrade_exp", 0));
				user.setGainedExp(jsonUser.optInt("gained_exp", 0));
				user.setBadges(jsonUser.optJSONArray("badge"));
				map.put(userId, user);
			}
			return map;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at HttpComponent.getUserInfoByIds throw an error.", e);
			return null;
		}
	}
	
	/**
	 * 敏感词过滤
	 * @param text
	 * @return
	 */
	public static SensitiveWord sensitiveFilter(String text)
	{
		String postData = "s=" + text;
		String result = post(GlobalObject.HTTP_CLIENT_SENSITIVEWORD, GlobalConfig.SENSITIVE_WORD_URL, postData);
		if(StringUtil.isNullOrEmpty(result))
			return null;
		try
		{
			JSONObject json = new JSONObject(result);
			SensitiveWord word = new SensitiveWord();
			word.setErrorNum(json.optInt("error_num", 0));
			word.setOutMark(json.optString("out_mark", ""));
			word.setOut(json.optString("out", ""));
			word.setTips(json.optString("tips", ""));
			word.setFatal(json.optBoolean("fatal", false));
			return word;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at HttpComponent.sensitiveFilter throw an error.", e);
			return null;
		}
	}
	
	/**
	 * 获取视频信息
	 * @param videoId
	 * @return
	 */
	public static Video getVideoInfo(long videoId)
	{
		String requestUrl = GlobalConfig.VIDEO_INFO_URL + "?video_id=" + videoId;
		String result = get(GlobalObject.HTTP_CLIENT_VIDEOSERVICE, requestUrl);
		if(StringUtil.isNullOrEmpty(result))
			return null;
		
		try
		{
			JSONObject json = new JSONObject(result);
			int code = json.optInt("code", -1);
			if(0 != code)
				return null;
			
			JSONObject data = json.optJSONObject("data");
			if(null == data)
				return null;
			
			Video video = new Video();
			video.setVideoId(videoId);
			video.setThumbnail(data.optString("thumbnail", ""));
			video.setDuration(data.optInt("duration", 0));
			return video;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at HttpComponent.getVideoInfo throw an error.", e);
			return null;
		}
	}
	
	private static String get(CloseableHttpClient httpClient, String requestUrl)
	{
		StringBuilder strLog = new StringBuilder();
		strLog.append("request url: " + requestUrl + " ");
		try
		{
			String result = HttpClientSender.get(httpClient, requestUrl);
			strLog.append("response data: " + ((null == result) ? "" : result) + " ");
			GlobalObject.INFO_LOG.info(strLog.toString());
			return result;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error(strLog.toString(), e);
			return null;
		}
	}
	
	private static String post(CloseableHttpClient httpClient, String requestUrl, String postData)
	{
		StringBuilder strLog = new StringBuilder();
		strLog.append("request url: " + requestUrl + " ");
		strLog.append("request data: " + postData + " ");
		try
		{
			String result = HttpClientSender.post(httpClient, requestUrl, postData);
			strLog.append("response data: " + ((null == result) ? "" : result) + " ");
			GlobalObject.INFO_LOG.info(strLog.toString());
			return result;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error(strLog.toString(), e);
			return null;
		}
	}
}