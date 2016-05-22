package cn.edu.zzu.wemall.ui;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.adapter.LoveAdapter;
import cn.edu.zzu.wemall.database.SqliteProcess;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;

public class MyFavourite extends Activity {

	private ListView listview;
	private SqliteProcess favourite;
	private ArrayList<HashMap<String, Object>> data;
	private LoveAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wemall_my_favourite);
		listview = (ListView) findViewById(R.id.favourite_list);
		favourite = new SqliteProcess(this);
		data = favourite.read_cart();
		adapter = new LoveAdapter(this, data);
		listview.setAdapter(adapter);
		listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				AlertDialog dialog=new AlertDialog.Builder(MyFavourite.this)
				.setTitle("是否将商品"+data.get(position).get("name").toString()+"从我的收藏中移除？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						favourite.delete_cartitem(Integer.parseInt(data.get(position).get("id").toString()));
						dialog.dismiss();
						data=favourite.read_cart();
						adapter=new LoveAdapter(MyFavourite.this, data);
						listview.setAdapter(adapter);
					}
				}).create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				return true;
			}
		});
	}
}
