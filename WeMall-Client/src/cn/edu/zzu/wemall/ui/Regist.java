package cn.edu.zzu.wemall.ui;

import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.net.NetUserRegist;
import cn.zzu.edu.wemall.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Regist extends Activity {
	private ViewGroup backbar;
	private int registstate = -1;
	private TextView wemall_phonenumber, wemall_register_name,
			wemall_register_button, wemall_agree_details;
	private EditText wemall_register_password,
			wemall_register_confirm_password;
	private CheckBox agree;
	private Handler handler = null;
	private ProgressBar registloadingBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_user_register);
		Bundle bundle = this.getIntent().getExtras();
		backbar = (ViewGroup) findViewById(R.id.title_left_layout_regist);
		wemall_phonenumber = (TextView) findViewById(R.id.wemall_register_phonenum);
		wemall_register_name = (EditText) findViewById(R.id.wemall_register_name);
		wemall_register_password = (EditText) findViewById(R.id.wemall_register_password);
		wemall_register_confirm_password = (EditText) findViewById(R.id.wemall_register_confirm_password);
		wemall_register_button = (TextView) findViewById(R.id.wemall_register_button);
		wemall_phonenumber.setText(bundle.getString("phone"));
		registloadingBar = (ProgressBar) findViewById(R.id.registloadingBar);
		agree = (CheckBox) findViewById(R.id.wemall_is_agree);
		this.wemall_agree_details = (TextView) findViewById(R.id.wemall_agree_details);
		wemall_agree_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Regist.this, Gpl.class);
				startActivity(intent);
				overridePendingTransition(R.anim.wemall_slide_in_right,
						R.anim.wemall_slide_out_left);
			}
		});
		backbar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Regist.this.finish();
				// 定义退出当前Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});
		wemall_register_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 隐藏键盘
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				checkregist();
			}
		});
	}

	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.wemall_slide_in_left,
				R.anim.wemall_slide_out_right);
	}

	public void checkregist() {
		if (wemall_register_name.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "用户名为空,请填写", Toast.LENGTH_SHORT).show();

		} else if (wemall_register_password.getText().toString().length() == 0) {
			Toast.makeText(this, "密码呢?,切莫逗我玩", Toast.LENGTH_SHORT).show();
		} else if (wemall_register_password.getText().toString().length() < 6) {
			Toast.makeText(this, "密码有点短额...", Toast.LENGTH_SHORT).show();
		} else if (!(wemall_register_password.getText().toString()
				.equals(wemall_register_confirm_password.getText().toString()))) {
			Toast.makeText(this, "两次输入的密码好像不一样!", Toast.LENGTH_SHORT).show();
		} else if (!(agree.isChecked())) {
			Toast.makeText(this, "您必须同意注册条款才能继续,不是吗？", Toast.LENGTH_SHORT)
					.show();
		} else {
			registloadingBar.setVisibility(View.VISIBLE);
			regist();
		}

	}

	public void regist() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				// xmlwebData解析网络中xml中的数据
				// base64码中会有可能出现+号，特别注意
				/*
				 * 用户名涉及到汉字, 使用Base64编码解决乱码问题，不过产生了下面的问题
				 * 注解2：坑爹的base64加密会产生+号，导致post参数字符串截断，再使用urlencoder转换一下
				 * 注解3：下版本去除base64，直接url编码，因需要同步更新服务端，本版本暂不采用
				 */
				registstate = NetUserRegist.getData("phone="
						+ wemall_phonenumber.getText().toString()
						+ "&name="
						+java.net.URLEncoder.encode(Utils.getBASE64(wemall_register_name.getText()
								.toString()))
						+ "&passwd="
						+ wemall_register_password.getText().toString());
				// 发送消息，并把persons结合对象传递过去
				handler.sendEmptyMessage(0x11099);
			}
		};

		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x11099) {
						// 下一步给ListView绑定数据
						registloadingBar.setVisibility(View.GONE);
						registresult();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registresult() {

		if (registstate == 0) {
			finish();
			Toast.makeText(this, "账户已存在,请登录或者找回密码", Toast.LENGTH_LONG).show();
		}
		if (registstate == -1) {

			Toast.makeText(this, "链接服务器异常,请稍候重试", Toast.LENGTH_LONG).show();
		}
		if (registstate == 1) {
			finish();
			Toast.makeText(this, "注册成功,请登录", Toast.LENGTH_LONG).show();
		}
	}
}
