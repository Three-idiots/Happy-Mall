package cn.edu.zzu.wemall.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.http.AsyncHttpClient;
import cn.edu.zzu.wemall.http.AsyncHttpResponseHandler;
import cn.edu.zzu.wemall.http.RequestParams;
import cn.edu.zzu.wemall.mylazylist.FileCache;
import cn.edu.zzu.wemall.mylazylist.ImageLoader;
import cn.edu.zzu.wemall.net.NetLoginCheck;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RecoveryPage;
import cn.smssdk.gui.RegisterPage;
import cn.zzu.edu.wemall.utils.Utils;

import com.umeng.analytics.MobclickAgent;

/**
 * 用户中心
 * 
 * @author Liu Dewei
 * 
 */
public class MainUIUser extends Fragment implements OnClickListener {

	private View view;
	private ViewGroup user_login_layout, user_center_layout, user_logout,
			usercenter_about, user_center_myorder, user_center_claer,
			user_wodeqianbao, wemall_user_center_changepasswd, topuserinfo;
	private ProgressBar loginBar;
	private TextView login, regist, wemall_forget_password;
	private EditText account, passwd;
	private HashMap<String, Object> accountinfo;
	private TextView usercenter_username, usercenter_address;
	private Handler handler = null;
	private String useruid, username, useraddress, userphone;
	private ImageView mFace;
	private SharedPreferences userinfo;
	// result,request code
	private static final int PHOTO_REQUEST_CAMERA = 0x111;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 0x112;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 0x113;// 结果
	private Bitmap bitmap;
	// 临时图像文件
	private String PHOTO_FILE_NAME = ".temp.jpg";
	private File tempFile;
	private ImageLoader imageLoader;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.wemall_tab_user, null);
		imageLoader = new ImageLoader(getActivity());
		regist = (TextView) view.findViewById(R.id.wemall_regist_button);
		login = (TextView) view.findViewById(R.id.wemall_login_button);
		account = (EditText) view.findViewById(R.id.wemall_login_account);
		passwd = (EditText) view.findViewById(R.id.wemall_login_passwd);
		wemall_forget_password = (TextView) view
				.findViewById(R.id.wemall_forget_password);
		topuserinfo = (ViewGroup) view.findViewById(R.id.topuserinfo);
		topuserinfo.setOnClickListener(this);
		wemall_forget_password.setOnClickListener(this);
		user_center_claer = (ViewGroup) view
				.findViewById(R.id.user_center_clear);
		user_center_claer.setOnClickListener(this);
		user_login_layout = (ViewGroup) view.findViewById(R.id.userloginlayout);
		user_center_myorder = (ViewGroup) view
				.findViewById(R.id.user_center_myorder);
		user_center_myorder.setOnClickListener(this);
		wemall_user_center_changepasswd = (ViewGroup) view
				.findViewById(R.id.wemall_user_center_changepasswd);
		wemall_user_center_changepasswd.setOnClickListener(this);
		user_wodeqianbao = (ViewGroup) view
				.findViewById(R.id.wemall_user_center_wodeqianbao);
		user_wodeqianbao.setOnClickListener(this);
		usercenter_about = (ViewGroup) view.findViewById(R.id.usercenter_about);
		usercenter_about.setOnClickListener(this);
		user_logout = (ViewGroup) view.findViewById(R.id.user_logout);
		user_logout.setOnClickListener(this);
		mFace = (ImageView) view.findViewById(R.id.user_center_user_icon);
		mFace.setOnClickListener(this);
		user_center_layout = (ViewGroup) view
				.findViewById(R.id.usercenterlayout);
		loginBar = (ProgressBar) view.findViewById(R.id.loginBar);
		usercenter_address = (TextView) view
				.findViewById(R.id.user_center_address);
		usercenter_username = (TextView) view
				.findViewById(R.id.user_center_username);
		this.userinfo = getActivity().getSharedPreferences("userinfo", 0);
		this.ReadPreferences();
		if (!(useruid.equals("NULL") || username.equals("NULL")
				|| userphone.equals("NULL") || useraddress.equals("NULL"))) {
			user_login_layout.setVisibility(View.GONE);
			user_center_layout.setVisibility(View.VISIBLE);
			usercenter_username.setText(username);
			// 异步加载头像
			imageLoader.DisplayImage(MyConfig.SERVERADDRESSBASE + "Api/uploads/"
					+ Utils.MD5(useruid) + ".jpg", mFace);
			if (!(useraddress.length() == 0)) {
				usercenter_address.setText(useraddress);
			}
		}
		regist.setOnClickListener(this);
		login.setOnClickListener(this);
		return view;
	}

	/*
	 * 
	 * 开启新线程验证登录
	 */

	@SuppressLint("HandlerLeak")
	public void getaccountinfo() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			public void run() {

				// xmlwebData解析网络中xml中的数据
				try {
					accountinfo = NetLoginCheck.getData("account="
							+ account.getText().toString() + "&passwd="
							+ passwd.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 发送消息，并把persons结合对象传递过去
				handler.sendMessage(handler.obtainMessage(0x11033, accountinfo));
			}
		};

		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				@SuppressWarnings("unchecked")
				public void handleMessage(Message msg) {
					if (msg.what == 0x11033) {
						// 下一步给ListView绑定数据
						regist.setClickable(true);
						login.setClickable(true);
						loginBar.setVisibility(View.GONE);
						initusercenter((HashMap<String, Object>) msg.obj);
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 * 
	 * 根据从服务器上获取的登录状态信息,初始化用户中心
	 */
	public void initusercenter(HashMap<String, Object> userinfo) {

		if (userinfo == null) {
			Toast.makeText(getActivity(), "链接服务器异常", Toast.LENGTH_SHORT).show();
		}

		else if (userinfo.get("state").equals("-1")) {
			Toast.makeText(getActivity(), "账户不存在", Toast.LENGTH_SHORT).show();
		}

		else if (userinfo.get("state").equals("0")) {
			Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
		}

		else if (userinfo.get("state").equals("1")) {
			this.SavePreferences((String) userinfo.get("uid"),
					(String) userinfo.get("name"),
					(String) userinfo.get("phone"),
					(String) userinfo.get("address"));

			this.ReadPreferences();
			// 初始化已登录界面//////////////////////////////////////////////
			account.setText("");
			passwd.setText("");
			user_login_layout.setVisibility(View.GONE);
			user_center_layout.setVisibility(View.VISIBLE);
			usercenter_username.setText((String) userinfo.get("name"));
			// 异步加载头像
			imageLoader.DisplayImage(MyConfig.SERVERADDRESS + "Api/uploads/"
					+ Utils.MD5(useruid) + ".jpg", mFace);
			if (!(userinfo.get("address").toString().length() == 0)) {
				usercenter_address.setText((String) userinfo.get("address"));
			}
			// /////////////////////////////////////////////////////////////
		}
	}

	/*
	 * 
	 * 实现的点击方法
	 */

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.wemall_regist_button:
			// startActivity(new Intent(getActivity(), Regist.class));
			// 打开手机注册
			RegisterPage registerPage = new RegisterPage();
			registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果,启动帐号信息完善界面
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String phone = (String) phoneMap.get("phone");

						// //////////////////////////////////////////////////////////////////

						Intent intent = new Intent(getActivity(), Regist.class);
						// 用Bundle携带数据
						Bundle bundle = new Bundle();
						// 传递name参数为tinyphp
						bundle.putString("phone", phone);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
			registerPage.show(getActivity());
			break;
		case R.id.wemall_forget_password:
			// startActivity(new Intent(getActivity(), Regist.class));
			// 打开手机验证
			RecoveryPage recoveryPage = new RecoveryPage();
			recoveryPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果,启动帐号信息完善界面
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String phone = (String) phoneMap.get("phone");

						// //////////////////////////////////////////////////////////////////
						Intent intent = new Intent(getActivity(),
								RecoveryPasswd.class);
						// 用Bundle携带数据
						Bundle bundle = new Bundle();
						// 传递name参数为tinyphp
						bundle.putString("phone", phone);
						intent.putExtras(bundle);
						startActivityForResult(intent, 0x212);
						// 定义进入新的Activity的动画
						((MainUIMain) getActivity()).overridePendingTransition(
								R.anim.wemall_slide_in_right,
								R.anim.wemall_slide_out_left);
					}
				}
			});
			recoveryPage.show(getActivity());
			break;
		case R.id.wemall_login_button:
			// 隐藏键盘
			((MainUIMain) getActivity()).HideKeyboard();
			if (((account.getText().toString().trim()).length() == 0)
					|| ((passwd.getText().toString().trim()).length() == 0)) {

				Toast.makeText(getActivity(), "帐号或密码为空", Toast.LENGTH_SHORT)
						.show();
			}

			else {
				regist.setClickable(false);
				login.setClickable(false);
				loginBar.setVisibility(View.VISIBLE);
				this.getaccountinfo();
			}
			break;
		case R.id.user_logout:
			LogoutTip();
			break;
		case R.id.usercenter_about:
			startActivity(new Intent(getActivity(), About.class));
			// 定义进入新的Activity的动画
			((MainUIMain) getActivity()).overridePendingTransition(
					R.anim.wemall_slide_in_right, R.anim.wemall_slide_out_left);
			break;
		case R.id.user_center_clear:
			CleanTip();
			break;
		case R.id.user_center_myorder:
			Intent intent = new Intent(getActivity(), Myorder.class);
			// 用Bundle携带数据
			Bundle bundle = new Bundle();
			// 传递name参数为tinyphp
			bundle.putString("uid", useruid);
			intent.putExtras(bundle);
			startActivity(intent);
			// 定义进入新的Activity的动画
			((MainUIMain) getActivity()).overridePendingTransition(
					R.anim.wemall_slide_in_right, R.anim.wemall_slide_out_left);
			break;
		case R.id.wemall_user_center_changepasswd:
			Intent intent1 = new Intent(getActivity(), ChangePasswd.class);
			// 用Bundle携带数据
			Bundle bundle1 = new Bundle();
			// 传递name参数为tinyphp
			bundle1.putString("uid", useruid);
			intent1.putExtras(bundle1);
			startActivityForResult(intent1, 0x211);
			// 定义进入新的Activity的动画
			((MainUIMain) getActivity()).overridePendingTransition(
					R.anim.wemall_slide_in_right, R.anim.wemall_slide_out_left);
			break;
		case R.id.topuserinfo:
			Intent intent2 = new Intent(getActivity(), UpdateAddress.class);
			// 用Bundle携带数据
			Bundle bundle2 = new Bundle();
			// 传递name参数为tinyphp
			bundle2.putString("uid", useruid);
			intent2.putExtras(bundle2);
			startActivityForResult(intent2, 0x711);
			// 定义进入新的Activity的动画
			((MainUIMain) getActivity()).overridePendingTransition(
					R.anim.wemall_slide_in_right, R.anim.wemall_slide_out_left);
			break;
		case R.id.user_center_user_icon:
			ChangeUserIcon();
			break;
		case R.id.wemall_user_center_wodeqianbao:
			user_wodeqianbao.setClickable(false);
			if (isPkgInstalled(getActivity(), "com.eg.android.AlipayGphone")) {
				Intent LaunchIntent = ((MainUIMain) getActivity())
						.getPackageManager().getLaunchIntentForPackage(
								"com.eg.android.AlipayGphone");
				startActivity(LaunchIntent);
				// 定义进入新的Activity的动画
				((MainUIMain) getActivity()).overridePendingTransition(
						R.anim.wemall_slide_in_right,
						R.anim.wemall_slide_out_left);
			} else {
				Toast.makeText(getActivity(), "支付宝钱包没有安装", Toast.LENGTH_SHORT)
						.show();
			}
			user_wodeqianbao.setClickable(true);
		default:
			break;
		}
	}

	public boolean isPkgInstalled(Context context, String packageName) {

		if (packageName == null || "".equals(packageName))
			return false;
		android.content.pm.ApplicationInfo info = null;
		try {
			info = context.getPackageManager().getApplicationInfo(packageName,
					0);
			return info != null;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	// Preferences设置

	/*
	 * 登录通过后保存登录信息到Preferences
	 */
	public void SavePreferences(String uid, String name, String userphone,
			String address) {
		SharedPreferences.Editor editor = userinfo.edit();
		editor.putString("useruid", uid);
		editor.putString("username", name);
		editor.putString("userphone", userphone);
		editor.putString("useraddress", address);
		editor.commit();
	}

	public void SavePreferencesnewaddress(String address) {
		SharedPreferences.Editor editor = userinfo.edit();
		editor.putString("useraddress", address);
		editor.commit();
	}

	/*
	 * 
	 * 
	 * 应用启动时读取Preferences,用来初始化个人信息
	 */

	public void ReadPreferences() {
		useruid = userinfo.getString("useruid", "NULL");
		username = userinfo.getString("username", "NULL");
		userphone = userinfo.getString("userphone", "NULL");
		useraddress = userinfo.getString("useraddress", "NULL");
	}

	/*
	 * 
	 * 注销账户时销毁Preferences
	 */

	public void DestroyPreferences() {
		SharedPreferences.Editor editor = userinfo.edit();
		editor.putString("useruid", "NULL");
		editor.putString("username", "NULL");
		editor.putString("userphone", "NULL");
		editor.putString("useraddress", "NULL");
		editor.commit();
	}

	/*
	 * 
	 * 清理缓存提示弹窗
	 */

	public void CleanTip() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		final Dialog dialog = builder.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.wemall_clean_dialog);
		TextView logout = (TextView) window.findViewById(R.id.cleanc);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});
		TextView logoutcancel = (TextView) window.findViewById(R.id.cleanb);
		logoutcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				loginBar.setVisibility(View.VISIBLE);
				cleardata();

			}
		});
	}

	/*
	 * 
	 * 账户注销提示
	 */

	public void LogoutTip() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		final Dialog dialog = builder.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.wemall_logout_dialog);
		ViewGroup logout = (ViewGroup) window.findViewById(R.id.logout);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				DestroyPreferences();
				user_login_layout.setVisibility(View.VISIBLE);
				user_center_layout.setVisibility(View.GONE);

			}
		});
		ViewGroup logoutcancel = (ViewGroup) window
				.findViewById(R.id.lougoutcancel);
		logoutcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	/*
	 * 
	 * 更换头像的弹出选择框
	 */

	public void ChangeUserIcon() {
		final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.wemall_choose_pic_layout);
		ViewGroup choose_from_camre = (ViewGroup) window
				.findViewById(R.id.choose_from_camre);
		choose_from_camre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				camera();

			}
		});
		ViewGroup choose_from_gallery = (ViewGroup) window
				.findViewById(R.id.choose_from_gallery);
		choose_from_gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				gallery();
			}
		});
	}

	/*
	 * 
	 * 
	 * 缓存清理
	 */
	@SuppressLint("HandlerLeak")
	public void cleardata() {

		Runnable runnable = new Runnable() {
			public void run() {
				String path = new File(
						android.os.Environment.getExternalStorageDirectory(),
						"WeMall/ImageCache").getPath();

				String size = Utils.FomatFilesize(Utils
						.getTotalSizeOfFilesInDir(path));
				Utils.delAllFile(path);
				handler.sendMessage(handler.obtainMessage(0x11077, size));
			}
		};

		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x11077) {
						loginBar.setVisibility(View.GONE);
						Toast.makeText(getActivity(), msg.obj + "缓存已清除",
								Toast.LENGTH_SHORT).show();
					}
				}

			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// //////////////////////头像相关-//////////////////

	/*
	 * 上传图片
	 */
	public void upload() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			byte[] buffer = out.toByteArray();
			byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
			String photo = new String(encode);
			RequestParams params = new RequestParams();
			params.put("photo", photo);
			params.put("uid", useruid);
			String url = MyConfig.SERVERADDRESS+ "?tag=wemall_update_head";
			AsyncHttpClient client = new AsyncHttpClient();
			client.setUserAgent(MyConfig.ClientUserAgent);
			client.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					try {
						if (statusCode == 200) {
							Toast.makeText(getActivity(), "头像上传成功!",
									Toast.LENGTH_SHORT).show();
							// 新的头像上传成功后旧的缓存失效,我们需要删除之//
							FileCache.Filedelete(MyConfig.SERVERADDRESSBASE
									+ "Api/uploads/" + Utils.MD5(useruid)
									+ ".jpg");

						} else {
							Toast.makeText(getActivity(), "头像上传失败!",
									Toast.LENGTH_SHORT).show();

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(getActivity(), "连接服务器异常", Toast.LENGTH_SHORT)
							.show();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 从相册获取
	 */
	public void gallery() {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/*
	 * 从相机获取
	 */
	public void camera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}

	/**
	 * 剪切图片
	 * 
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/*
	 * 判断扩展存储是否存在
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */

	@SuppressLint("ShowToast")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// ///////////////////////////////////////////////////////////////////
		switch (resultCode) {
		// 修改密码成功的返回结果
		case 0x211:
			Bundle b = data.getExtras();
			String str = b.getString("result");
			if (str.equals("1")) {
				DestroyPreferences();
				user_login_layout.setVisibility(View.VISIBLE);
				user_center_layout.setVisibility(View.GONE);
				// account.setText(userphone);//填充手机号失败,待检查
			}
			// 找回密码成功的返回结果
		case 0x212:
			Bundle backdata = data.getExtras();
			String reresult = backdata.getString("result");
			String rephone = backdata.getString("phone");
			if (reresult.equals("1")) {
				account.setText(rephone);
			}
			break;
		case 0x711:
			Bundle backdata2 = data.getExtras();
			boolean reresult2 = backdata2.getBoolean("result");
			String newaddress = backdata2.getString("newaddress");
			System.out.println(reresult2);
			if (reresult2 == true) {
				useraddress = newaddress;
				usercenter_address.setText(newaddress);
				SavePreferencesnewaddress(newaddress);

			}
			break;
		default:
			break;

		}
		// ///////////////////////////////////////////////////////////////////
		switch (requestCode) {
		// 从图库获取图像的返回结果
		case PHOTO_REQUEST_GALLERY:
			if (data != null) {
				System.out.println("有图");
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}
			break;
		// 从照相机获取图像的返回结果
		case PHOTO_REQUEST_CAMERA:
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(getActivity(), "未找到存储卡,无法存储照片!", 0).show();
			}
			break;
		// 裁减图像的返回结果
		case PHOTO_REQUEST_CUT:
			try {
				// 获取到图像，将图像设置到到imageview，并启动图像上传
				bitmap = data.getParcelableExtra("data");
				this.mFace.setImageBitmap(bitmap);
				upload();

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// U盟统计-标记
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("个人中心"); // 统计页面
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("个人中心");
	}

}