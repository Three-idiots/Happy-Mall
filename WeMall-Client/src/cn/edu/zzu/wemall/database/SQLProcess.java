package cn.edu.zzu.wemall.database;

import java.util.ArrayList;
import java.util.HashMap;
import cn.zzu.edu.wemall.utils.Utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作
 * 
 * @author liudewei-zzu
 * 
 */
public class SQLProcess {
	private MyDatabaseHelper sqlHelper;
	private SQLiteDatabase db;

	public SQLProcess(Context context) {
		sqlHelper = new MyDatabaseHelper(context, "wemall", null, 1);
		db = sqlHelper.getWritableDatabase();
	}

	/**
	 * 
	 * 插入订单
	 * 
	 * 
	 */
	public void insert_to_cart(int id, String name, String imgurl, int num,
			double price) {

		Cursor cursor = db.query("wemallcart", new String[] { "id" }, "id=?",
				new String[] { id + "" }, null, null, null);
		if (cursor.getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put("id", id);
			values.put("name", name);
			values.put("imgurl", imgurl);
			values.put("num", num);
			values.put("price", price);
			values.put("itemtotal", price * num);
			db.insert("wemallcart", null, values);
		} else {
			ContentValues in = new ContentValues();
			in.put("num", num);
			in.put("itemtotal", price * num);
			db.update("wemallcart", in, "id=?", new String[] { id + "" });
		}

	}

	/**
	 * 
	 * 读取指定ID商品的购买份数
	 * 
	 * 
	 */
	public int read_cart_item_num(int id) {
		int num = 0;
		Cursor cursor = db.query("wemallcart", new String[] { "num" }, "id=?",
				new String[] { id + "" }, null, null, null);
		if (cursor.getCount() == 0) {
			return num;
		} else {
			while (cursor.moveToNext()) {
				num = cursor.getInt(0);
			}
		}
		return num;
	}

	// 更新商品购买件数,此方法由insert_to_order方法自动调用(当检测到该商品已经在购物车中时,调用此方法,仅修改份数即可

	// 读取购物车详细数据----JSon
	public String read_cartdatails() {
		ArrayList<HashMap<String, Object>> order = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> data;
		// 查询的语法，参数1为表名；参数2为表中的列名；参数3为要查询的条件；参数四为对应列的值；该函数返回的是一个游标
		Cursor cursor = db.query("wemallcart", new String[] { "id", "name",
				"num", "price", "itemtotal" }, null, new String[] {}, null,
				null, null);
		// 遍历每一个记录
		while (cursor.moveToNext()) {
			data = new HashMap<String, Object>();
			data.put("name", cursor.getString(cursor.getColumnIndex("name")));
			data.put("num", cursor.getInt(2));
			data.put("price", cursor.getDouble(3));
			order.add(data);
		}
		return Utils.ArrayListToJsonString(order);
	}

	// 读取订单内容
	public ArrayList<HashMap<String, Object>> read_cart() {
		ArrayList<HashMap<String, Object>> order = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> data;
		// 查询的语法，参数1为表名；参数2为表中的列名；参数3为要查询的条件；参数四为对应列的值；该函数返回的是一个游标
		Cursor cursor = db.query("wemallcart", new String[] { "id", "name",
				"imgurl", "num", "price", "itemtotal" }, null, new String[] {},
				null, null, null);
		// 遍历每一个记录
		while (cursor.moveToNext()) {
			data = new HashMap<String, Object>();
			data.put("id", cursor.getInt(0));
			data.put("name", cursor.getString(cursor.getColumnIndex("name")));
			data.put("imgurl",
					cursor.getString(cursor.getColumnIndex("imgurl")));
			data.put("num", cursor.getInt(3));
			data.put("price", cursor.getDouble(4));
			order.add(data);
		}
		return order;
	}

	// 获取订单总价
	public Double read_carttotal() {
		double ordertotal = 0.0;
		// 查询的语法，参数1为表名；参数2为表中的列名；参数3为要查询的条件；参数四为对应列的值；该函数返回的是一个游标
		Cursor cursor = db.query("wemallcart", new String[] { "itemtotal" },
				null, new String[] {}, null, null, null);
		// 遍历每一个记录
		while (cursor.moveToNext()) {
			ordertotal += cursor.getDouble(0);
		}
		return ordertotal;
	}

	// 从数据库删除传入id的商品
	public void delete_cartitem(int id) {
		db.delete("wemallcart", "id=?", new String[] { id + "" });
	}

	// 清空购物车
	public void clear_cart() {
		db.delete("wemallcart", null, null);
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		db.close();
		sqlHelper.close();
	}
}
