package cn.edu.zzu.wemall.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.mylazylist.LazyAdapter;
import cn.edu.zzu.wemall.myview.XListView;
import cn.edu.zzu.wemall.myview.XListView.IXListViewListener;
import cn.edu.zzu.wemall.net.NetGoodsData;
import cn.edu.zzu.wemall.object.GoodsItem;
import cn.zzu.edu.wemall.utils.Utils;

import com.umeng.analytics.MobclickAgent;

/**
 * Author Liu Dewei
 * 
 * 商品列表
 * 
 * */

public class MainUiGoods extends ListFragment implements OnClickListener,
		IXListViewListener {
	private ArrayList<GoodsItem> Items, DisplayItem;
	private Handler handler = null;
	private View view;
	private LazyAdapter adapter;
	private ViewGroup topbar, list;
	private ProgressBar loading;
	private TextView reload;
	private int typeid = -1;
	private boolean PullRefresh = true;
	private int num = 30;// 每次显示的条目数，多余的隐藏到上拉加载更多里面
	private int group = 1;// 显示第几组数据，分段加载需要

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.wemall_tab_goods, null);
		topbar = (ViewGroup) view.findViewById(R.id.errnet);
		reload = (TextView) view.findViewById(R.id.error_notice_button1);
		list = (ViewGroup) view.findViewById(R.id.list);
		loading = (ProgressBar) view.findViewById(R.id.good_loadingBar);
		reload.setOnClickListener(this);
		return view;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Items = new ArrayList<GoodsItem>();
		DisplayItem = new ArrayList<GoodsItem>();
		adapter = new LazyAdapter(getActivity(), DisplayItem);
		setListAdapter(adapter);
		// /获取并更新数据
		Getdate();
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		((XListView) getListView()).setPullRefreshEnable(PullRefresh);
		((XListView) getListView()).setPullLoadEnable(false);
		((XListView) getListView()).setXListViewListener(this);
	};

	@SuppressLint("HandlerLeak")
	private void Getdate() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			public void run() {
				// xmlwebData解析网络中xml中的数据
				Items = NetGoodsData.getData();
				// 发送消息
				handler.sendEmptyMessage(0x11021);
			}
		};
		try {
			// 开启线程
			new Thread(runnable).start();
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x11021) {
						group = 1;
						loading.setVisibility(View.GONE);
						onLoad();
						// 判断数据获取状态,提示失败,或者初始化数据
						if (Items == null) {
							topbar.setVisibility(View.VISIBLE);
							list.setVisibility(View.GONE);
							// 此处添加屏幕提示,让用户尝试手动加载并告知可能存在的网络异常
						} else {
							// 首次初始化,使用全部分类,全部分类标志符为-1
							initview(typeid);
						}
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 初始化ListView,我们在这里使用typeid来标识要用那些数据初始化,以供分类时使用,如果初始化全部商品则使用-1
	void initview(int typeid) {
		((XListView) getListView()).setPullLoadEnable(true);
		this.typeid = typeid;
		DisplayItem = new ArrayList<GoodsItem>();
		try {
			DisplayItem = (ArrayList<GoodsItem>) DeepCopy(Items);

			if (!(typeid == -1)) {
				// 处理要显示的数据,过滤掉不需要的,亲,不要从小往大循环,想想为什么

				for (int i = DisplayItem.size() - 1; i >= 0; i--) {
					if (!(DisplayItem.get(i).getTypeId() == typeid)) {
						DisplayItem.remove(i);
					}
				}
			}
			if (DisplayItem.size() > ((group == 1) ? num : num * (group - 1))) {
				for (int i = DisplayItem.size() - 1; i >= (num * group); i--) {
					DisplayItem.remove(i);
				}
			} else {
				((XListView) getListView()).setPullLoadEnable(false);
				if (group > 1) {
					Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT)
							.show();
				}

			}
			//
			adapter.set_datasource(DisplayItem);
			adapter.notifyDataSetChanged();
			onLoad();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 此处坑多！需要修改，未加载的情况下Count=0
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (((LazyAdapter) this.getListAdapter()).getCount() == 0) {
			//Toast.makeText(getActivity(), "正在加载中，请稍候...", Toast.LENGTH_SHORT).show();
		} else if (((LazyAdapter) this.getListAdapter()).getCount() > 0) {
			if (position == 0) {
				//Toast.makeText(getActivity(), "正在加载中，请稍候...",Toast.LENGTH_SHORT).show();
			} else if (position > 0) {
				final GoodsItem thisitem = ((LazyAdapter) this.getListAdapter())
						.getGoodItem((PullRefresh == true) ? position - 1
								: position);
				Intent i = new Intent(getActivity(), ItemDetails.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("t", thisitem);
				i.putExtras(bundle);
				startActivityForResult(i, 0x222);
			}
		}
	}
	// List深度拷贝,保留Items,以备切换分类时从本地获取而不必联网获取
	public List<GoodsItem> DeepCopy(List<GoodsItem> src) throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(
				byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		List<GoodsItem> dest = (List<GoodsItem>) in.readObject();
		return dest;
	}

	public void resetgroup() {
		group = 1;
	}

	public void resetlist() {
		getListView().setSelection(0);
	}

	// U盟统计
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("商品浏览"); // 统计页面
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("商品浏览");
	}

	@Override
	public void onClick(View viewid) {
		switch (viewid.getId()) {
		case R.id.error_notice_button1:
			if (Utils.isNetworkConnected(getActivity())) {
				((XListView) getListView()).CorrectTime();// 校准上次加载时间
				// 刷新分类和商品列表
				((MainUIMain) getActivity()).RefreshMenu();
				Getdate();
				topbar.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
				loading.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(getActivity(), "网络不可用,呜呜...", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		default:
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode) {
		// 从详情页退出时捕获的结果,根据结果决定是否刷新购物车
		case 0x222:
			boolean flag = (boolean) data.getBooleanExtra("flag", true);
			if (flag == true) {
				((MainUIMain) getActivity()).refreshcart();
				;
			} else {

			}
			// 从详情页退出时捕获的结果,根据结果决定是否前往购物车
			boolean flag2 = (boolean) data.getBooleanExtra("flag2", false);
			if (flag2 == true) {
				((MainUIMain) getActivity()).gotocart();
			} else {

			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (Utils.isNetworkConnected(getActivity())) {
			// 刷新分类和商品列表
			((MainUIMain) getActivity()).RefreshMenu();
			Getdate();
		} else {
			Toast.makeText(getActivity(), "网络不可用,呜呜...", Toast.LENGTH_SHORT)
					.show();
			onLoad();
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		group++;
		initview(typeid);

	}

	private void onLoad() {
		((XListView) getListView()).stopRefresh();
		((XListView) getListView()).stopLoadMore();
	}
}
