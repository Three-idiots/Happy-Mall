package cn.edu.zzu.wemall.adapter;

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

public class MenuAdapter extends BaseAdapter {
	/**
	 * @author liudewei-zzu 菜单adapter
	 */

	private Activity activity;
	private ArrayList<HashMap<String, Object>> data;
	private HashMap<String, Object> menuitem;
	private LayoutInflater inflater = null;
	private TextView menulistitem;
	private View vi;

	public MenuAdapter(Activity a, ArrayList<HashMap<String, Object>> d) {
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
		menuitem = data.get(position);
		vi = convertView;
		if (vi == null) {
			vi = inflater.inflate(R.layout.wemall_menu_item, null);
		}
		menulistitem = (TextView) vi.findViewById(R.id.menulistitem);
		menulistitem.setText(menuitem.get("name").toString());
		return vi;
	}
}