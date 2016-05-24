package cn.edu.zzu.wemall.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.zzu.wemall.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderPopAdapter extends BaseAdapter {
	/**
	 * @author liudewei-zzu 订单详情adapter
	 * 
	 */

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private HashMap<String, String> popitem;
	private LayoutInflater inflater = null;
	private TextView popitemname, popitemnum, popitemprice, popitemt1total;
	private View vi;
	private DecimalFormat df = new DecimalFormat("0.#");
	public OrderPopAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return data.size();
	}

	public HashMap<String, String> getItem(int position) {

		return this.data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// 重设数据源,避免adapter.notifyDataSetChanged()无响应
	public void set_datasource(ArrayList<HashMap<String, String>> d) {
		this.data = d;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		popitem = data.get(position);
		vi = convertView;
		if (vi == null) {
			vi = inflater.inflate(R.layout.wemall_orderpopwindowitem, null);
		}
		popitemname = (TextView) vi.findViewById(R.id.popitemname);
		popitemnum = (TextView) vi.findViewById(R.id.popitemnum);
		popitemprice = (TextView) vi.findViewById(R.id.popitemprice);
		popitemt1total = (TextView) vi.findViewById(R.id.popitemt1total);
		popitemt1total.setText(df.format((Integer.parseInt(popitem.get("num")
				.toString()) * Double.parseDouble(popitem.get("price")
				.toString()))));
		popitemname.setText(popitem.get("name").toString());
		popitemnum.setText(popitem.get("num").toString());
		popitemprice.setText(popitem.get("price").toString());
		return vi;
	}
}