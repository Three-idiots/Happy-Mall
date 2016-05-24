package cn.edu.zzu.wemall.net;

import cn.edu.zzu.wemall.config.MyConfig;

/**
 * 
 * 
 * 
 * @author liudewei-zzu
 *返回修改密码的结果
 */
public class NetChangePasswd {
	public static int getData(final String param) throws Exception {
		
		int state = 0;
		try {
			state = Integer.parseInt(HttpRequest.sendPost(MyConfig.SERVERADDRESS+"?tag=wemall_update_passwd",param));
		} catch (Exception e) {
			throw e;
		}
		return state;
	}

}
