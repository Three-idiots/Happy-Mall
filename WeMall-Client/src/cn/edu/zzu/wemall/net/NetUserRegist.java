package cn.edu.zzu.wemall.net;

import cn.edu.zzu.wemall.config.MyConfig;

/*
 * Author Liudewei
 * 
 * 提交注册,返回int型注册结果 */
public class NetUserRegist {
	public static int getData(final String param) {
		
		int state = 0;
		try {
			state = Integer.parseInt(HttpRequest.sendPost(MyConfig.SERVERADDRESS+"?tag=wemall_user_regist", param));
		} catch (Exception e) {
			return -1;
		}
		return state;
	}

}
