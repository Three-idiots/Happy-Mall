package cn.edu.zzu.wemall.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.adapter.CartAdapter.CartitemListener;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.mylazylist.ImageLoader;
import cn.edu.zzu.wemall.ui.MyFavourite;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoveAdapter extends BaseAdapter{
	
	private ArrayList<HashMap<String, Object>> data;
	private HashMap<String, Object> item;
	private ImageLoader imageLoader;
	private Activity activity;
	
	public LoveAdapter(Activity activity,ArrayList<HashMap<String, Object>> data){
		this.data=data;
		this.activity=activity;
		this.imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		
		return data==null?0:data.size();
	}

	@Override
	public HashMap<String, Object> getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Viewholder holder=new Viewholder();
		LayoutInflater inflater = LayoutInflater.from(activity); 
		if (convertView == null) { 
		convertView = inflater.inflate(R.layout.wemall_item_love, null); 
		item=data.get(position);
		holder.imgurl=(ImageView) convertView.findViewById(R.id.item_imgurl);
		holder.name=(TextView) convertView.findViewById(R.id.item_name);
		holder.name.setText(item.get("name").toString());
		holder.intro=(TextView) convertView.findViewById(R.id.item_intro);
		holder.intro.setText(item.get("describe").toString());
		imageLoader.DisplayImage(MyConfig.SERVERADDRESSBASE + "Public/Uploads/" + item.get("imgurl"),
				holder.imgurl);
		}
		return convertView;
	}

	public final class Viewholder 
	{
		public ImageView imgurl;
		public TextView name,intro;
	}
}
