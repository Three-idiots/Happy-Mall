package cn.edu.zzu.wemall.database;

import android.database.sqlite.SQLiteDatabase;

public class OperateTable {
	private static final String TABLENAME_history = "address";
	private SQLiteDatabase db = null;

	public OperateTable(SQLiteDatabase db) {
		this.db = db;

	}

	public void insert_his(String address_name) {
		String sql = "INSERT INTO " + TABLENAME_history + "(address_name)" + "VALUES('" + address_name + "')";
		this.db.execSQL(sql);
		this.db.close();
	}

	public void update_his(String address_name) {
		String sql = "UPDATE " + TABLENAME_history + "SET address_name='" + address_name + "'WHERE address_name=" + address_name;
		this.db.execSQL(sql);
		this.db.close();
	}

	public void delete_his(String address_name) {
		String sql = "DELETE FROM " + TABLENAME_history + " WHERE address_name='" + address_name + "'";
		this.db.execSQL(sql);
		this.db.close();
	}

}
