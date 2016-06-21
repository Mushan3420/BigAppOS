package net.ag.lib.gallery.util;

import java.io.Serializable;

/***
 * 媒体信息
 * 
 * @author AG
 *
 */
public class MediaInfo implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2368954820504205134L;
	public int id;
	/***
	 * 0显示网络，1显示本地
	 */
	public int type = 1;
	public String url;
	public String filePath = "";
	public String mimeType = "";
	public String title = "";
	public String content = "";
	public int duration;
	public int size;
	public boolean select;

	public MediaInfo() {
	}

	public MediaInfo(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public String toString() {
		return "VideoInfo [id=" + id + ", filePath=" + filePath + ", mimeType="
				+ mimeType + ", title=" + title + ", duration=" + duration
				+ ", size=" + size + ", select=" + select + "]";
	}

	@Override
	public boolean equals(Object o) {
		try {
			MediaInfo videoInfo = (MediaInfo) o;
			return type == 0 ? url.equals(videoInfo.url) : filePath
					.equals(videoInfo.filePath);
		} catch (Exception e) {
			return super.equals(o);
		}
	}

}
