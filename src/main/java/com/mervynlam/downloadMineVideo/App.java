package com.mervynlam.downloadMineVideo;

import java.util.Scanner;

import com.mervynlam.downloadMineVideo.entity.User;
import com.mervynlam.downloadMineVideo.utils.DownloadUtils;

public class App {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.print("输入id：");
		String id = sc.next();
		do {
			System.out.println("级别：0初级，1中级，2高级");
			DownloadUtils.level = sc.nextInt();
		} while(DownloadUtils.level > 2 || DownloadUtils.level < 0);
		User user = DownloadUtils.getUserInfo(id);
		DownloadUtils.getVideiList(user);
	}
}
