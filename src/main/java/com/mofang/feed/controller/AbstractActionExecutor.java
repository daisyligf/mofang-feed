package com.mofang.feed.controller;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnCodeHelper;
import com.mofang.feed.util.SignUtil;
import com.mofang.framework.util.StringUtil;
import com.mofang.framework.web.server.reactor.context.HttpRequestContext;
import com.mofang.framework.web.server.reactor.context.RequestContext;
import com.mofang.framework.web.server.reactor.proxy.ActionExecutor;

/**
 * 
 * @author zhaodx
 *
 */
public abstract class AbstractActionExecutor implements ActionExecutor
{	
	@Override
	public String execute(RequestContext context)
	{
		long start = System.currentTimeMillis();
		long end;
		String clsName = getClass().getSimpleName();
		
		///获取请求的基本信息
		HttpRequestContext httpContext = (HttpRequestContext)context; 
		String requestUri = httpContext.getRequestUrl();
		String postData = httpContext.getPostData();
		StringBuilder strLog= new StringBuilder();
		String atom = null;
		try
		{
			ResultValue result = new ResultValue();
			
			if(needCheckSign())
			{
				///验证签名
				String appId = context.getParameters("appid");
				if(!appId.equals(GlobalConfig.SERVER_APPID))
				{
					result.setCode(ReturnCode.SIGN_ERROR);
					result.setMessage("服务签名错误");
					return result.toJsonString();
				}
				
				Map<String, String> getParam = context.getParamMap();
				JSONObject jsonPost = new JSONObject(postData);
				String postParam = jsonPost.toString();
				String mySign = SignUtil.buildSign(getParam, postParam, GlobalConfig.SERVER_SECRET);
				String sign = context.getParameters("sign");
				if(!mySign.equals(sign))
				{
					result.setCode(ReturnCode.SIGN_ERROR);
					result.setMessage("服务签名错误");
					return result.toJsonString();
				}
			}
			
			///解密原子封装
			atom = context.getParameters("atom");
			if(StringUtil.isNullOrEmpty(atom))
			{
				if(needCheckAtom())
				{
					result.setCode(ReturnCode.CLIENT_REQUEST_LOST_NECESSARY_PARAMETER);
					result.setMessage("缺少原子封装");
					return result.toJsonString();
				}
			}
			else
			{
				atom = URLDecoder.decode(atom, "UTF-8");
				atom = new String(Base64.decodeBase64(atom), "UTF-8");
				///填充原子封装参数
				String[] items = atom.split("&");
				String[] keyval;
				for(String item : items)
				{
					keyval = item.split("=");
					if(keyval.length == 1)
						context.getParamMap().put(keyval[0], "");
					else if(keyval.length == 2)
						context.getParamMap().put(keyval[0], keyval[1]);
				}
			}
			
			///处理业务逻辑
			result = exec(httpContext);
			end = System.currentTimeMillis();
			String returnVal = result.toJsonString();
			
			///构建日志信息
			strLog.append((end - start) + " | ");
			strLog.append(clsName + " | ");
			strLog.append( result.getCode() + " | ");
			strLog.append("url=" + requestUri + " | ");
			strLog.append("atom=" + atom + " | ");
			strLog.append("postData=" + postData + "&response=" + returnVal);
			GlobalObject.INFO_LOG.info(strLog.toString());
			return returnVal;
		}
		catch(Exception e)
		{
			end = System.currentTimeMillis();
			strLog.append((end - start) + " | ");
			strLog.append(clsName + " | ");
			strLog.append(ReturnCode.SERVER_ERROR + " | ");
			strLog.append("url=" + requestUri + " | ");
			strLog.append("atom=" + atom + " | ");
			strLog.append("postData=" + postData);
			
			GlobalObject.ERROR_LOG.error(strLog.toString(), e);
			return ReturnCodeHelper.serverError().toJsonString();
		}
	}
	
	protected boolean needCheckAtom()
	{
		return false;
	}
	
	protected boolean needCheckSign()
	{
		return false;
	}
	
	protected abstract ResultValue exec(HttpRequestContext context) throws Exception;
}