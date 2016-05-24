package cn.edu.zzu.wemall.mylazylist;

import java.util.ArrayList;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.object.GoodsItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
	/**
	 * @author liudewei-zzu 商品adapter，感谢lazyadapter作者
	 * 
	 */

	private Activity activity;
	private ArrayList<GoodsItem> data;
	private static LayoutInflater inflater = null;
	private ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<GoodsItem> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return (position);
	}

	public GoodsItem getGoodItem(int position) {

		return this.data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// 重设数据源,避免adapter.notifyDataSetChanged()无响应
	public void set_datasource(ArrayList<GoodsItem> d) {
		this.data = d;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.wemall_good_item, null);
		GoodsItem item = data.get(position);
		TextView name = (TextView) vi.findViewById(R.id.name);
		TextView intro = (TextView) vi.findViewById(R.id.intro);
		TextView price = (TextView) vi.findViewById(R.id.price);
		TextView priceno = (TextView) vi.findViewById(R.id.priceno);
		ImageView image = (ImageView) vi.findViewById(R.id.header);
		name.setText(item.getName());
		intro.setText(item.getIntro());
		price.setText(item.getPrice().toString());
		priceno.setText(item.getPriceno().toString());
		// 异步加载图像
		imageLoader.DisplayImage(MyConfig.SERVERADDRESSBASE + "Public/Uploads/"
				+ item.getImage(), image);
		return vi;
	}
}