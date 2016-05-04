package cn.edu.zzu.wemall.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.database.SQLProcess;
import cn.edu.zzu.wemall.mylazylist.ImageLoader;
import cn.edu.zzu.wemall.ui.MainUiCart;

public class CartAdapter extends BaseAdapter {
	/**
	 * 
	 * @author liudewei-zzu 购物车adapter
	 * 
	 * 
	 */
	private View footerView, view;
	private TextView summary;
	private SQLProcess wemalldb;
	private DecimalFormat df = new DecimalFormat(".#");
	private ViewGroup cartnogoods, cartlist;
	private CartAdapter adapter;
	private ListFragment listFragment;
	private ArrayList<HashMap<String, Object>> cart;
	private Activity activity;
	private ArrayList<HashMap<String, Object>> data;
	private HashMap<String, Object> cartitem;
	private LayoutInflater inflater = null;
	private ImageView itemheader, cartitemnumcut, cartitemnumadd, cartitemcut;
	private TextView cartitemname, cartitemnum, cartitemprice;
	private View vi;
	private ImageLoader imageLoader;
	private int num = 0;

	public CartAdapter(Activity a, ArrayList<HashMap<String, Object>> d, ListFragment l) {
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(activity.getApplicationContext());
		this.listFragment = l;
	}

	public int getCount() {
		return data.size();
	}

	public HashMap<String, Object> getItem(int position) {

		return this.data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// 重设数据源,避免adapter.notifyDataSetChanged()无响应
	public void set_datasource(ArrayList<HashMap<String, Object>> d) {
		this.data = d;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		cartitem = data.get(position);
		vi = convertView;
		CartitemListener cartitemListener = null;
		if (vi == null) {
			vi = inflater.inflate(R.layout.wemall_cart_item, null);
		}
		itemheader = (ImageView) vi.findViewById(R.id.itemheader);
		cartitemname = (TextView) vi.findViewById(R.id.cartitemname);
		cartitemnum = (TextView) vi.findViewById(R.id.cartitemnum);
		cartitemprice = (TextView) vi.findViewById(R.id.cartitemprice);
		cartitemnumcut = (ImageView) vi.findViewById(R.id.cartitemnumcut);
		cartitemnumadd = (ImageView) vi.findViewById(R.id.cartitemnumadd);
		cartitemcut = (ImageView) vi.findViewById(R.id.cartitemcut);

		view = (View) activity.findViewById(R.layout.wemall_tab_cart);
		cartlist = (ViewGroup) activity.findViewById(R.id.cartlist);
		cartnogoods = (ViewGroup) activity.findViewById(R.id.cartnogoods);
		footerView = (View) activity.findViewById(R.layout.wemall_cart_footer);
		summary = (TextView) activity.findViewById(R.id.summary);

		wemalldb = new SQLProcess(activity);
		cart = wemalldb.read_cart();
		adapter = new CartAdapter(activity, cart, listFragment);

		cartitemListener = new CartitemListener(position);
		cartitemnumcut.setOnClickListener(cartitemListener);
		cartitemnumadd.setOnClickListener(cartitemListener);
		cartitemcut.setOnClickListener(cartitemListener);

		cartitemname.setText(cartitem.get("name").toString());
		cartitemnum.setText(cartitem.get("num").toString());
		cartitemprice.setText(cartitem.get("price").toString());
		imageLoader.DisplayImage(MyConfig.SERVERADDRESSBASE + "Public/Uploads/" + cartitem.get("imgurl"), itemheader);
		return vi;

	}
	// 设置点击事件，主要有修改数量，删除商品

	public class CartitemListener implements OnClickListener {

		int cartposition;

		public CartitemListener(int inposition) {
			// TODO Auto-generated constructor stub
			cartposition = inposition;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.cartitemnumcut:
				num = Integer.parseInt(data.get(cartposition).get("num").toString());
				if (num == 0) {

				} else if (num > 0) {
					data.get(cartposition).put("num", --num);
					notifyDataSetChanged();
				}
				updatedate(cartposition);
				break;
			case R.id.cartitemnumadd:
				num = Integer.parseInt(data.get(cartposition).get("num").toString());
				data.get(cartposition).put("num", ++num);
				notifyDataSetChanged();
				updatedate(cartposition);
				break;
			case R.id.cartitemcut:

				// 弹窗显示订单内容

				final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				final Dialog dialog = builder.show();
				Window window = dialog.getWindow();
				window.setContentView(R.layout.wemall_cart_dialog);
				TextView cartcuttitle = (TextView) window.findViewById(R.id.cartcuttitle);
				cartcuttitle.setText(data.get(cartposition).get("name").toString());
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
						wemalldb.delete_cartitem(Integer.parseInt(data.get(cartposition).get("id").toString()));
						InitCart();
						dialog.dismiss();
					}
				});
				InitCart();
				break;
			default:
				break;
			}

		}

	}

	// 修改商品数量后写入数据库等
	public void updatedate(int cartposition) {

		wemalldb = new SQLProcess(activity);
		cart = wemalldb.read_cart();
		wemalldb.update_cartitemGoodsnum(Integer.parseInt(cart.get(cartposition).get("id").toString()),
				Integer.parseInt(data.get(cartposition).get("num").toString()));
		wemalldb.update_cartitemGoodssum(Integer.parseInt(cart.get(cartposition).get("id").toString()),
				Double.parseDouble(data.get(cartposition).get("price").toString())
						* Integer.parseInt(data.get(cartposition).get("num").toString()));
		summary.setText(df.format(wemalldb.read_carttotal()));

	}

	public void InitCart() {
		cart = wemalldb.read_cart();
		adapter = new CartAdapter(activity, cart, listFragment);
		Log.d("cart.size", String.valueOf(cart.size()));
		if (cart.size() == 0) {
			cartlist.setVisibility(View.GONE);

			listFragment.getListView().removeFooterView(footerView);
			Log.d("removeFooterView", String.valueOf(listFragment.getListView().removeFooterView(footerView)));
			Log.d("removeFooterView", String.valueOf(listFragment.getListView().getFooterViewsCount()));
			cartnogoods.setVisibility(View.VISIBLE);
		} else {
			cartlist.setVisibility(View.VISIBLE);
			if (listFragment.getListView().getFooterViewsCount() == 0) {
				listFragment.getListView().addFooterView(footerView);
			}
			summary.setText(df.format(wemalldb.read_carttotal()));
			cartnogoods.setVisibility(View.GONE);
		}

		// 重设数据源,否则无法更新listview(继承baseadapter,自定义传入参数导致的问题(请详细了解堆栈,引用相关知识))
		listFragment.setListAdapter(adapter);// 不添加本代码,部分机型的footerview不显示
		adapter.set_datasource(cart);
		adapter.notifyDataSetChanged();
	}
}