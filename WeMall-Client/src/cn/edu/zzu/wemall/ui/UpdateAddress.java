package cn.edu.zzu.wemall.ui;

import java.net.URLEncoder;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.net.NetNewAddress;
import cn.zzu.edu.wemall.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateAddress extends Activity implements AMapLocationListener {
	private ViewGroup backbar;
	private String uid;
	private boolean flag = false;
	private int state = -1;
	private EditText newaddress;
	private ViewGroup addressbutton;
	private ProgressBar addBar;
	private Handler handler = null;
	private boolean manual = false;// 判断是否是手动获取地址,如果是在没有主动获取地址的情况下点击了使用网络地址,则在获取到地之后主动覆盖到编辑框
	private String netaddress = null; // 通过高德地图接口定位获取当前地址...么么哒
	private LocationManagerProxy mLocationManagerProxy;
	private ImageView netaddressbutton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_user_newaddress);
		Bundle bundle = this.getIntent().getExtras();
		uid = bundle.getString("uid");
		newaddress = (EditText) findViewById(R.id.newaddress);
		addBar = (ProgressBar) findViewById(R.id.newaddloadingBar);
		initnetaddress();
		addressbutton = (ViewGroup) findViewById(R.id.title_right_layout_newadd);
		this.backbar = (ViewGroup) findViewById(R.id.title_left_layout_newadd);
		netaddressbutton = (ImageView) findViewById(R.id.netaddressbutton);
		netaddressbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 判断,如果系统没有在启动activity时获取到地址,就尝试手动获取,并修改手动获取标志为真,如果已经获取到,则覆盖到输入框
				if (netaddress == null) {
					Toast.makeText(UpdateAddress.this, "正在获取地址,请稍候...",
							Toast.LENGTH_SHORT).show();
					manual = true;
					initnetaddress();
				} else {

					newaddress.setText(netaddress);
				}
			}
		});
		addressbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(UpdateAddress.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				NewaddressCheck();

			}
		});
		this.backbar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				// 定义退出当前Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});
	}

	public void NewaddressCheck() {
		if (newaddress.getText().toString().length() < 10) {
			Toast.makeText(this, "地址貌似有点短....", Toast.LENGTH_SHORT).show();
		} else {
			addBar.setVisibility(View.VISIBLE);
			NewAddress();
		}
	}

	@SuppressLint("HandlerLeak")
	public void NewAddress() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				// xmlwebData解析网络中xml中的数据
				state = NetNewAddress.getData("uid="
						+ uid
						+ "&address="
						+ URLEncoder.encode(Utils.getBASE64(newaddress
								.getText().toString())));
				// 发送消息，并把persons结合对象传递过去
				handler.sendEmptyMessage(0x22199);
			}
		};
		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x22199) {
						addBar.setVisibility(View.GONE);
						result();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void result() {

		if (state == 0) {

			Toast.makeText(this, "更新地址失败", Toast.LENGTH_SHORT).show();
		}
		if (state == -1) {

			Toast.makeText(this, "链接服务器异常,请稍候重试", Toast.LENGTH_SHORT).show();
		}
		if (state == 1) {
			flag = true;
			config_exit();

		}
	}

	public void config_exit() {

		Intent intent = new Intent(UpdateAddress.this, MainUIMain.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", flag);
		bundle.putString("newaddress", newaddress.getText().toString());
		intent.putExtras(bundle);
		setResult(0x711, intent);
		Toast.makeText(this, "更新地址成功", Toast.LENGTH_LONG).show();
		finish();
		// 定义退出当前Activity的动画
		overridePendingTransition(R.anim.wemall_slide_in_left,
				R.anim.wemall_slide_out_right);
	}

	private void initnetaddress() {
		// 初始化定位，只采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(false);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次,
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() != 0) {
			// 获取网络地址失败
		}

		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 定位成功回调信息，设置相关消息
			if (amapLocation.getProvince() == null) {
				// 地址信息为空....
			} else {
				// 将网络地址赋予变量,以供需要时使用
				if (manual == true) {
					// 注意去除高德返回的地址中的不确定因素
					this.netaddress = (amapLocation.getAddress()).replaceAll(
							"靠近", "");
					newaddress.setText(netaddress);
				} else {
					this.netaddress = (amapLocation.getAddress()).replaceAll(
							"靠近", "");
				}
			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// 移除定位请求
		mLocationManagerProxy.removeUpdates(this);
		// 销毁定位
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onBackPressed() {
		this.finish();
		// 定义退出当前Activity的动画
		overridePendingTransition(R.anim.wemall_slide_in_left,
				R.anim.wemall_slide_out_right);
	}

}
