package cn.zzu.edu.wemall.utils;

import java.io.File;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 *规范化数据类型
 * 工具集合类,包含各种程序中用到的静态方法
 * 
 * Edit By 幻舞奇影
 * 
 */
public class Utils {
	// 判断是否处于联网状态
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 删除指定路径的文件夹
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定路径文件夹及其所有内容
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	// 获取指定路径目录/文件的总大小(如目录包含目录下所有文件)
	public static long getTotalSizeOfFilesInDir(String path) {
		File file = new File(path);
		if (file.isFile())
			return file.length();
		final File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (final File child : children)
				total += getTotalSizeOfFilesInDir(child.getPath());
		return total;
	}

	// 大小格式化工具,传入long型值
	public static String FomatFilesize(long size) {
		/**
		 * 返回byte的数据大小对应的文本
		 * 
		 * @param size
		 * @return
		 */
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "B";
		} else if (size < 1024 * 1024) {
			float kbsize = size / 1024f;
			return formater.format(kbsize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mbsize = size / 1024f / 1024f;
			return formater.format(mbsize) + "MB";
		} else if (size < 1024 * 1024 * 1024 * 1024) {
			float gbsize = size / 1024f / 1024f / 1024f;
			return formater.format(gbsize) + "GB";
		} else {
			return "size: error";

		}
	}

	// list转json字符串
	public static String ArrayListToJsonString(
			ArrayList<HashMap<String, Object>> order) {
		JSONArray json = (JSONArray) JSONArray.toJSON(order);
		return json.toString();
	}

	// json字符串转arraylist
	public static ArrayList<HashMap<String, String>> JsonStringToArrayList(
			String data) {
		JSONObject jsonobject = (JSONObject) JSONObject.parse("{order:" + data
				+ "}");
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (Object item : jsonobject.getJSONArray("order")) {
			HashMap<String, String> thisitem = new HashMap<String, String>();
			thisitem.put("name", ((JSONObject) item).get("name").toString());
			thisitem.put("num", ((JSONObject) item).get("num").toString());
			thisitem.put("price", ((JSONObject) item).get("price").toString());
			thisitem.put("name", ((JSONObject) item).get("name").toString());
			list.add(thisitem);
		}
		return list;
	}

	// MD5加密
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	// 获取6位随机26字母
	public static String getRandomSix() {
		Random rd = new Random();
		String n = "";
		int getNum;
		do {
			getNum = Math.abs(rd.nextInt()) % 26 + 97;// 产生字母a-z的随机数
			char num1 = (char) getNum;
			String dn = Character.toString(num1);
			n += dn;
		} while (n.length() < 6);
		return n;
	}

	// 获取字符串的base值,用于网络传输避免乱码
	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return (new Decoder.BASE64Encoder()).encode(s.getBytes());
	}
	//防止快速多次点击
	public static boolean isFastDoubleClick() {
		long lastClickTime = 0;
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 500) {   
            return true;   
        }   
        lastClickTime = time;   
        return false;   
    }

}
