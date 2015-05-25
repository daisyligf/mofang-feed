package com.mofang.feed.model.external;

/**
 * 
 * @author zhaodx
 *
 */
public class Video
{
	private long videoId;
	private String thumbnail;
	private int duration = 0;

	public long getVideoId() {
		return videoId;
	}

	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}