package cn.edu.zzu.wemall.net;

import android.content.Context;
import android.widget.Toast;

public class NetServerErr {
	public  static void ServerErrToast(Context context){
		Toast.makeText(context, "服务器异常,请稍候重试", Toast.LENGTH_SHORT).show();
	}
}
