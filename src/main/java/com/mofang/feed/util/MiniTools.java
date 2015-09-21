package com.mofang.feed.util;

import org.json.JSONArray;

import com.mofang.feed.global.GlobalObject;
import com.mofang.framework.util.StringUtil;

/**
 * 看名字就知道啦，一些实在不知道放在哪个类的妖孽方法，都归我管！
 * @author zhaodx
 *
 */
public class MiniTools
{
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static JSONArray StringToJSONArray(String value)
	{
		JSONArray array = new JSONArray();
		try
		{
			if(StringUtil.isNullOrEmpty(value))
				return array;
			
			if(value.startsWith("["))
				value = value.substring(1, value.length());
			if(value.endsWith("]"))
				value = value.substring(0, value.length() - 1);
			
			if(value.length() == 0)
				return array;
			
			String[] items = value.split(",");
			if(null == items || items.length == 0)
				return array;
			
			for(String item : items)
				array.put(item);
			return array;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at MiniTools.StringToJSONArray throw an error.", e);
			return array;
		}
	}
	
	public static String JSONArrayToString(JSONArray array)
	{
		try
		{
			if(null == array)
				return null;
			
			String value = array.toString();
			value = value.substring(1);
			value = value.substring(0, value.length() - 1);
			return value;
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at MiniTools.JSONArrayToString throw an error.", e);
			return null;
		}
	}
	
	public static String filterSpecialChars(String content) {
    	String regex = "------&gt;([\\s\\S]*?)&lt;------";
		return content.replaceAll(regex, "");
	}
	
}