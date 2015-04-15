package com.mofang.feed.model.external;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.GlobalObject;

/**
 * 
 * @author zhaodx
 *
 */
public class User
{
	private long userId;
	private String nickName;
	private String avatar;
	private int level = 1;
	private int exp = 0;
	private int coin = 0;
	private int diamond = 0;
	private int upgradeExp = 0;
	private int gainedExp = 0;
	private JSONArray badges;
	private long registerTime = System.currentTimeMillis();

	public User() 
	{}

	public User(JSONObject json) throws Exception
	{
		try
		{
			this.userId = json.optLong("uid", 0L);
			this.nickName = json.optString("nick_name", "");
			this.avatar = json.optString("avatar", "");
			this.level = json.optInt("level", 1);
			this.exp = json.optInt("exp", 0);
			this.coin = json.optInt("coin", 0);
			this.diamond = json.optInt("diamond", 0);
			this.upgradeExp = json.optInt("upgrade_exp", 0);
			this.gainedExp = json.optInt("gained_exp", 0);
			this.badges = json.optJSONArray("badges");
			this.registerTime = json.optLong("register_time", System.currentTimeMillis());
		} 
		catch (Exception e)
		{
			throw e;
		}
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getUpgradeExp() {
		return upgradeExp;
	}

	public void setUpgradeExp(int upgradeExp) {
		this.upgradeExp = upgradeExp;
	}

	public int getGainedExp() {
		return gainedExp;
	}

	public void setGainedExp(int gainedExp) {
		this.gainedExp = gainedExp;
	}

	public JSONArray getBadges() {
		return badges;
	}

	public void setBadges(JSONArray badges) {
		this.badges = badges;
	}
	
	public long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public JSONObject toJson() 
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put("uid", userId);
			json.put("nick_name", null == nickName ? "" : nickName);
			json.put("avatar", null == avatar ? "" : avatar);
			json.put("level", level);
			json.put("exp", exp);
			json.put("coin", coin);
			json.put("diamond", diamond);
			json.put("upgrade_exp", upgradeExp);
			json.put("gained_exp", gainedExp);
			json.put("badges", badges);
			json.put("register_time", registerTime);
			return json;
		}
		catch (Exception e)
		{
			GlobalObject.ERROR_LOG.error("at User.toJson throw an error.", e);
			return null;
		}
	}
}