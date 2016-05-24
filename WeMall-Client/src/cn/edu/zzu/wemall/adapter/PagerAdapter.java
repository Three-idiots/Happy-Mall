package cn.edu.zzu.wemall.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author liudewei 页面适配器
 */
public class PagerAdapter extends FragmentPagerAdapter {

	/**
	 * 页面内容集合
	 */
	private List<Fragment> fgs = null;
	private int mChildCount = 0;

	@Override
	public void notifyDataSetChanged() {
		mChildCount = getCount();
		super.notifyDataSetChanged();
	}

	public PagerAdapter(FragmentManager fm, List<Fragment> fgs) {
		super(fm);
		this.fgs = fgs;
	}

	@Override
	public Fragment getItem(int index) {
		return fgs.get(index);
	}

	@Override
	public int getCount() {
		return fgs.size();// 返回选项卡总数
	}

	public int getItemPosition(Object object) {
		if (mChildCount > 0) {
			mChildCount--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}
}