package cn.edu.zzu.wemall.config;

/**
 * @author liudewei-zzu
 * 配置类
 * 注意:请首先集成Api到Wemall服务器
 */
public class MyConfig {
	// 此处配置服务器地址注意需要斜杠，一般情况下您只需要修改下面的这一个地址///////////////////////////////////////////////////////////////////////
	public static final String SERVERADDRESSBASE= "http://www.uaide.net/wemall/" ;
	//////////////////////////////////////////接口文件/////////////////////////////////////////////////////
	public static final String SERVERADDRESS= SERVERADDRESSBASE+"/Api/client.php" ;
	////此处配置User_Agent,服务端接口与此处要保持一致,否则服务器拒绝响应请求////////////////////////////////////
	public static final String ClientUserAgent="WeMall_Client";	
}
