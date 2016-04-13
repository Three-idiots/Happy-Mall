package cn.edu.zzu.wemall.ui;

import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.net.NetChangePasswd;
import cn.edu.zzu.wemall.net.NetServerErr;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswd extends Activity {
	private EditText wemall_changepasswd_old, wemall_changepasswd_new,
			wemall_changepasswd_new_re;
	private ProgressBar wemall_changepasswd_loadingBar;
	private TextView wemall_changepasswd_button;
	private Handler handler = null;
	private int ChangePasswdtstate = -1;
	@SuppressWarnings("unused")
	private SharedPreferences userinfo;
	private String uid;
	private ViewGroup backbar;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_user_changepasswd);

		Bundle bundle = this.getIntent().getExtras();
		uid = bundle.getString("uid");
		wemall_changepasswd_old = (EditText) findViewById(R.id.wemall_changepasswd_old);
		wemall_changepasswd_new = (EditText) findViewById(R.id.wemall_changepasswd_new);
		wemall_changepasswd_new_re = (EditText) findViewById(R.id.wemall_changepasswd_new_re);
		wemall_changepasswd_loadingBar = (ProgressBar) findViewById(R.id.wemall_changepasswd_loadingBar);
		wemall_changepasswd_button = (TextView) findViewById(R.id.wemall_changepasswd_button);
		this.userinfo = getSharedPreferences("userinfo", 0);
		this.backbar = (ViewGroup) findViewById(R.id.title_left_layout_changepasswd);
		backbar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ChangePasswd.this.finish();
				// 定义退出当前Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});

		wemall_changepasswd_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 隐藏键盘
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				ChangePasswdCheck();
			}
		});
	}

	public void ChangePasswdCheck() {
		if (wemall_changepasswd_old.getText().toString().length() == 0) {
			Toast.makeText(this, "请输入当前密码", Toast.LENGTH_SHORT).show();
		} else if (wemall_changepasswd_new.getText().toString().length() < 6) {
			Toast.makeText(this, "新密码至少要六位额....", Toast.LENGTH_SHORT).show();
		} else if (!(wemall_changepasswd_new.getText().toString()
				.equals(wemall_changepasswd_new_re.getText().toString()))) {
			Toast.makeText(this, "两次输入的新密码好像不一样额....", Toast.LENGTH_SHORT)
					.show();
		} else if (wemall_changepasswd_new.getText().toString()
				.equals(wemall_changepasswd_new_re.getText().toString())) {
			wemall_changepasswd_loadingBar.setVisibility(View.VISIBLE);
			Changepasswd();
		}
	}

	@SuppressLint("HandlerLeak")
	public void Changepasswd() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			public void run() {

				// 提交数据到服务器/接收返回的结果
				try {
					ChangePasswdtstate = NetChangePasswd
							.getData("uid="+ uid+ "&old="
									+ wemall_changepasswd_old.getText()
											.toString()
									+ "&new="
									+ wemall_changepasswd_new.getText()
											.toString());
					// 发送消息，并把persons结合对象传递过去
					handler.sendEmptyMessage(0x11199);
				} catch (Exception e) {
					handler.sendEmptyMessage(0x11198);

				}
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
						wemall_changepasswd_loadingBar.setVisibility(View.GONE);
						result();
					}
					if (msg.what == 0x11198) {
						// 提醒用户连接服务器异常
						NetServerErr.ServerErrToast(ChangePasswd.this);
						wemall_changepasswd_loadingBar.setVisibility(View.GONE);
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void result() {

		if (ChangePasswdtstate == 0) {

			Toast.makeText(this, "错误的当前密码,无权设置新密码", Toast.LENGTH_SHORT).show();
		}
		if (ChangePasswdtstate == 1) {
			Toast.makeText(this, "修改密码成功,请使用新密码登录", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(ChangePasswd.this, MainUIMain.class);
			Bundle bundle = new Bundle();
			bundle.putString("result", "1");
			intent.putExtras(bundle);
			setResult(0x211, intent);
			finish();
		}
	}

}
