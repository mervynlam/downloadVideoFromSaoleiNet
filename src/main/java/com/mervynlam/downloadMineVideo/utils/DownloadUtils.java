package com.mervynlam.downloadMineVideo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.mervynlam.downloadMineVideo.entity.User;

public class DownloadUtils {
	
	private static Map<String, String> userCookies;		//玩家cookies
	private static Map<String, String> videoCookies;	//录像cookies
	
	private static List<String> failList = new ArrayList<String>();		//下载失败
	
	public static int level;		//选择的级别
	private static String[] levelName = {"beg", "int", "exp"};
	
	public static User getUserInfo(String id) {
		User user = new User(id);
		try {
			//姓名
			Document doc = Jsoup.connect(Constant.INFO_URL+id)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
					.get();
			user.setName(doc.getElementsByClass("sign").get(0).text().trim());
			//各等级录像数
			doc = Jsoup.connect(Constant.STATUS_URL+id)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
					.get();
			List<Element> counters = doc.getElementsByClass("counters");
			user.setBegNum(Integer.valueOf(counters.get(0).text().trim()));//初级
			user.setIntNum(Integer.valueOf(counters.get(1).text().trim()));//中级
			user.setExpNum(Integer.valueOf(counters.get(2).text().trim()));//高级
			user.setVideoNum(user.getBegNum()+user.getIntNum()+user.getExpNum());
		} catch (IOException e) {
			System.out.println("用户数据拉取失败");
//			e.printStackTrace();
		}
		return user;
	}
	
	public static void getVideiList(User user) {
		//获取总页数
		int videoNum = level==0?user.getBegNum():level==1?user.getIntNum():user.getExpNum();
		System.out.println("共"+videoNum+"个录像");
		int pageNum = (int) Math.ceil(videoNum*1.0/Constant.PER_PAGE_NUM);
		try {
			for (int i = 1; i <= pageNum; ++i) {
				System.out.println("获取第"+i+"页");
				Response response;
				if (i == 1) {
					//第一页，获取cookies
					response = Jsoup.connect(Constant.VIDEO_LIST_URL[level]+user.getId())
							.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
							.header("Accept-Encoding", "gzip, deflate")
							.header("Accept-Language", "zh-CN,zh;q=0.9")
							.header("Host", Constant.HOST)
							.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
							.timeout(5000)
							.execute();
					userCookies = response.cookies();
				} else {
					try {
						//第n页，不带userId，所以要放入cookies
						response = Jsoup.connect(Constant.PAGE_URL[level]+i)
								.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
								.header("Accept-Encoding", "gzip, deflate")
								.header("Accept-Language", "zh-CN,zh;q=0.9")
								.header("Host", Constant.HOST)
								.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
								.cookies(userCookies)
								.timeout(5000)
								.execute();
					} catch(Exception e) {
						System.out.println("获取第"+i+"页失败");
//						e.printStackTrace();
						i--;
						continue;
					}
				}
				try {
					//获取录像id
					Document doc = response.parse();
					List<Element> videoList = doc.select("a[class=Title]");
					int flag = Constant.DOWNLOAD_SUCCESS;
					for (Element elem : videoList) {
						try {
							String onclickStr = elem.attr("onclick");
							String videoId = onclickStr.substring(onclickStr.indexOf("=")+1, onclickStr.indexOf("&"));
							flag = downloadVideo(user, videoId);
							//如果录像已存在，则后面的都不在继续下载
							if (flag == Constant.DOWNLOAD_EXIST) {
								break;
							}
						} catch(Exception e) {
							System.out.println("录像id获取失败");
//							e.printStackTrace();
						}
					}
					if (flag == Constant.DOWNLOAD_EXIST) {
						break;
					}
				} catch(Exception e) {
					System.out.println("录像<a>标签列表获取失败");
//					e.printStackTrace();
				}
			}
			//打印下载失败的录像id和文件名
			if (failList.size() != 0) {
				System.out.println("下载失败录像"+failList.size()+"个：");
				for (String id : failList) {
					System.out.println("id:"+id);
				}
			}
			System.out.println("录像下载完成");
		} catch(Exception e) {
			System.out.println("获取录像列表失败");
//			e.printStackTrace();
		}
	}
	
	//下载成功1，下载到已存在的录像0，失败-1
	public static int downloadVideo(User user, String videoId) throws Exception {
		try {
			boolean flag = false;
			Response downloadResponse = null;
			int retryCount = 0;
			while (!flag) {
				try {
					//打开录像信息页，存cookies
					Response videoResponse = Jsoup.connect(Constant.SHOW_VIDEO_URL+videoId)
							.ignoreContentType(true)
							.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
							.header("Accept-Encoding", "gzip, deflate")
							.header("Accept-Language", "zh-CN,zh;q=0.9")
							.header("Host", Constant.HOST)
							.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
							.timeout(5000)
							.execute();
					videoCookies = videoResponse.cookies();
					
					//在通过cookies下载文件
					downloadResponse = Jsoup.connect(Constant.DOWNLOAD_URL)
							.ignoreContentType(true)
							.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
							.header("Accept-Encoding", "gzip, deflate")
							.header("Accept-Language", "zh-CN,zh;q=0.9")
							.header("Host", Constant.HOST)
							.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36")
							.timeout(5000)
							.cookies(videoCookies)
							.execute();
					flag = true;
				} catch (Exception e) {
					retryCount++;
					if (retryCount > 3) {
						throw e;
					}
					System.out.println("录像id"+videoId+"连接超时，重试第"+retryCount+"次");
				}
			}
			
			//获取文件名
			String disposition = downloadResponse.headers().get("content-disposition");
			String originFileName = disposition.substring(disposition.indexOf("=")+1);
			String fileName = originFileName.substring(0, originFileName.lastIndexOf(".")) + "_" + videoId + originFileName.substring(originFileName.lastIndexOf("."));
			System.out.println("下载"+fileName);
			//录像存放在 姓名id 文件夹下
			fileName = user.getName()+user.getId()+"\\"+levelName[level]+"\\"+fileName;
			File file = new File(fileName);
			//文件夹不存在则创建
			File dirFile = file.getParentFile();
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			//文件不存在则创建文件
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e1) {
					System.out.println("文件创建失败");
//					e1.printStackTrace();
					failList.add(videoId);
					return Constant.DOWNLOAD_FAIL;
				}
			} else {
				System.out.println("录像已存在，后面的将不再继续下载");
				return Constant.DOWNLOAD_EXIST;
			}
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(downloadResponse.bodyAsBytes());
			out.close();
		} catch (IOException e) {
			System.out.println("下载录像(id="+videoId+")失败");
//			e.printStackTrace();
			failList.add(videoId);
			return Constant.DOWNLOAD_FAIL;
		}
		return Constant.DOWNLOAD_SUCCESS;
	}
}
