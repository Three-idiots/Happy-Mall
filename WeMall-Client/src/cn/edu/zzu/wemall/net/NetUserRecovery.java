package cn.edu.zzu.wemall.net;

import cn.edu.zzu.wemall.config.MyConfig;

/*
 * Author Liudewei
 * 
 * 提交注册,返回int型找回结果 */
public class NetUserRecovery {
	public static int getData(final String param) {
		
		int state = 0;
		try {
			state = Integer.parseInt(HttpRequest.sendPost(MyConfig.SERVERADDRESS+"?tag=wemall_rec_passwd", param));
		} catch (Exception e) {
			return -1;
		}
		return state;
	}

}
