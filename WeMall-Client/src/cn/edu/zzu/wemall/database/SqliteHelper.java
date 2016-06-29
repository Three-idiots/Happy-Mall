package cn.edu.zzu.wemall.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SqliteHelper extends SQLiteOpenHelper {
	
	private static final int DATABASEVERSION = 1;
	private static final String TABLENAME_FAVOURITE = "Favourite";

	public SqliteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String his = "create table if not exists "+TABLENAME_FAVOURITE+" (id int,name varchar(20),imgurl varchar(40),describe text,price double,priceno double)";
		db.execSQL(his);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
