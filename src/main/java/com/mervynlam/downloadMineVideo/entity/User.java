package com.mervynlam.downloadMineVideo.entity;

public class User {
	private String id;
	private String name;
	private int begNum;
	private int intNum;
	private int expNum;
	private int videoNum;
	
	public int getVideoNum() {
		return videoNum;
	}

	public void setVideoNum(int videoNum) {
		this.videoNum = videoNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getBegNum() {
		return begNum;
	}
	public void setBegNum(int begNum) {
		this.begNum = begNum;
	}
	public int getIntNum() {
		return intNum;
	}
	public void setIntNum(int intNum) {
		this.intNum = intNum;
	}
	public int getExpNum() {
		return expNum;
	}
	public void setExpNum(int expNum) {
		this.expNum = expNum;
	}
	
}
