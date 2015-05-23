package com.mofang.feed.logic.admin.impl;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mofang.feed.global.ResultValue;
import com.mofang.feed.global.ReturnCode;
import com.mofang.feed.global.ReturnMessage;
import com.mofang.feed.global.common.ModeratorApplyStatus;
import com.mofang.feed.logic.admin.FeedModeratorApplyLogic;
import com.mofang.feed.model.FeedModeratorApply;
import com.mofang.feed.model.FeedSysUserRole;
import com.mofang.feed.model.ModeratorApplyCondition;
import com.mofang.feed.model.Page;
import com.mofang.feed.service.FeedAdminUserService;
import com.mofang.feed.service.FeedModeratorApplyService;
import com.mofang.feed.service.FeedPostService;
import com.mofang.feed.service.FeedSysUserRoleService;
import com.mofang.feed.service.FeedThreadService;
import com.mofang.feed.service.impl.FeedAdminUserServiceImpl;
import com.mofang.feed.service.impl.FeedModeratorApplyServiceImpl;
import com.mofang.feed.service.impl.FeedPostServiceImpl;
import com.mofang.feed.service.impl.FeedSysUserRoleServiceImpl;
import com.mofang.feed.service.impl.FeedThreadServiceImpl;

/**
 * 
 * @author zhaodx
 *
 */
public class FeedModeratorApplyLogicImpl implements FeedModeratorApplyLogic
{
	private final static FeedModeratorApplyLogicImpl LOGIC = new FeedModeratorApplyLogicImpl();
	private FeedAdminUserService adminService = FeedAdminUserServiceImpl.getInstance();
	private FeedSysUserRoleService userRoleService = FeedSysUserRoleServiceImpl.getInstance();
	private FeedModeratorApplyService applyService = FeedModeratorApplyServiceImpl.getInstance();
	private FeedThreadService threadService = FeedThreadServiceImpl.getInstance();
	private FeedPostService postService = FeedPostServiceImpl.getInstance();
	
	private FeedModeratorApplyLogicImpl()
	{}
	
	public static FeedModeratorApplyLogicImpl getInstance()
	{
		return LOGIC;
	}

	@Override
	public ResultValue audit(int applyId, int status, int roleId, long operatorId) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			///申请有效性检查
			FeedModeratorApply applyInfo = applyService.getInfo(applyId);
			if(null == applyInfo)
			{
				result.setCode(ReturnCode.MODERATOR_APPLY_NOT_EXISTS);
				result.setMessage(ReturnMessage.MODERATOR_APPLY_NOT_EXISTS);
				return result;
			}
			
			///判断是否满足申请条件
			ModeratorApplyCondition condition = applyService.checkCondition(applyInfo.getUserId(), applyInfo.getForumId(), true);
			boolean isPass = condition.isFollowForumIsOK() && condition.isThreadsIsOK() && condition.isTopEliteCountIsOK() && condition.isTimeIntervalIsOK();
			if(!isPass)
			{
				result.setCode(ReturnCode.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
				result.setMessage(ReturnMessage.MODERATOR_APPLY_CONDITION_INSUFFICIENT);
				return result;
			}
			
			///判断版主是否满额
			boolean isFull = userRoleService.isFull(applyInfo.getForumId());
			if(isFull)
			{
				result.setCode(ReturnCode.FORUM_MODERATOR_IS_FULL);
				result.setMessage(ReturnMessage.FORUM_MODERATOR_IS_FULL);
				return result;
			}
			
			///权限检查
			boolean hasPrivilege = adminService.exists(operatorId);
			if(!hasPrivilege)
			{
				result.setCode(ReturnCode.INSUFFICIENT_PERMISSIONS);
				result.setMessage(ReturnMessage.INSUFFICIENT_PERMISSIONS);
				return result;
			}
			
			///审核版主申请
			applyService.audit(applyId, status);
			///如果审核通过，则需要创建系统角色(向feed_sys_user_role添加一条记录)
			if(status == ModeratorApplyStatus.PASS)
			{
				FeedSysUserRole userRoleInfo = new FeedSysUserRole();
				userRoleInfo.setForumId(applyInfo.getForumId());
				userRoleInfo.setUserId(applyInfo.getUserId());
				userRoleInfo.setRoleId(roleId);
				userRoleService.save(userRoleInfo);
			}
			
			///返回结果
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModeratorApplyLogicImpl.audit throw an error.", e);
		}
	}

	@Override
	public ResultValue getList(int pageNum, int pageSize) throws Exception
	{
		try
		{
			ResultValue result = new ResultValue();
			JSONObject data = new JSONObject();
			long total = 0;
			JSONArray arrayApplies =new JSONArray();
			Page<FeedModeratorApply> page = applyService.getList(pageNum, pageSize);
			if(null != page)
			{
				total = page.getTotal();
				List<FeedModeratorApply> applies = page.getList();
				if(null != applies)
				{
					JSONObject jsonApply = null;
					JSONObject jsonForum = null;
					JSONObject jsonUser = null;
					for(FeedModeratorApply applyInfo : applies)
					{
						jsonApply = new JSONObject();
						jsonApply.put("apply_id", applyInfo.getApplyId());
						jsonApply.put("reason", applyInfo.getReason());
						jsonApply.put("status", applyInfo.getStatus());
						jsonApply.put("create_time", applyInfo.getCreateTime());
						
						jsonForum = new JSONObject();
						jsonForum.put("fid", applyInfo.getForumId());
						jsonForum.put("name", applyInfo.getForumName());
						
						jsonUser = new JSONObject();
						jsonUser.put("user_id", applyInfo.getUserId());
						jsonUser.put("nickname", applyInfo.getNickName());
						jsonUser.put("contact_qq", applyInfo.getContactQQ());
						jsonUser.put("contact_mobile", applyInfo.getContactMobile());
						///获取用户发帖总数
						long threads = threadService.getUserThreadCount(applyInfo.getUserId());
						///获取用户回帖总数
						long replies = postService.getUserReplyCount(applyInfo.getUserId());
						jsonUser.put("threads", threads);
						jsonUser.put("replies", replies);
						jsonApply.put("forum", jsonForum);
						jsonApply.put("user", jsonUser);
						arrayApplies.put(jsonApply);
					}
				}
			}
			data.put("total", total);
			data.put("list", arrayApplies);
			result.setCode(ReturnCode.SUCCESS);
			result.setMessage(ReturnMessage.SUCCESS);
			result.setData(data);
			return result;
		}
		catch(Exception e)
		{
			throw new Exception("at FeedModeratorApplyLogicImpl.getList throw an error.", e);
		}
	}
}