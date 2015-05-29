package com.mofang.feed.util;

import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author zhaodx
 *
 */
public class SignUtil
{
	public static String buildSign(Map<String, String> getParam, String postParam, String secret)
	{
		Set<String> sortKey = new TreeSet<String>();
		for(String key : getParam.keySet())
		{
			if("sign".equals(key.toLowerCase()))
				continue;
			
			sortKey.add(key);
		}
		
		StringBuilder sbSign = new StringBuilder();
		for(String key : sortKey)
			sbSign.append(key + getParam.get(key));
		
		sbSign.append(postParam);
		sbSign.append(secret);
		return getMd5(sbSign.toString().getBytes());
	}
	
	public static String getMd5(byte[] bytes)
	{
		try
		{
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(bytes);
			String str = bytesToHexString(localMessageDigest.digest());
			return str;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static String bytesToHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
		{
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1)
			{
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}