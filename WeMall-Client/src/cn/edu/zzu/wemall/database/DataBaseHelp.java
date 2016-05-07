package cn.edu.zzu.wemall.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelp extends SQLiteOpenHelper {
	private static final String DATABASENAME = "Address.db";
	private static final int DATABASEVERSION = 1;
	private static final String TABLENAME_address = "Address";

	public DataBaseHelp(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String his = "CREATE TABLE " + TABLENAME_address + "(" + "id   INTEGER      PRIMARY KEY ,"
				+ "address_name VARCHAR(50)  NOT NULL   )";
		db.execSQL(his);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		String his = "DROP TABLE IF EXISTS " + TABLENAME_address;
		db.execSQL(his);

		this.onCreate(db);
	}

}
