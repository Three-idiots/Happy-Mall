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
 *
 * 
 */
public class SqliteProcess {
	private SqliteHelper sqlHelper;
	private SQLiteDatabase db;

	public SqliteProcess(Context context) {
		sqlHelper = new SqliteHelper(context, "MyFavourite", null, 1);
		db = sqlHelper.getWritableDatabase();
	}

	/**
	 * 
	 * 插入商品
	 * 
	 * 
	 */
	public void insert_to_cart(int id, String name, String imgurl, 
			String describe,double price,double priceno) {

		Cursor cursor = db.query("Favourite", new String[] { "id" }, "id=?",
				new String[] { id + "" }, null, null, null);
		if (cursor.getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put("id", id);
			values.put("name", name);
			values.put("imgurl", imgurl);
			values.put("describe", describe);
			values.put("price",price);
			values.put("priceno", priceno);
			db.insert("Favourite", null, values);
			
		} 
	}

		// 读取喜爱商品列表
		public ArrayList<HashMap<String, Object>> read_cart() {
			ArrayList<HashMap<String, Object>> order = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> data;
			// 查询的语法，参数1为表名；参数2为表中的列名；参数3为要查询的条件；参数四为对应列的值；该函数返回的是一个游标
			Cursor cursor = db.query("Favourite", new String[] { "id", "name",
					"imgurl", "describe","price","priceno"}, null, new String[] {},
					null, null, null);
			// 遍历每一个记录
			while (cursor.moveToNext()) {
				data = new HashMap<String, Object>();
				data.put("id", cursor.getInt(0));
				data.put("name", cursor.getString(cursor.getColumnIndex("name")));
				data.put("imgurl",
						cursor.getString(cursor.getColumnIndex("imgurl")));
				data.put("describe", cursor.getString(cursor.getColumnIndex("describe")));
				data.put("price", cursor.getDouble(cursor.getColumnIndex("price")));
				data.put("priceno", cursor.getDouble(cursor.getColumnIndex("priceno")));
				
				order.add(data);
			}
			return order;
		}

	/////////////////////////////////////////////////////////////////////////////////////////////
	// 从数据库删除传入id的商品
	public void delete_cartitem(int id) {
		db.delete("Favourite", "id=?", new String[] { id + "" });
	}
	
	/**
	 * 根据Id获取商品是否处于喜爱状态
	 * @param Id  商品Id
	 * @return
	 */
	public boolean exist(String Id) {
        Cursor cursor = db.query("Favourite", new String[]{"id"}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                if (id.equals(Id)) {
                    return true;
                }
            }
        }
        return false;
    }

	/**
	 * 关闭数据库
	 */
	public void close() {
		db.close();
		sqlHelper.close();
	}
}
