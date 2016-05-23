package cn.edu.zzu.wemall.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.database.SQLProcess;
import cn.edu.zzu.wemall.ui.MainUIMain;
import cn.smssdk.SMSSDK;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 闪屏
 * 
 * @author liudewei
 *
 */
@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {
	private Handler mMainHandler = new Handler() {

		public void handleMessage(Message msg) {
			Intent intent = new Intent(Intent.ACTION_MAIN);

			PackageInfo info;
			try {
				info = getPackageManager().getPackageInfo("cn.edu.zzu.wemall",
						0);
				int currentVersion = info.versionCode;
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(SplashActivity.this);
				int lastVersion = prefs.getInt("VERSION_KEY", 0);
				if (currentVersion > lastVersion) {
					prefs.edit().putInt("VERSION_KEY", currentVersion).commit();
					intent.setClass(getApplication(), WelcomeMain.class);
				} else {

					intent.setClass(getApplication(), MainUIMain.class);
				}

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			finish();

		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wemall_splash);
		@SuppressWarnings("unused")
		SQLProcess chushihuaProcess=new SQLProcess(this);//初始化数据库,防止FC
		// ////////////////////初始化第三方SDK组件///////////////////////////////////
		//短信验证SDK
		MobclickAgent.openActivityDurationTrack(false);
		mMainHandler.sendEmptyMessageDelayed(0, 2000);
		SMSSDK.initSDK(this, "459a9a08a663", "a584570646bb76ff799b2388c26b26e9");
		// ///////////////////////////////////////////////////////////////////////
	}

	public void onBackPressed() {
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("闪屏"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("闪屏"); // 保证 onPageEnd 在onPause // 之前调用,因为
										// onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}