package cn.edu.zzu.wemall.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.net.NetCheckUp;
import cn.zzu.edu.wemall.utils.Utils;

public class About extends Activity {

	private ViewGroup backbar, about_official_site, about_official_tieba,
			about_app_checkupdate,wemall_gpl;
	private TextView version;
	private Handler handler = null;
	private File updatefile;
	private HashMap<String, String> versioninfo = new HashMap<String, String>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_about);
		this.version = (TextView) findViewById(R.id.app_version);
		this.backbar = (ViewGroup) findViewById(R.id.title_left_layout_about);
		this.about_official_site = (ViewGroup) findViewById(R.id.about_official_site);
		this.about_official_tieba = (ViewGroup) findViewById(R.id.about_official_tieba);
		this.about_app_checkupdate = (ViewGroup) findViewById(R.id.about_app_checkupdate);
		this.wemall_gpl=(ViewGroup) findViewById(R.id.about_official_gpl);
		try {
			String currentVersion = getPackageManager().getPackageInfo(
					"cn.edu.zzu.wemall", 0).versionName;
			version.setText(currentVersion);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wemall_gpl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(About.this, Gpl.class);
				startActivity(intent);
				overridePendingTransition(R.anim.wemall_slide_in_right,
						R.anim.wemall_slide_out_left);
			}
		});

		backbar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				About.this.finish();
				// 定义进入新的Activity的动画
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});
		about_app_checkupdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Utils.isNetworkConnected(About.this)) {
					Checkupdate();
				} else {
					Toast.makeText(About.this, R.string.wemall_neterrtip, Toast.LENGTH_LONG)
							.show();
				}

			}
		});
		about_official_site.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("http://www.itlaborer.com");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		});
		about_official_tieba.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri
						.parse("http://tieba.baidu.com/f?kw=幻舞奇影&ie=utf-8");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		});

	}

	@SuppressLint("HandlerLeak")
	public void Checkupdate() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			public void run() {
				// 解析网络中xml中的数据
				versioninfo = NetCheckUp
						.getData(MyConfig.SERVERADDRESSBASE
								+ "Api/update.xml");
				// 发送消息，唤醒主线程更新UI
				handler.sendEmptyMessage(0X111111);
			}
		};
		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0X111111) {
						int version = Integer.parseInt(versioninfo
								.get("version"));
						String path = versioninfo.get("path");
						String newi = versioninfo.get("new");
						int currentVersion = 0;
						try {
							PackageInfo info;
							info = getPackageManager().getPackageInfo(
									"cn.edu.zzu.wemall", 0);
							currentVersion = info.versionCode;
						} catch (Exception e) {
							// TODO: handle exception
						}
						if (version <= currentVersion) {
							// 弹出当前就是最新版提示
							Toast.makeText(About.this, R.string.wemall_versiontop,
									Toast.LENGTH_LONG).show();
						} else {
							UpdateTip(newi, path, version);
						}
					}
				}

			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void UpdateTip(String tip, final String path, final int version) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final Dialog dialog = builder.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.wemall_update_dialog);
		TextView logout = (TextView) window.findViewById(R.id.updatec);
		TextView updatetip = (TextView) window.findViewById(R.id.updatetip);
		updatetip.setText(tip);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});
		TextView logoutcancel = (TextView) window.findViewById(R.id.updateb);
		logoutcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Getupdate(path, version);
			}
		});
	}

	@SuppressLint("HandlerLeak")
	public void Getupdate(final String httpUrl, final int version) {

		final String fileName = "client" + version + ".apk";
		File tmpFile = new File(
				android.os.Environment.getExternalStorageDirectory(), "WeMall/"
						+ fileName);
		if (tmpFile.exists()) {
			openFile(tmpFile);
		} else {
			
			Toast.makeText(About.this, R.string.wemall_downloading, Toast.LENGTH_SHORT)
			.show();

			// 开一条子线程加载网络数据
			Runnable runnable = new Runnable() {
				public void run() {
					updatefile = downLoadFile(httpUrl, version);
					handler.sendEmptyMessage(0X111112);
				}
			};

			try {
				// 开启线程
				new Thread(runnable).start();
				// handler与线程之间的通信及数据处理
				handler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 0X111112) {
							openFile(updatefile);
						}
					}

				};
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected File downLoadFile(String httpUrl, int version) {
		// TODO Auto-generated method stub
		final String fileName = "client" + version + ".apk";
		final File file = new File(
				android.os.Environment.getExternalStorageDirectory(), "WeMall/"
						+ fileName);
		try {
			URL url = new URL(httpUrl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buf = new byte[256];
				conn.connect();
				double count = 0;
				if (conn.getResponseCode() >= 400) {
					Toast.makeText(About.this, R.string.wemall_conn_timeout, Toast.LENGTH_SHORT)
							.show();
				} else {
					while (count <= 100) {
						if (is != null) {
							int numRead = is.read(buf);
							if (numRead <= 0) {
								break;
							} else {
								fos.write(buf, 0, numRead);
							}
						} else {
							break;
						}

					}
				}

				conn.disconnect();
				fos.close();
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file;
	}

	private void openFile(File file) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		// 定义退出当前Activity的动画
		overridePendingTransition(R.anim.wemall_slide_in_left,
				R.anim.wemall_slide_out_right);
	}

}
