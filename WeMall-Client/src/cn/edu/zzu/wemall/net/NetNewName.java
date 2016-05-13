package cn.edu.zzu.wemall.net;

import cn.edu.zzu.wemall.config.MyConfig;

public class NetNewName {
	public static int getData(String param) {

		int state = 0;
		try {
			state = Integer.parseInt(HttpRequest.sendPost(MyConfig.SERVERADDRESS + "?tag=wemall_update_myname", param));
		} catch (Exception e) {
			return -1;
		}
		return state;
	}
}
