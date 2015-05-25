package com.mofang.feed.model.external;

import org.json.JSONObject;

/**
 * 
 * @author zhaodx
 *
 */
public class Task
{
	private long userId;
	private int event;
	
	public Task(long userId, int event)
	{
		this.userId = userId;
		this.event = event;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public int getEvent()
	{
		return event;
	}

	public void setEvent(int event)
	{
		this.event = event;
	}
	
	public JSONObject toJson()
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put("uid", userId);
			json.put("event", event);
			return json;
		}
		catch(Exception e)
		{
			return null;
		}
	}
}