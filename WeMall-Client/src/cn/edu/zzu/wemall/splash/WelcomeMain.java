package cn.edu.zzu.wemall.splash;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import cn.edu.zzu.wemall.R;

/**
 * 欢迎页主页面
 * 
 * @author Liu Dewei
 */
public class WelcomeMain extends FragmentActivity implements
		OnPageChangeListener, OnCheckedChangeListener {

	private RadioButton navigationBtn[] = new RadioButton[4];
	// 页卡内容
	private ViewPager viewPager;
	// Tab页面列表
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	public static MyPagerAdapter mpAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.wemall_welcome_main);

		InitViewPager();
		navigationBtn[0] = (RadioButton) findViewById(R.id.welcomebutton1);
		navigationBtn[1] = (RadioButton) findViewById(R.id.welcomebutton2);
		navigationBtn[2] = (RadioButton) findViewById(R.id.welcomebutton3);
		navigationBtn[3] = (RadioButton) findViewById(R.id.welcomebutton4);
		for (int i = 0; i < navigationBtn.length; i++) {
			navigationBtn[i].setOnCheckedChangeListener(this);
		}
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		viewPager.setOffscreenPageLimit(4);
		fragmentList.add(new WelcomeStep(1));
		fragmentList.add(new WelcomeStep(2));
		fragmentList.add(new WelcomeStep(3));
		fragmentList.add(new WelcomeStep(4));
		mpAdapter = new MyPagerAdapter(getSupportFragmentManager(),
				fragmentList);
		viewPager.setOffscreenPageLimit(5);
		viewPager.setAdapter(mpAdapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(this);
	}

	/**
	 * 定义适配器
	 * 
	 * @author Liudewei
	 * 
	 */
	class MyPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragmentList;

		public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
			super(fm);
			this.fragmentList = fragmentList;
		}

		/**
		 * 得到每个页面
		 */
		@Override
		public Fragment getItem(int arg0) {
			return (fragmentList == null || fragmentList.size() == 0) ? null
					: fragmentList.get(arg0);
		}

		/**
		 * 页面的总个数
		 */
		@Override
		public int getCount() {
			return fragmentList == null ? 0 : fragmentList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
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
		int currentFragment = 0;
		if (!isChecked)
			return;
		switch (buttonView.getId()) {
		case R.id.welcomebutton1:
			currentFragment = 0;
			break;
		case R.id.welcomebutton2:
			currentFragment = 1;
			break;
		case R.id.welcomebutton3:
			currentFragment = 2;
			break;
		case R.id.welcomebutton4:
			currentFragment = 3;
			break;
		}
		viewPager.setCurrentItem(currentFragment);
	}
}