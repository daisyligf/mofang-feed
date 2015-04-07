package com.mofang.feed.model;

import java.util.Map;

import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.util.MapDecorator;
import com.mofang.framework.data.mysql.core.annotation.ColumnName;
import com.mofang.framework.data.mysql.core.annotation.PrimaryKey;
import com.mofang.framework.data.mysql.core.annotation.TableName;

/**
 * 
 * @author zhaodx
 *
 */
@TableName(name = "feed_sys_role")
public class FeedSysRole
{
	@PrimaryKey
	@ColumnName(name = "role_id")
	private int roleId;
	@ColumnName(name = "role_name")
	private String roleName;
	@ColumnName(name = "color")
	private String color;
	@ColumnName(name = "icon")
	private String icon;
	@ColumnName(name = "privileges")
	private String privileges;
	@ColumnName(name = "create_time")
	private long createTime = System.currentTimeMillis();
	
	public FeedSysRole()
	{}
	
	public FeedSysRole(Map<String, String> map) throws Exception
	{
		try
		{
			MapDecorator decorator = new MapDecorator(map);
			this.roleId = decorator.optInt("role_id", 0);
			this.roleName = decorator.optString("role_name", "");
			this.color = decorator.optString("color", "");
			this.icon = decorator.optString("icon", "");
			this.privileges = decorator.optString("privileges", "");
			this.createTime = decorator.optLong("create_time", System.currentTimeMillis());
		}
		catch(Exception e)
		{
			throw e;
		}
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName == null ? "" : roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getColor() {
		return color == null ? "" : color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getIcon() {
		return icon == null ? "" : icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPrivileges() {
		return privileges == null ? "" : privileges;
	}

	public void setPrivileges(String privileges) {
		this.privileges = privileges;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	/**
	 * 将实体对象转换为redis的hashmap
	 * @return
	 */
	public Map<String, String> toMap()
	{
		try
		{
			MapDecorator decorator = new MapDecorator();
			decorator.put("role_id", roleId);
			decorator.put("role_name", roleName);
			decorator.put("color", color);
			decorator.put("icon", icon);
			decorator.put("privileges", privileges);
			decorator.put("create_time", createTime);
			return decorator.toMap();
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at FeedSysRole.toMap throw an error.", e);
			return null;
		}
	}
}