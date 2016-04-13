package cn.edu.zzu.wemall.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import com.umeng.analytics.MobclickAgent;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.adapter.CartAdapter;
import cn.edu.zzu.wemall.database.SQLProcess;
import cn.edu.zzu.wemall.net.NetSubmitOrder;

/**
 * 购物车
 * 
 * @author Liu Dewei
 * 
 */
public class MainUiCart extends ListFragment implements OnCheckedChangeListener {

	private View view, footerView;
	private ViewGroup cartnogoods, cartlist;
	private CartAdapter adapter;
	private SQLProcess wemalldb;
	private ArrayList<HashMap<String, Object>> cart;
	private RadioButton pay[] = new RadioButton[2];
	private int paystyle = 0;
	private TextView note;
	private TextView submit, gotoshop, gotomyorder, summary;
	private Handler handler = null;
	@SuppressWarnings("unused")
	private int state = -1;
	private SharedPreferences userinfo;
	private ProgressBar cartBar;
	private DecimalFormat df = new DecimalFormat(".#");

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(getActivity()).inflate(
				R.layout.wemall_tab_cart, null);
		footerView = LayoutInflater.from(getActivity()).inflate(
				R.layout.wemall_cart_footer, null);
		cartnogoods = (ViewGroup) view.findViewById(R.id.cartnogoods);
		cartlist = (ViewGroup) view.findViewById(R.id.cartlist);
		cartBar = (ProgressBar) view.findViewById(R.id.cartBar);
		pay[0] = (RadioButton) footerView.findViewById(R.id.pay_ofline);
		pay[1] = (RadioButton) footerView.findViewById(R.id.pay_online);
		note = (TextView) footerView.findViewById(R.id.beizhu);
		submit = (TextView) footerView.findViewById(R.id.submit);
		gotoshop = (TextView) view.findViewById(R.id.gotoshop);
		gotomyorder = (TextView) view.findViewById(R.id.gotomyorder);
		summary = (TextView) footerView.findViewById(R.id.summary);
		note.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setnote();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((MainUIMain) getActivity()).HideKeyboard();
				submit();
			}
		});
		gotoshop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((MainUIMain) getActivity()).gotoshop();
			}
		});
		gotomyorder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (GetPreferencesUid().toString().equals("NULL")) {
					Toast.makeText(getActivity(), "要先登录才能查看订单哦...",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(getActivity(), Myorder.class);
				// 用Bundle携带数据
				Bundle bundle = new Bundle();
				// 传递name参数为tinyphp
				bundle.putString("uid", GetPreferencesUid().toString());
				intent.putExtras(bundle);
				startActivity(intent);
				// 定义进入新的Activity的动画
				((MainUIMain) getActivity()).overridePendingTransition(R.anim.wemall_slide_in_right,
						R.anim.wemall_slide_out_left);
			}
		});
		for (int i = 0; i < pay.length; i++) {
			pay[i].setOnCheckedChangeListener(this);
		}
		return view;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.wemalldb = new SQLProcess(getActivity());
		cart = wemalldb.read_cart();
		this.userinfo = getActivity().getSharedPreferences("userinfo", 0);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		// 在此方法中给listview添加脚部元素///
		super.onActivityCreated(savedInstanceState);
		adapter = new CartAdapter(getActivity(), cart);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int id, long arg3) {
				// 弹窗显示订单内容

				final AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				final Dialog dialog = builder.show();
				Window window = dialog.getWindow();
				window.setContentView(R.layout.wemall_cart_dialog);
				TextView cartcuttitle = (TextView) window
						.findViewById(R.id.cartcuttitle);
				cartcuttitle.setText(cart.get(id).get("name").toString());
				TextView cartcan = (TextView) window.findViewById(R.id.cartcan);
				cartcan.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						dialog.dismiss();

					}
				});
				TextView cartcut = (TextView) window.findViewById(R.id.cartcut);
				cartcut.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						// 确定
						wemalldb.delete_cartitem(Integer.parseInt(cart.get(id)
								.get("id").toString()));
						InitCart();
						dialog.dismiss();

					}
				});

			}
		});
		InitCart();
	}

	public void InitCart() {
		cart = wemalldb.read_cart();
		if (cart.size() == 0) {
			cartlist.setVisibility(View.GONE);
			this.getListView().removeFooterView(footerView);
			cartnogoods.setVisibility(View.VISIBLE);
		} else {
			cartlist.setVisibility(View.VISIBLE);
			if (this.getListView().getFooterViewsCount() == 0) {
				this.getListView().addFooterView(footerView);
			}
			summary.setText(df.format(wemalldb.read_carttotal()));
			cartnogoods.setVisibility(View.GONE);
		}
		// 重设数据源,否则无法更新listview(继承baseadapter,自定义传入参数导致的问题(请详细了解堆栈,引用相关知识))
		setListAdapter(adapter);// 不添加本代码,部分机型的footerview不显示
		adapter.set_datasource(cart);
		adapter.notifyDataSetChanged();
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (!isChecked)
			return;
		switch (buttonView.getId()) {
		case R.id.pay_ofline:
			paystyle = 0;
			break;
		case R.id.pay_online:
			paystyle = 1;
			break;

		}

	}

	public void setnote() {
		final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);// 调用输入法显示
		dlg.show();
		window.setContentView(R.layout.wemall_order_note);
		ViewGroup noteclose = (ViewGroup) window.findViewById(R.id.noteclose);
		final EditText notedetails = (EditText) window
				.findViewById(R.id.notedetails);
		noteclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				updatenote(notedetails.getText().toString());

			}
		});
	}

	// 更新备注的方法
	public void updatenote(String note) {
		((MainUIMain)getActivity()).HideKeyboard();
		this.note.setText(note);
	}

	@SuppressLint("HandlerLeak")
	public void submit() {
		if (GetPreferencesUid().toString().equals("NULL")) {
			Toast.makeText(getActivity(), "要先登录才能下单哦...", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (paystyle == 1) {
			Toast.makeText(getActivity(), "暂不提供在线支付,请选择货到付款",
					Toast.LENGTH_SHORT).show();
			return;
		}
		cartBar.setVisibility(View.VISIBLE);
		Runnable runnable = new Runnable() {
			public void run() {
				try {

					state = NetSubmitOrder.getData("uid=" + GetPreferencesUid()
							+ "&totalprice="
							+ df.format(wemalldb.read_carttotal())
							+ "&paystyle=" + "货到付款" + "&paystatus=0" + "&note="
							+ note.getText().toString() + "&cartdata="
							+ wemalldb.read_cartdatails());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0x42199);
			}
		};

		try {
			// 开启线程
			new Thread(runnable).start();
			// handler与线程之间的通信及数据处理
			handler = new Handler() {
				public void handleMessage(Message msg) {
					if (msg.what == 0x42199) {
						result();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void result() {
		// if (!(state == 1)) {
		// cartBar.setVisibility(View.GONE);
		// Toast.makeText(getActivity(), "连接服务器异常,请稍后再试", Toast.LENGTH_SHORT)
		// .show();
		// } else if (state == 1) {
		// state = -1;
		wemalldb.clear_cart();
		note.setText("");
		InitCart();
		cartBar.setVisibility(View.GONE);
		Toast.makeText(getActivity(), "订单提交成功", Toast.LENGTH_SHORT).show();
		// }
	}

	public String GetPreferencesUid() {
		String useruid = userinfo.getString("useruid", "NULL");
		return useruid;
	}

	// U盟统计
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("购物车"); // 统计页面
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("购物车");
	}

}