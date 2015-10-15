package com.mofang.feed.mysql;

import java.util.List;
import com.mofang.feed.model.external.Pair;

public interface UserTPCDao {

	public List<Pair<Long, Long>> getForumIdThreadIdPairList(long userId, int start, int end) throws Exception;
	
	public void deleteThreadAll(long userId) throws Exception;
	
	public void deleteThreadPostAll(long userId) throws Exception;
	
	public void deleteThreadCommentAll(long userId) throws Exception;
	
	public void deleteThreadRecommendAll(long userId) throws Exception;
	
	public void deleteThreadFavorateAll(long userId) throws Exception;
	
	public List<Pair<Long, Long>> getThreadIdPostIdPairList(long userId, int start, int end) throws Exception;
	
	public void deletePostAll(long userId) throws Exception;
	
	public void deletePostRecommendAll(long userId) throws Exception;
	
	public void updatePostThreadRepliesAll(long userId) throws Exception;
	
	public void deletePostCommentAll(long userId) throws Exception;
	
	public List<Object[]> getPostIdCommentIdPairList(long userId, int start, int end) throws Exception;
	
	public void deleteCommentAll(long userId) throws Exception;
	
	public void updateCommentThreadRepliesAll(long userId) throws Exception;
	
	public void updateCommentPostRepliesAll(long userId) throws Exception;
}
