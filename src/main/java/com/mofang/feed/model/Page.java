package com.mofang.feed.model;

import java.util.List;

/**
 * 
 * @author zhaodx
 *
 */
public class Page<T>
{
	private long total = 0L;
	private List<T> list;
	
	public Page(long total, List<T> list)
	{
		this.total = total;
		this.list = list;
	}

	public long getTotal() {
		return total;
	}

	public List<T> getList() {
		return list;
	}
}