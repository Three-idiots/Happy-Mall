package cn.edu.zzu.wemall.ui;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.adapter.LoveAdapter;
import cn.edu.zzu.wemall.database.SqliteProcess;
import cn.edu.zzu.wemall.object.GoodsItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

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
		listview.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
				final GoodsItem goodsItem=new GoodsItem();
				HashMap<String , Object> item = data.get(arg2);
				goodsItem.setId((Integer) item.get("id"));
				goodsItem.setName(item.get("name").toString());
				goodsItem.setImage(item.get("imgurl").toString());
				goodsItem.setIntro(item.get("describe").toString());
				goodsItem.setPrice((Double)item.get("price"));
				goodsItem.setPriceno((Double)item.get("priceno"));
				Intent intent = new Intent(MyFavourite.this, ItemDetails.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("t", goodsItem);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
	}
}
