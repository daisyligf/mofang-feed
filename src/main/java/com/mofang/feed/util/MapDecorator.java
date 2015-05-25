package com.mofang.feed.util;

import java.util.HashMap;
import java.util.Map;

import com.mofang.framework.util.StringUtil;

/**
 * Map装饰器
 * @author zhaodx
 *
 */
public class MapDecorator
{
	private Map<String, String> map;
	
	public MapDecorator()
	{
		this.map = new HashMap<String, String>();
	}
	
	public MapDecorator(Map<String, String> map)
	{
		this.map = map;
	}
	
	public void put(String key, Object value)
	{
		if(null == value)
			value = "";
		map.put(key, String.valueOf(value));
	}
	
	public Map<String, String> toMap()
	{
		return map;
	}
	
	public String optString(String key, String defaultVal)
	{
		String value = map.get(key);
		return null != value ? value : defaultVal;
	}
	
	public int optInt(String key, int defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isInteger(value) ? Integer.parseInt(value) : defaultVal;
	}
	
	public long optLong(String key, long defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isLong(value) ? Long.parseLong(value) : defaultVal;
	}
	
	public short optShort(String key, short defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isShort(value) ? Short.parseShort(value) : defaultVal;
	}
	
	public double optDouble(String key, double defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isDouble(value) ? Double.parseDouble(value) : defaultVal;
	}
	
	public float optFloat(String key, float defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isFloat(value) ? Float.parseFloat(value) : defaultVal;
	}
	
	public boolean optBoolean(String key, boolean defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isBoolean(value) ? Boolean.parseBoolean(value) : defaultVal;
	}
	
	public byte optByte(String key, Byte defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isByte(value) ? Byte.parseByte(value) : defaultVal;
	}
	
	public char optChar(String key, char defaultVal)
	{
		String value = map.get(key);
		return StringUtil.isNullOrEmpty(value) ? defaultVal : Character.valueOf(value.charAt(0));
	}
}
