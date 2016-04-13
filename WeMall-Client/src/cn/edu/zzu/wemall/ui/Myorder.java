package cn.edu.zzu.wemall.ui;

import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.adapter.OrderAdapter;
import cn.edu.zzu.wemall.adapter.OrderPopAdapter;
import cn.edu.zzu.wemall.myview.XListView;
import cn.edu.zzu.wemall.myview.XListView.IXListViewListener;
import cn.edu.zzu.wemall.net.NetOrdersData;
import cn.edu.zzu.wemall.object.OrderItem;
import cn.zzu.edu.wemall.utils.Utils;

public class Myorder extends Activity implements IXListViewListener {

	private Handler handler = null;
	private ArrayList<OrderItem> allorders;
	private String uid;
	private OrderAdapter adapter;
	private XListView myorder;
	private ViewGroup backbar;
	private ProgressBar loadingorderbar;
	private boolean PullRefresh = true;
	private int num = 100;
	private int group = 1;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wemall_user_myorder);
		myorder = (XListView) findViewById(R.id.listorder);
		myorder.setPullRefreshEnable(PullRefresh);
		myorder.setPullLoadEnable(false);
		Bundle bundle = this.getIntent().getExtras();
		uid = bundle.getString("uid");
		allorders = new ArrayList<OrderItem>();
		adapter = new OrderAdapter(this, allorders);
		myorder.setAdapter(adapter);
		myorder.setXListViewListener(this);
		myorder.setOverScrollMode(View.OVER_SCROLL_NEVER);
		myorder.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int id,
					long arg3) {
				// 弹窗显示订单内容
				orderitem(Utils.JsonStringToArrayList(adapter.getItem(
						(PullRefresh == true) ? id - 1 : id).getCartdata()));
			}
		});
		this.backbar = (ViewGroup) findViewById(R.id.title_left_layout_myorder);
		this.loadingorderbar = (ProgressBar) findViewById(R.id.orderloadingBar);
		this.backbar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Myorder.this.finish();
				overridePendingTransition(R.anim.wemall_slide_in_left,
						R.anim.wemall_slide_out_right);
			}
		});
		if (!(Utils.isNetworkConnected(Myorder.this))) {
			Toast.makeText(Myorder.this, "网络不可用,呜呜...", Toast.LENGTH_SHORT)
					.show();
		} else {
			GetMyOrder();
		}

	}

	@SuppressLint("HandlerLeak")
	public void GetMyOrder() {

		// 开一条子线程加载网络数据
		Runnable runnable = new Runnable() {
			public void run() {
				// 解析网络中xml中的数据
				
				allorders = NetOrdersData
						.getData("uid=" + uid);
				// 发送消息，唤醒主线程更新UI
				handler.sendMessage(handler.obtainMessage(0x11039, ""));
			}
		};

		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					onLoad();
					if (msg.what == 0x11039) {
						if (allorders == null) {
							loadingorderbar.setVisibility(View.GONE);
							Toast.makeText(Myorder.this, "您还没有订单,快去购买吧",
									Toast.LENGTH_SHORT).show();
						} else {
							loadingorderbar.setVisibility(View.GONE);
							InitOrderList();
						}

					}
				}

			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 初始化订单界面
	private void InitOrderList() {
		// 如果总订单条数小于分组限制条数，则直接不显示上拉加载更多选项
		myorder.setPullLoadEnable(true);
		if (allorders.size() > ((group == 1) ? num : num * (group - 1))) {
			for (int i = allorders.size() - 1; i >= (num * group); i--) {
				allorders.remove(i);
			}
		} else {
			myorder.setPullLoadEnable(false);
			if (group > 1) {
				Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
			}
		}
		adapter.set_datasource(allorders);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		// 定义退出当前Activity的动画
		overridePendingTransition(R.anim.wemall_slide_in_left,
				R.anim.wemall_slide_out_right);
	}

	// 弹窗

	public void orderitem(ArrayList<HashMap<String, String>> data) {
		//避免快速双击产生的重复弹窗问题
		if (Utils.isFastDoubleClick()) {
		        return;
		    }
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final Dialog dialog = builder.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.wemall_orderpopwindow);
		// 订单详情列表
		ListView popitemlist = (ListView) window
				.findViewById(R.id.orderpoplist);
		OrderPopAdapter orderPopAdapter = new OrderPopAdapter(this, data);
		popitemlist.setAdapter(orderPopAdapter);
		ViewGroup exit = (ViewGroup) window.findViewById(R.id.exitdig);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	private void onLoad() {
		myorder.stopRefresh();
		myorder.stopLoadMore();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!(Utils.isNetworkConnected(Myorder.this))) {
			onLoad();
			Toast.makeText(Myorder.this, "网络不可用,呜呜...", Toast.LENGTH_SHORT)
					.show();
		} else {
			GetMyOrder();
			group = 1;
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		group++;
		InitOrderList();
		onLoad();
	}

}
