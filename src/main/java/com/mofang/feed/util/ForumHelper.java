package com.mofang.feed.util;

import java.util.HashMap;
import java.util.Map;

import com.mofang.feed.component.HttpComponent;
import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.common.ForumURLKey;
import com.mofang.feed.model.FeedForum;
import com.mofang.feed.model.external.Game;
import com.mofang.framework.util.StringUtil;

public class ForumHelper {

	public static final String ABCDE = "ABCDE";
	public static final String FGHIJ = "FGHIJ";
	public static final String KLMNO = "KLMNO";
	public static final String PQRST = "PQRST";
	public static final String WXYZ = "WXYZ";
	public static final String OTHER = "OTHER";
	
	public static String match(String nameSp){
		char p = nameSp.charAt(0);
		int idx;
		for(idx = 0; idx < ABCDE.length(); idx ++){
			if(p == (ABCDE.charAt(idx))){
				return ABCDE;
			}
		}
		for(idx = 0; idx < FGHIJ.length(); idx ++){
			if(p == (FGHIJ.charAt(idx))){
				return FGHIJ;
			}
		}
		for(idx = 0; idx < KLMNO.length(); idx ++){
			if(p == (KLMNO.charAt(idx))){
				return KLMNO;
			}
		}
		for(idx = 0; idx < PQRST.length(); idx ++){
			if(p == (PQRST.charAt(idx))){
				return PQRST;
			}
		}
		for(idx = 0; idx < WXYZ.length(); idx ++){
			if(p == (WXYZ.charAt(idx))){
				return WXYZ;
			}
		}
		return OTHER;
	}
	
	public static Map<String, String> buildUrlMap(FeedForum forum){
		Map<String,String> map = new HashMap<String, String>(3);
		int gameId = forum.getGameId();
		map.put(ForumURLKey.DOWNLOAD_URL_KEY, GlobalConfig.GAME_DOWNLOAD_URL + gameId);
		boolean flag = HttpComponent.checkGift(gameId);
		if(flag) {
			Game game = HttpComponent.getGameInfo(gameId);
			if(game != null) {
				map.put(ForumURLKey.GIFT_URL_KEY, GlobalConfig.GIFT_INFO_URL + game.getName());
			}else {
				map.put(ForumURLKey.GIFT_URL_KEY, "");
			}
		} else{
			map.put(ForumURLKey.GIFT_URL_KEY, "");
		}
		String prefectureUrl = HttpComponent.getPrefectureUrl(forum.getForumId());
		if(StringUtil.isNullOrEmpty(prefectureUrl)) {
			prefectureUrl = "";
		}
		map.put(ForumURLKey.PREFECTURE_URL_KEY, prefectureUrl);
		return map;
	}
	
	
}
