package com.mervynlam.downloadMineVideo.utils;

public class Constant {
	public static final int PER_PAGE_NUM = 22;

	public static final String HOST = "saolei.wang";
	public static final String STATUS_URL = "http://"+HOST+"/Video/Satus.asp?Id=";
	public static final String INFO_URL = "http://"+HOST+"/Player/Info.asp?Id=";
	public static final String[] VIDEO_LIST_URL = {"http://"+HOST+"/Video/My_Beg.asp?Id=",
												"http://"+HOST+"/Video/My_Int.asp?Id=",
												"http://"+HOST+"/Video/My_Exp.asp?Id="};
	public static final String[] PAGE_URL = {"http://"+HOST+"/Video//My_Beg.asp?Save=1&Page=",
											"http://"+HOST+"/Video//My_Int.asp?Save=1&Page=",
											"http://"+HOST+"/Video//My_Exp.asp?Save=1&Page="};
	public static final String SHOW_VIDEO_URL = "http://"+HOST+"/Video/Show.asp?Id=";
	public static final String DOWNLOAD_URL = "http://"+HOST+"/Video/Action/Download_Action.asp";
	
	public static final int DOWNLOAD_SUCCESS = 1;
	public static final int DOWNLOAD_EXIST = 0;
	public static final int DOWNLOAD_FAIL = -1;
}
