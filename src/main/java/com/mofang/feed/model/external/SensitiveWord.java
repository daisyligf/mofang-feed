package com.mofang.feed.model.external;

/**
 * 
 * @author zhaodx
 *
 */
public class SensitiveWord 
{
	private int errorNum = 0;
	private String outMark;
	private String out;
	private boolean fatal = false;
	private String tips;

	public int getErrorNum()
	{
		return errorNum;
	}

	public void setErrorNum(int errorNum)
	{
		this.errorNum = errorNum;
	}

	public String getOutMark()
	{
		return outMark;
	}

	public void setOutMark(String outMark)
	{
		this.outMark = outMark;
	}

	public String getOut()
	{
		return out;
	}

	public void setOut(String out)
	{
		this.out = out;
	}

	public boolean isFatal()
	{
		return fatal;
	}

	public void setFatal(boolean fatal) 
	{
		this.fatal = fatal;
	}

	public String getTips()
	{
		return tips;
	}

	public void setTips(String tips)
	{
		this.tips = tips;
	}
}