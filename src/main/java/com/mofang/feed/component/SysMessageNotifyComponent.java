package com.mofang.feed.component;

import java.util.ArrayList;
import java.util.List;

import com.mofang.feed.global.GlobalConfig;
import com.mofang.feed.global.GlobalObject;
import com.mofang.feed.model.external.SysMessageNotify;
import com.mofang.feed.model.external.User;
import com.mofang.framework.util.StringUtil;

/**
 * 
 * @author zhaodx
 *
 */
public class SysMessageNotifyComponent
{
	/**
	 * 管理员删除主题
	 * @param userId 发布主题的用户ID
	 * @param subject 主题标题
	 * @param reason 删除理由
	 */
	public static void deleteThread(final long userId, final String subject, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run()
				{
					String delReason = reason;
					if(StringUtil.isNullOrEmpty(reason))
						delReason = "亲爱的玩家，非常抱歉您的帖子涉嫌恶意灌水已被管理员删除，建议您发布丰富有趣的内容，成为加加明星玩家！";
					
					String category = "del_post";
					String title = "您的帖子" + subject + "已被删除";
					String detail = "您的帖子" + subject + "已被删除。删除理由:" + delReason;
					pushNotify(userId, category, title, detail, null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.deleteThread throw an error.", e);
		}
	}
	
	/**
	 * 管理员删除回复通知
	 * @param operatorId 操作人ID
	 * @param userId  发布回复的用户ID
	 * @param subject  主题标题
	 * @param content  回复内容
	 * @param reason  删除理由
	 */
	public static void deletePost(final long operatorId, final long userId, final String subject, final String content, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run()
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "del_post_reply";
					String title = "您在主题" + subject + "的回复" + content + "被删除";
					StringBuilder detail = new StringBuilder("您的主题").append(subject).append("的回复").append(content).append("被").append(operatorName).append("删除");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append(", 理由：").append(reason);
					
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.deletePost throw an error.", e);
		}
	}
	
	/**
	 * 设置主题置顶
	 * @param operatorId  操作人ID
	 * @param userId  发布主题的用户ID
	 * @param forumId 版块ID
	 * @param subject  主题标题
	 * @param reason  置顶理由
	 */
	public static void setTopThread(final long operatorId, final long userId, final long forumId, final String subject, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "post_top";
					String title = "您的主题" + subject + "被设为置顶";
					StringBuilder detail = new StringBuilder("您的主题").append(subject).append("被").append(operatorName).append("置顶");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append(", 理由：").append(reason);
					
					String clickAction = GlobalConfig.FORUM_DETAIL_URL + "?fid=" + forumId;
					pushNotify(userId, category, title, detail.toString(), clickAction);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.setTopThread throw an error.", e);
		}
	}
	
	/**
	 * 取消主题置顶
	 * @param operatorId  操作人ID
	 * @param userId  发布主题的用户ID
	 * @param subject  主题标题
	 * @param reason  取消理由
	 */
	public static void cancelTopThread(final long operatorId, final long userId, final String subject, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "post_cancel_top";
					String title = "您的主题" + subject + "被取消置顶";
					StringBuilder detail = new StringBuilder("您的主题").append(subject).append("被").append(operatorName).append("取消置顶");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.cancelTopThread throw an error.", e);
		}
	}
	
	/**
	 * 设置精华主题
	 * @param operatorId  操作人ID
	 * @param userId  发布主题的用户ID
	 * @param subject  主题标题
	 * @param reason  操作理由
	 */
	public static void setEliteThread(final long operatorId, final long userId,  final String subject, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "post_elite";
					String title = "您的主题" + subject + "被" + operatorName + "设为精华";
					StringBuilder detail = new StringBuilder("您的主题").append(subject).append("被").append(operatorName).append("设为精华");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.setEliteThread throw an error.", e);
		}
	}
	
	/**
	 * 取消精华主题
	 * @param operatorId  操作人ID
	 * @param userId  发布主题的用户ID
	 * @param subject  主题标题
	 * @param reason  操作理由
	 */
	public static void cancelEliteThread(final long operatorId, final long userId,  final String subject, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "post_cancel_elite";
					String title = "您的主题" + subject + "被" + operatorName + "取消精华";
					StringBuilder detail = new StringBuilder("您的主题").append(subject).append("被").append(operatorName).append("取消精华");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.cancelEliteThread throw an error.", e);
		}
	}
	
	/**
	 * 设置主题标红
	 * @param operatorId  操作人ID
	 * @param userId  发布主题的用户ID
	 * @param subject  主题标题
	 * @param reason  操作理由
	 */
	public static void setMarkThread(final long operatorId, final long userId,  final String subject, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "post_mark";
					String title = "您的主题" + subject + "被" + operatorName + "设置标红";
					StringBuilder detail = new StringBuilder("您的主题").append(subject).append("被").append(operatorName).append("设置标红");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.setMarkThread throw an error.", e);
		}
	}
	
	/**
	 * 取消主题标红
	 * @param operatorId  操作人ID
	 * @param userId  发布主题的用户ID
	 * @param subject  主题标题
	 * @param reason  操作理由
	 */
	public static void cancelMarkThread(final long operatorId, final long userId,  final String subject, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "post_cancel_mark";
					String title = "您的主题" + subject + "被" + operatorName + "取消标红";
					StringBuilder detail = new StringBuilder("您的主题").append(subject).append("被").append(operatorName).append("取消标红");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.cancelMarkThread throw an error.", e);
		}
	}
	
	/**
	 * 审核吧主申请
	 * @param forumName  版块名称
	 * @param userId  申请的用户ID
	 * @param isPass  是否通过审核
	 * @param reason  操作理由
	 */
	public static void auditModeratorApply(final String forumName, final long userId, final boolean isPass, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String category = "moderator_audit";
					String title = "非常抱歉，您未通过" + forumName + "吧实习吧主申请";
					StringBuilder detail = new StringBuilder("非常抱歉，您未通过").append(forumName).append("吧实习吧主申请");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，失败原因：").append(reason);
					
					if (isPass)
					{
						title = "祝贺您已成功申请为" + forumName + "吧实习吧主，期待您的良好表现。";
						detail = new StringBuilder("祝贺您已成功申请为").append(forumName).append("吧实习吧主，期待您的良好表现。");
					} 
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.auditModeratorApply throw an error.", e);
		}
	}
	
	/**
	 * 删除用户角色
	 * @param operatorId 操作人ID
	 * @param roleName  角色名称
	 * @param userId  用户ID
	 * @param reason  操作理由
	 */
	public static void deleteUserRole(final long operatorId, final String roleName, final long userId, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "role_delete";
					String title = "您已被" + operatorName + "移出" + roleName + "，目前所在普通用户";
					StringBuilder detail = new StringBuilder("您已被").append(operatorName).append("移出").append(roleName).append("，目前所在普通用户");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.deleteUserRole throw an error.", e);
		}
	}
	
	/**
	 * 变更用户角色
	 * @param operatorId  操作人ID
	 * @param oriRoleName  原角色
	 * @param curRoleName  当前角色
	 * @param userId  用户ID
	 * @param reason  操作理由
	 */
	public static void changeUserRole(final long operatorId, final String oriRoleName, final String curRoleName, final long userId, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "role_change";
					String title = "您的管理组已被" + operatorName + "从" + oriRoleName + "变更为" + curRoleName;
					StringBuilder detail = new StringBuilder("您的管理组已被").append(operatorName).append("从").append(oriRoleName).append("变更为").append(curRoleName);
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.changeUserRole throw an error.", e);
		}
	}
	
	/**
	 * 禁言用户
	 * @param operatorId  操作人ID
	 * @param forumName  版块名称
	 * @param userId  用户ID
	 * @param reason  操作理由
	 */
	public static void forbidUser(final long operatorId, final String forumName, final long userId, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "forbid_user";
					String title = "您已被" + operatorName + "禁止在" + forumName + "吧发言";
					StringBuilder detail = new StringBuilder("您已被").append(operatorName).append("禁止在").append(forumName).append("吧发言");
					if (!StringUtil.isNullOrEmpty(reason)) 
						detail.append("，理由：").append(reason);
					
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.forbidUser throw an error.", e);
		}
	}
	
	/**
	 * 解禁用户
	 * @param operatorId  操作人ID
	 * @param userId  用户ID
	 * @param reason  操作理由
	 */
	public static void relieveUser(final long operatorId, final long userId, final String reason)
	{
		try
		{
			Runnable pushTask = new Runnable()
			{
				@Override
				public void run() 
				{
					String operatorName = "";
					User userInfo = UserComponent.getInfo(operatorId);
					if(null != userInfo)
						operatorName = userInfo.getNickName();
					
					String category = "relieve_user";
					String title = "您已被" + operatorName + "解除禁言";
					StringBuilder detail = new StringBuilder("您已被").append(operatorName).append("解除禁言");
					if (!StringUtil.isNullOrEmpty(reason))
						detail.append("，理由：").append(reason).append("。希望您在日后能够遵守本吧规则，祝您玩的愉快。");
					
					pushNotify(userId, category, title, detail.toString(), null);
				}
			};
			GlobalObject.ASYN_EXECUTOR.execute(pushTask);
		}
		catch(Exception e)
		{
			GlobalObject.ERROR_LOG.error("at SysMessageNotifyComponent.relieveUser throw an error.", e);
		}
	}
	
	/**
	 * 发送系统通知
	 * @param userId  通知接收用户
	 * @param category  通知分类(高版本中已经废弃)
	 * @param title  通知标题
	 * @param detail  通知内容
	 * @param clickAction  连接地址(linkurl)
	 */
	private static void pushNotify(final long userId, final String category, final  String title, final String detail, final String clickAction)
	{
		List<Long> uidList = new ArrayList<Long>();
		uidList.add(userId);
		SysMessageNotify notify = new SysMessageNotify();
		notify.setMessageCategory(category);
		notify.setUidList(uidList);
		notify.setTitle(title);
		notify.setDetail(detail);
		notify.setClickAction(clickAction);
		HttpComponent.pushSysMessageNotify(notify);
	}
}