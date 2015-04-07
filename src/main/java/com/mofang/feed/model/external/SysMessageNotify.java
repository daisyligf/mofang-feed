package com.mofang.feed.model.external;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author zhaodx
 *
 */
public class SysMessageNotify 
{
	private String action = "push_sys_msg";
	private List<Long> uidList;
	private int messageType = 1;
	private String messageCategory = "";
	private String title;
	private String detail;
	private String icon;
	private boolean isShowNotify = true;
	private String clickAction;

	public String getAction() 
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public List<Long> getUidList() 
	{
		return uidList;
	}

	public void setUidList(List<Long> uidList)
	{
		this.uidList = uidList;
	}

	public int getMessageType() 
	{
		return messageType;
	}

	public void setMessageType(int messageType) 
	{
		this.messageType = messageType;
	}

	public String getMessageCategory()
	{
		return messageCategory;
	}

	public void setMessageCategory(String messageCategory)
	{
		this.messageCategory = messageCategory;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getDetail() 
	{
		return detail;
	}

	public void setDetail(String detail)
	{
		this.detail = detail;
	}

	public String getIcon() 
	{
		return icon;
	}

	public void setIcon(String icon) 
	{
		this.icon = icon;
	}

	public boolean isShowNotify() 
	{
		return isShowNotify;
	}

	public void setShowNotify(boolean isShowNotify)
	{
		this.isShowNotify = isShowNotify;
	}

	public String getClickAction()
	{
		return clickAction;
	}

	public void setClickAction(String clickAction)
	{
		this.clickAction = clickAction;
	}
	
	public JSONObject toJson()
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put("act", action);
			JSONArray uidArray = new JSONArray();
			if(null != uidList)
				uidArray.put(uidList);
			json.put("uid_list", uidArray);
			JSONObject jsonMsg = new JSONObject();
			JSONObject jsonMsgContent = new JSONObject();
			
			jsonMsg.put("msg_type", messageType);
			jsonMsg.put("msg_category", messageCategory);
			jsonMsgContent.put("title", null == title ? "" : title);
			jsonMsgContent.put("detail", null == detail ? "" : detail);
			jsonMsgContent.put("icon", null == icon ? "" : icon);
			jsonMsg.put("content", jsonMsgContent);
			json.put("msg", jsonMsg);
			json.put("is_show_notify", isShowNotify);
			json.put("click_act", null == clickAction ? "" : clickAction);
			
			return json;
		}
		catch(Exception e)
		{
			return null;
		}
	}
}