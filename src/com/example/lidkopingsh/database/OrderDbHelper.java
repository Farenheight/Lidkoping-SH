package com.example.lidkopingsh.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lidkopingsh.database.DataContract.Order;
import com.example.lidkopingsh.database.DataContract.Product;
import com.example.lidkopingsh.database.DataContract.Stone;
import com.example.lidkopingsh.database.DataContract.Task;
import com.example.lidkopingsh.database.DataContract.TaskToProduct;

public class OrderDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Orders.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String NOT_NULL = " NOT NULL";

	private static final String ORDER_TABLE_CREATE = "CREATE TABLE " 
			+ Order.TABLE_NAME + " ("
			+ Order._ID + INTEGER_TYPE + " PRIMARY KEY,"
			+ Order.COLUMN_NAME_ORDER_DATE + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ Order.COLUMN_NAME_ORDER_NUMBER + INTEGER_TYPE + NOT_NULL + " UNIQUE"
			+ Order.COLUMN_NAME_TIME_LAST_UPDATE + INTEGER_TYPE + NOT_NULL + " )";
	private static final String PRODUCT_TABLE_CREATE = "CREATE TABLE "
			+ Product.TABLE_NAME + " (" 
			+ Product._ID + INTEGER_TYPE + " PRIMARY KEY,"
			+ Product.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + NOT_NULL + " UNIQUE,"
			+ Product.COLUMN_NAME_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ Product.COLUMN_NAME_ORDER_NUMBER + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ Product.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP
			+ Product.COLUMN_NAME_FRONT_WORK + TEXT_TYPE + NOT_NULL +COMMA_SEP
			+ Product.COLUMN_NAME_MATERIAL_COLOR + NOT_NULL + TEXT_TYPE + " )";
	private static final String STONE_TABLE_CREATE = "CREATE TABLE " 
			+ Stone.TABLE_NAME + " ("
			+ Stone._ID + INTEGER_TYPE + " PRIMARY KEY,"
			+ Stone.COLUMN_NAME_STONE_ID + INTEGER_TYPE + NOT_NULL + " UNIQUE,"
			+ Stone.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + NOT_NULL + " UNIQUE,"
			+ Stone.COLUMN_NAME_ORNAMENT + TEXT_TYPE + COMMA_SEP
			+ Stone.COLUMN_NAME_TEXTSTYLE + TEXT_TYPE + COMMA_SEP
			+ Stone.COLUMN_NAME_SIDE_BACK_WORK + TEXT_TYPE + COMMA_SEP
			+ Stone.COLUMN_NAME_STONE_MODEL + TEXT_TYPE + NOT_NULL + " )";
	private static final String TASK_TABLE_CREATE = "CREATE TABLE "
			+ Task.TABLE_NAME + " (" + Task._ID + INTEGER_TYPE + " PRIMARY KEY,"
			+ Task.COLUMN_NAME_TASK + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ Task.COLUMN_NAME_TASK_ID + INTEGER_TYPE + NOT_NULL + " UNIQUE" + " )";
	private static final String TASK_TO_PRODUCT_TABLE_CREATE = "CREATE TABLE "
			+ TaskToProduct.TABLE_NAME + " ("
			+ TaskToProduct._ID + INTEGER_TYPE + " PRIMARY KEY,"
			+ TaskToProduct.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ TaskToProduct.COLUMN_NAME_TASK_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ TaskToProduct.COLUMN_NAME_TASK_COMPLETED + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ TaskToProduct.COLUMN_NAME_SORT_ORDER + INTEGER_TYPE + NOT_NULL + " )";

	public OrderDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ORDER_TABLE_CREATE);
		db.execSQL(PRODUCT_TABLE_CREATE);
		db.execSQL(STONE_TABLE_CREATE);
		db.execSQL(TASK_TABLE_CREATE);
		db.execSQL(TASK_TO_PRODUCT_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTable(db, Order.TABLE_NAME);
		dropTable(db, Product.TABLE_NAME);
		dropTable(db, Stone.TABLE_NAME);
		dropTable(db, Task.TABLE_NAME);
		dropTable(db, TaskToProduct.TABLE_NAME);
		onCreate(db);
	}

	private void dropTable(SQLiteDatabase db, String tableName) {
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
	}

}
