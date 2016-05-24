package cn.edu.zzu.wemall.ui;

import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.database.DataBaseHelp;
import cn.edu.zzu.wemall.database.OperateTable;
import cn.edu.zzu.wemall.net.NetNewAddress;
import cn.edu.zzu.wemall.net.NetNewName;
import cn.zzu.edu.wemall.utils.Utils;

public class UpdateAddress extends Activity implements AMapLocationListener {
	private ViewGroup backbar;
	private String uid;
	private boolean flag = false;
	private boolean flag1 = false;
	private int state = -1;
	private int state1 = -1;
	private EditText newaddress;
	private ViewGroup addressbutton;
	private ProgressBar addBar;
	private Handler handler = null;
	private boolean manual = false;// 判断是否是手动获取地址,如果是在没有主动获取地址的情况下点击了使用网络地址,则在获取到地之后主动覆盖到编辑框
	private String netaddress = null; // 通过高德地图接口定位获取当前地址...么么哒
	// 声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;
	public AMapLocationClient mlocationClient;

	private ImageView netaddressbutton;
	private ImageView addaddressbutton;
	private DataBaseHelp openhelper;
	private static final String TABLENAME_address = "address";
	private SQLiteDatabase db;
	private ArrayList<String> address_list;
	private OperateTable mytable;
	private Handler myhandler;
	private ListView addaddresslist;
	private DataBaseHelp helper;
	private List<String> address;
	private EditText phonenumber;
	private EditText name;
	private ImageView savename;
	private ImageView savephonenumber;
	private SharedPreferences userinfo;
	private String OldPhoneNumber;
	private String OldName;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_user_newaddress);
		Bundle bundle = this.getIntent().getExtras();
		uid = bundle.getString("uid");
		newaddress = (EditText) findViewById(R.id.newaddress);
		addBar = (ProgressBar) findViewById(R.id.newaddloadingBar);
      
		addressbutton = (ViewGroup) findViewById(R.id.title_right_layout_newadd);
		this.backbar = (ViewGroup) findViewById(R.id.title_left_layout_newadd);
		// 添加多个收货地址
		addaddressbutton = (ImageView) findViewById(R.id.addaddressbutton);
		addaddresslist = (ListView) findViewById(R.id.addaddresslist);

		// 姓名，电话
		name = (EditText) findViewById(R.id.name);
		phonenumber = (EditText) findViewById(R.id.phonenumber);
		OldName = bundle.getString("username");
		name.setText(OldName);
		OldPhoneNumber = bundle.getString("userphone");
		phonenumber.setText(OldPhoneNumber);

		// 修改姓名和电话
		savename = (ImageView) findViewById(R.id.savename);
		savephonenumber = (ImageView) findViewById(R.id.savphonenumber);
		savename.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						UpdateAddress.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				NewnameCheck();
			}
		});
		savephonenumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean iscorrect = isPhoneNumberValid(phonenumber.getText().toString());
				if (iscorrect) {
					if (!phonenumber.getText().toString().equals(OldPhoneNumber)) {
						SavePreferencesnewphone(phonenumber.getText().toString());
						Toast.makeText(UpdateAddress.this, "成功修改电话号码", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(UpdateAddress.this, "电话号码没有改变", Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(UpdateAddress.this, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
				}

			}
		});
		// 获取定位坐标
		netaddressbutton = (ImageView) findViewById(R.id.netaddressbutton);
		netaddressbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(UpdateAddress.this, "正在获取地址,请稍候...", Toast.LENGTH_SHORT).show();

				initnetaddress();
				newaddress.setText(netaddress);
			}
		});
		addressbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ((InputMethodManager)
				// getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				// UpdateAddress.this.getCurrentFocus().getWindowToken(),
				// InputMethodManager.HIDE_NOT_ALWAYS);
				NewaddressCheck();

			}
		});
		this.backbar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				// 定义退出当前Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left, R.anim.wemall_slide_out_right);
			}
		});
		// 添加多个收货地址点击事件
		initaddress();
		addaddressbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addaddress();
				initaddress();
				newaddress.setText("");
				UpdateAddress.this.HideKeyboard();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		userinfo = this.getSharedPreferences("userinfo", 0);
		String userphone = userinfo.getString("userphone", "NULL");
		phonenumber.setText(userphone);
		super.onResume();
	}

	// 检测手机号输入是否合法
	public static boolean isPhoneNumberValid(String phoneNumber) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();
	}

	public void HideKeyboard() {
		try {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}

	}

	// 判断添加的地址标签的同名字段，和数据库操作
	public void addaddress() {
		String address_name = newaddress.getText().toString();
		if (!address_name.equals("")) {
			String sql = "SELECT address_name FROM " + TABLENAME_address + " WHERE address_name= '" + address_name
					+ "'";
			this.openhelper = new DataBaseHelp(UpdateAddress.this);
			db = UpdateAddress.this.openhelper.getReadableDatabase();
			OperateTable mytable = new OperateTable(db);
			Cursor resul_find = this.db.rawQuery(sql, null);
			if (!resul_find.isAfterLast()) {
				Toast.makeText(UpdateAddress.this, "地址标签已存在", Toast.LENGTH_SHORT).show();
				this.db.close();
			} else
				mytable.insert_his(address_name);
		} else {
			Toast.makeText(UpdateAddress.this, "请输入你要添加的地址标签", 0).show();
		}

	}

	// 界面初始从数据加载地址标签
	public void initaddress() {
		address_list = new ArrayList<String>();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				address_list = (ArrayList<String>) update_address();
				if (!address_list.isEmpty())
					Log.i("i", address_list.get(0).toString());
				Message address_message = myhandler.obtainMessage();
				address_message.obj = address_list;
				myhandler.sendMessage(address_message);
			}

		}.start();
		;
		myhandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				address = (List<String>) msg.obj;
				TextView headView = new TextView(UpdateAddress.this);
				ArrayAdapter<String> myadapter = new ArrayAdapter<String>(UpdateAddress.this,
						android.R.layout.simple_list_item_1, address);
				myadapter.notifyDataSetChanged();
				addaddresslist.setAdapter(myadapter);
				addaddresslist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						ListView list = (ListView) parent;
						newaddress.setText(list.getItemAtPosition(position).toString());
					}

				});

				// 长按删除
				addaddresslist.setOnItemLongClickListener(new butAlertImpl());

			}
		};
	}

	// 长按删除地址标签
	private class butAlertImpl implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
			// TODO Auto-generated method stub
			final ListView listView = (ListView) parent;
			final String info = listView.getItemAtPosition(position).toString();
			Dialog dialog = new AlertDialog.Builder(UpdateAddress.this).setIcon(R.drawable.warning).setTitle("确定删除？")
					.setMessage("您确定要删除：  " + info + "？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							ArrayAdapter<String> myadapter = new ArrayAdapter<String>(UpdateAddress.this,
									android.R.layout.simple_list_item_1, address);
							address.remove(position);
							myadapter.notifyDataSetChanged();
							// 数据更新后要重新刷新adapter 不然会出错
							listView.setAdapter(myadapter);

							helper = new DataBaseHelp(UpdateAddress.this);
							mytable = new OperateTable(UpdateAddress.this.helper.getWritableDatabase());
							mytable.delete_his(info);

							// removeLineFromFile(FILENAME, info);
							Toast.makeText(UpdateAddress.this, info, 0).show();
						}

					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).create();
			dialog.show();
			return false;
		}
	}

	//
	public List<String> update_address() {
		List<String> all = new ArrayList<String>();
		String sql = "SELECT address_name FROM " + TABLENAME_address;
		this.openhelper = new DataBaseHelp(UpdateAddress.this);
		db = UpdateAddress.this.openhelper.getReadableDatabase();
		Cursor result = this.db.rawQuery(sql, null);
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			all.add(result.getString(0));
		}
		this.db.close();
		return all;

	}

	// 更新昵称
	public void NewnameCheck() {
		if (name.getText().toString().length() < 2) {
			Toast.makeText(this, "昵称貌似有点短....", Toast.LENGTH_SHORT).show();
		}
		if (name.getText().toString().length() > 15) {
			Toast.makeText(this, "昵称貌似有点长....", Toast.LENGTH_SHORT).show();
		} else {
			if (!name.getText().toString().equals(OldName)) {
				addBar.setVisibility(View.VISIBLE);
				addBar.bringToFront();
				Newname();
			} else {
				Toast.makeText(this, "昵称貌似没有改变....", Toast.LENGTH_SHORT).show();
			}

		}
	}

	@SuppressLint("HandlerLeak")
	public void Newname() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				// xmlwebData解析网络中xml中的数据
				state1 = NetNewName.getData(
						"uid=" + uid + "&name=" + URLEncoder.encode(Utils.getBASE64(name.getText().toString())));
				// 发送消息，并把persons结合对象传递过去
				Log.i("i", String.valueOf(state1));
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
						addBar.bringToFront();
						result1();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void NewaddressCheck() {
		if (newaddress.getText().toString().length() < 10) {
			Toast.makeText(this, "地址貌似有点短....", Toast.LENGTH_SHORT).show();
		} else {
			addBar.setVisibility(View.VISIBLE);
			addBar.bringToFront();
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
				state = NetNewAddress.getData("uid=" + uid + "&address="
						+ URLEncoder.encode(Utils.getBASE64(newaddress.getText().toString())));
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
						addBar.bringToFront();
						result();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void result1() {

		if (state1 == 0) {

			Toast.makeText(this, "更新昵称失败", Toast.LENGTH_SHORT).show();
		}
		if (state1 == -1) {

			Toast.makeText(this, "昵称链接服务器异常,请稍候重试", Toast.LENGTH_SHORT).show();
		}
		if (state1 == 1) {
			flag1 = true;
			config_exit1();

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

	public void config_exit1() {

		Intent intent = new Intent(UpdateAddress.this, MainUIMain.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean("result", flag1);
		bundle.putString("newname", name.getText().toString());
		intent.putExtras(bundle);
		setResult(0x712, intent);
		Toast.makeText(this, "更新昵称成功", Toast.LENGTH_LONG).show();
		finish();
		// 定义退出当前Activity的动画
		overridePendingTransition(R.anim.wemall_slide_in_left, R.anim.wemall_slide_out_right);
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
		overridePendingTransition(R.anim.wemall_slide_in_left, R.anim.wemall_slide_out_right);
	}

	private void initnetaddress() {

		mlocationClient = new AMapLocationClient(this);
		// 初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		// 设置返回地址信息，默认为true
		mLocationOption.setNeedAddress(true);
		// 设置定位监听
		mlocationClient.setLocationListener(this);
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(2000);
		// 设置定位参数
		mlocationClient.setLocationOption(mLocationOption);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用onDestroy()方法
		// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
		// 启动定位
		mlocationClient.startLocation();

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null) {
			if (amapLocation.getErrorCode() == 0) {
				// 定位成功回调信息，设置相关消息
				amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
				amapLocation.getLatitude();// 获取纬度
				amapLocation.getLongitude();// 获取经度
				amapLocation.getAccuracy();// 获取精度信息
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date(amapLocation.getTime());
				df.format(date);// 定位时间
				amapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
				amapLocation.getCountry();// 国家信息
				amapLocation.getProvince();// 省信息
				amapLocation.getCity();// 城市信息
				amapLocation.getDistrict();// 城区信息
				amapLocation.getStreet();// 街道信息
				amapLocation.getStreetNum();// 街道门牌号信息
				amapLocation.getCityCode();// 城市编码
				amapLocation.getAdCode();// 地区编码
				netaddress = amapLocation.getProvince() + amapLocation.getCity() + amapLocation.getDistrict()
						+ amapLocation.getStreet() + amapLocation.getStreetNum();
			} else {
				// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
				Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:"
						+ amapLocation.getErrorInfo());
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mlocationClient.stopLocation();// 停止定位
		mlocationClient.onDestroy();// 销毁定位客户端
	}

	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onBackPressed() {
		this.finish();
		// 定义退出当前Activity的动画
		overridePendingTransition(R.anim.wemall_slide_in_left, R.anim.wemall_slide_out_right);
	}

	// 修改配置文件电话
	public void SavePreferencesnewphone(String phone) {
		userinfo = this.getSharedPreferences("userinfo", 0);
		SharedPreferences.Editor editor = userinfo.edit();
		editor.putString("userphone", phone);
		editor.commit();
	}

}
