package cn.edu.zzu.wemall.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.adapter.MenuAdapter;
import cn.edu.zzu.wemall.adapter.PagerAdapter;
import cn.edu.zzu.wemall.net.NetGoodsType;

/**
 * 主页面
 * 
 * @author Liu Dewei
 * 
 * 
 */
public class MainUIMain extends FragmentActivity implements
		OnPageChangeListener, OnCheckedChangeListener {
	private ListView menulistview;
	private PagerAdapter pageadapter = null;
	private ArrayList<HashMap<String, Object>> menudata;
	private SlideMenu slideMenu;
	private TextView titleText, titleText2;
	private ViewGroup MenuButton;
	private RadioButton navigationBtn[] = new RadioButton[3];
	// 页卡内容
	private ViewPager viewPager;
	// Tab页面列表
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	// 当前页面
	private MainUiGoods Goods;
	private MainUiCart Cart;
	private MainUIUser User;
	private long exitTime = 0;
	private MenuAdapter menuadapter;
	public static PagerAdapter mpAdapter;
	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_main_ui);
		Cart = new MainUiCart();
		User = new MainUIUser();
		Goods = new MainUiGoods();
		titleText = (TextView) findViewById(R.id.main_title_text);
		titleText2 = (TextView) findViewById(R.id.main_title_text2);
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		MenuButton = (ViewGroup) findViewById(R.id.main_title_left_layout_details);
		menulistview = (ListView) findViewById(R.id.menulist);
		menulistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 隐藏菜单
				if (!slideMenu.isMainScreenShowing()) {
					slideMenu.closeMenu();
				}
				if ((menudata.get(arg2).get("name").equals(titleText.getText()))) {
					// 分类无更改
				} else {
					titleText.setText((String) menudata.get(arg2).get("name"));
					Goods.resetgroup();
					Goods.initview(Integer.parseInt(menudata.get(arg2)
							.get("id").toString()));
					Goods.resetlist();
				}
			}

		});
		menudata = new ArrayList<HashMap<String, Object>>();
		menuadapter = new MenuAdapter(this, menudata);
		menulistview.setAdapter(menuadapter);
		
		

		// 初始化ViewPager，菜单数据
		InitViewPager();
		GetMenu();
		MenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (slideMenu.isMainScreenShowing()) {
					slideMenu.openMenu();
					viewPager.setClickable(false);
				} else {
					slideMenu.closeMenu();
					viewPager.setClickable(true);
				}

			}
		});
		navigationBtn[0] = (RadioButton) findViewById(R.id.type_tab_good);
		navigationBtn[1] = (RadioButton) findViewById(R.id.type_tab_cart);
		navigationBtn[2] = (RadioButton) findViewById(R.id.type_tab_user);
		navigationBtn[0].setChecked(true);// 初始化第一个按钮为选中
		for (int i = 0; i < navigationBtn.length; i++) {
			navigationBtn[i].setOnCheckedChangeListener(this);
		}
	}

	protected Context getActivity() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * 初始化菜单列表
	 * 
	 */
	@SuppressLint("HandlerLeak")
	public void GetMenu() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			public void run() {
				// xmlwebData解析网络中xml中的数据
				try {
					menudata = NetGoodsType.getData();
				} catch (Exception e) {

				}
				// 发送消息，并把persons结合对象传递过去
				handler.sendEmptyMessage(0x11024);
			}
		};

		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x11024) {
						// 下一步给ListView绑定数据
						initmenu();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initmenu() {
		// 重设数据源
		menuadapter.set_datasource(menudata);
		menuadapter.notifyDataSetChanged();
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		viewPager.setOffscreenPageLimit(3);
		fragmentList.add(Goods);
		fragmentList.add(Cart);
		fragmentList.add(User);
		mpAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(mpAdapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(this);
	}

	public PagerAdapter getAdapter() {
		return pageadapter;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// 在这里判断vpage是否滑到了商品页面，如果滑到了商品页面并且继续向右拉动屏幕，则展现菜单列表//
		if (this.viewPager.getCurrentItem() == 0 && arg0 == 1) {

		} else {

		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		navigationBtn[arg0].setChecked(true);
	}

	/*
	 * 
	 * 页面切换监听器
	 */

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		HideKeyboard();
		int currentFragment = 0;
		if (!isChecked)
			return;
		switch (buttonView.getId()) {
		case R.id.type_tab_good:
			currentFragment = 0;
			MenuButton.setVisibility(View.VISIBLE);
			titleText2.setVisibility(View.GONE);
			if (!slideMenu.isMainScreenShowing()) {
				slideMenu.closeMenu();
			}
			break;
		case R.id.type_tab_cart:
			currentFragment = 1;
			titleText2.setVisibility(View.VISIBLE);
			titleText2.setText("购物车");
			MenuButton.setVisibility(View.GONE);
			break;
		case R.id.type_tab_user:
			currentFragment = 2;
			titleText2.setVisibility(View.VISIBLE);
			titleText2.setText("个人中心");
			MenuButton.setVisibility(View.GONE);
			break;
		}
		viewPager.setCurrentItem(currentFragment);
	}

	/**
	 * 
	 * 
	 * 返回按钮监听
	 * 
	 */

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				// 此处写Toast
				Toast toast = Toast.makeText(this, R.string.wemall_press_more,
						Toast.LENGTH_SHORT);
				toast.show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);

			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * 刷新购物车 (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public void refreshcart() {
		Cart.InitCart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 切换到商品列表页面
	public void gotoshop() {
		viewPager.setCurrentItem(0);
		navigationBtn[0].isChecked();

	}

	// 切换到购物车页面
	public void gotocart() {
		viewPager.setCurrentItem(1);
		navigationBtn[1].isChecked();

	}

	/**
	 * 菜单设置
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {

		case R.id.exitselect:
			configExit();
			break;

		case R.id.aboutselect:
			startActivity(new Intent(this, About.class));
			break;
		default:
			break;
		}
		return true;
	}

	public void configExit() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.wemall_exit)
				.setMessage("\n真的要退出吗?\n")
				.setPositiveButton(R.string.wemall_ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								finish();
								System.exit(0);

							}
						})
				.setNegativeButton(R.string.wemall_cancel,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	// U盟统计
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void RefreshMenu() {
		GetMenu();
	}

	public void HideKeyboard() {
		try {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(this.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}

	}

}