package cn.edu.zzu.wemall.ui;

import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.net.NetUserRecovery;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecoveryPasswd extends Activity {
	private EditText wemall_recoverypasswd_new, wemall_recoverypasswd_new_re;
	private ProgressBar wemall_recoverypasswd_loadingBar;
	private TextView wemall_recoverypasswd_button;
	private Handler handler = null;
	private int RecoveryPasswdtstate = -1;
	private String phone;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_user_recoverypasswd);

		Bundle bundle = this.getIntent().getExtras();
		phone = bundle.getString("phone");

		wemall_recoverypasswd_new = (EditText) findViewById(R.id.wemall_recoverypasswd_new);
		wemall_recoverypasswd_new_re = (EditText) findViewById(R.id.wemall_recoverypasswd_new_re);
		wemall_recoverypasswd_loadingBar = (ProgressBar) findViewById(R.id.wemall_recoverypasswd_loadingBar);
		wemall_recoverypasswd_button = (TextView) findViewById(R.id.wemall_recoverypasswd_button);
		wemall_recoverypasswd_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 隐藏键盘
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				RecoveryPasswdCheck();
			}
		});
	}

	public void RecoveryPasswdCheck() {
		if (wemall_recoverypasswd_new.getText().toString().length() < 6) {
			Toast.makeText(this, "新密码至少要六位额....", Toast.LENGTH_SHORT).show();
		} else if (!(wemall_recoverypasswd_new.getText().toString()
				.equals(wemall_recoverypasswd_new_re.getText().toString()))) {
			Toast.makeText(this, "两次输入的新密码好像不一样额....", Toast.LENGTH_SHORT)
					.show();
		} else if (wemall_recoverypasswd_new.getText().toString()
				.equals(wemall_recoverypasswd_new_re.getText().toString())) {
			wemall_recoverypasswd_loadingBar.setVisibility(View.VISIBLE);
			reapasswd();
		}
	}

	@SuppressLint("HandlerLeak")
	public void reapasswd() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			public void run() {

				// xmlwebData解析网络中xml中的数据
				RecoveryPasswdtstate = NetUserRecovery
						.getData("phone="
								+ phone
								+ "&new="
								+ wemall_recoverypasswd_new.getText()
										.toString());
				// 发送消息，并把persons结合对象传递过去
				handler.sendEmptyMessage(0x11199);
			}
		};

		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x11199) {
						// 下一步给ListView绑定数据
						wemall_recoverypasswd_loadingBar
								.setVisibility(View.GONE);
						result();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void result() {

		if (RecoveryPasswdtstate == 0) {

			Toast.makeText(this, "当前手机号未注册,请注册后登录", Toast.LENGTH_SHORT).show();
		}
		if (RecoveryPasswdtstate == -1) {

			Toast.makeText(this, "链接服务器异常,请稍候重试", Toast.LENGTH_SHORT).show();
		}
		if (RecoveryPasswdtstate == 1) {
			Intent intent = new Intent(RecoveryPasswd.this, MainUIMain.class);
			Bundle bundle = new Bundle();
			bundle.putString("result", "1");
			bundle.putString("phone",phone);
			intent.putExtras(bundle);
			setResult(0x212, intent);
			Toast.makeText(this, "恢复密码成功,请使用新密码登录", Toast.LENGTH_LONG).show();
			finish();
		}
	}

}
