package cn.edu.zzu.wemall.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.mylazylist.ImageLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CartAdapter extends BaseAdapter {
	/**
	 * 
	 * @author liudewei-zzu 购物车adapter
	 * 
	 * 
	 */
	private Activity activity;
	private ArrayList<HashMap<String, Object>> data;
	private HashMap<String, Object> cartitem;
	private LayoutInflater inflater = null;
	private ImageView itemheader;
	private TextView cartitemname, cartitemnum, cartitemprice;
	private View vi;
	private ImageLoader imageLoader;

	public CartAdapter(Activity a, ArrayList<HashMap<String, Object>> d) {
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(activity.getApplicationContext());
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
		if (vi == null) {
			vi = inflater.inflate(R.layout.wemall_cart_item, null);
		}
		itemheader = (ImageView) vi.findViewById(R.id.itemheader);
		cartitemname = (TextView) vi.findViewById(R.id.cartitemname);
		cartitemnum = (TextView) vi.findViewById(R.id.cartitemnum);
		cartitemprice = (TextView) vi.findViewById(R.id.cartitemprice);
		cartitemname.setText(cartitem.get("name").toString());
		cartitemnum.setText(cartitem.get("num").toString());
		cartitemprice.setText(cartitem.get("price").toString());
		imageLoader.DisplayImage(MyConfig.SERVERADDRESSBASE + "Public/Uploads/"
				+ cartitem.get("imgurl"), itemheader);
		return vi;
	}
}