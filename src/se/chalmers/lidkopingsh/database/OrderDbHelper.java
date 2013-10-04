package se.chalmers.lidkopingsh.database;

import se.chalmers.lidkopingsh.database.DataContract.CustomerTable;
import se.chalmers.lidkopingsh.database.DataContract.ImageTable;
import se.chalmers.lidkopingsh.database.DataContract.OrderTable;
import se.chalmers.lidkopingsh.database.DataContract.ProductTable;
import se.chalmers.lidkopingsh.database.DataContract.ProductTypeTable;
import se.chalmers.lidkopingsh.database.DataContract.StationTable;
import se.chalmers.lidkopingsh.database.DataContract.StoneTable;
import se.chalmers.lidkopingsh.database.DataContract.TaskTable;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Helper class for database connection. This handles the opening of the
 * database and creates as well as updates the database with all of its tables
 * when needed.
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 * 
 */
class OrderDbHelper extends SQLiteOpenHelper {
 
	/**
	 * Current version of the application's database structure.
	 * If the structure is changed, this version number must be increased.
	 */
	private static final int DATABASE_VERSION = 16;
	private static final String DATABASE_NAME = "Orders.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String CREATE_TABLE = "CREATE TABLE ";
	private static final String PRIMARY_KEY = " PRIMARY KEY";
	private static final String UNIQUE = " UNIQUE";
	private static final String NOT_NULL = " NOT NULL";
	private static final String COMMA_SEP = ", ";

	/**
	 * These variables defines the tables rows more specifically, what type they are and if they should be
	 * unique, not null or primary key.
	 */
	private static final String ORDER_TABLE_CREATE = CREATE_TABLE 
			+ OrderTable.TABLE_NAME + " ("
			+ OrderTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ OrderTable.COLUMN_NAME_ORDER_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ OrderTable.COLUMN_NAME_ORDER_DATE + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ OrderTable.COLUMN_NAME_ORDER_NUMBER + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ OrderTable.COLUMN_NAME_ID_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ OrderTable.COLUMN_NAME_CEMETERY_BOARD + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ OrderTable.COLUMN_NAME_CEMETERY + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ OrderTable.COLUMN_NAME_CEMETERY_BLOCK + TEXT_TYPE + COMMA_SEP
			+ OrderTable.COLUMN_NAME_CEMETERY_NUMBER + TEXT_TYPE + COMMA_SEP
			+ OrderTable.COLUMN_NAME_CUSTOMER_ID + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ OrderTable.COLUMN_NAME_TIME_CREATED + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ OrderTable.COLUMN_NAME_TIME_LAST_UPDATE + INTEGER_TYPE + NOT_NULL + " )";
	
	private static final String IMAGE_TABLE_CREATE = CREATE_TABLE 
			+ ImageTable.TABLE_NAME + " ("
			+ ImageTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ ImageTable.COLUMN_NAME_IMAGE_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ ImageTable.COLUMN_NAME_ORDER_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ ImageTable.COLUMN_NAME_IMAGE + TEXT_TYPE + ") ";
	
	private static final String PRODUCT_TABLE_CREATE = CREATE_TABLE
			+ ProductTable.TABLE_NAME + " (" 
			+ ProductTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ ProductTable.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ ProductTable.COLUMN_NAME_ORDER_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ ProductTable.COLUMN_NAME_PRODUCT_TYPE_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ ProductTable.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP
			+ ProductTable.COLUMN_NAME_FRONT_WORK + TEXT_TYPE + NOT_NULL +COMMA_SEP
			+ ProductTable.COLUMN_NAME_MATERIAL_COLOR + TEXT_TYPE + NOT_NULL + " )";
	
	private static final String STONE_TABLE_CREATE = CREATE_TABLE 
			+ StoneTable.TABLE_NAME + " ("
			+ StoneTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ StoneTable.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ StoneTable.COLUMN_NAME_ORNAMENT + TEXT_TYPE + COMMA_SEP
			+ StoneTable.COLUMN_NAME_TEXTSTYLE + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ StoneTable.COLUMN_NAME_SIDE_BACK_WORK + TEXT_TYPE + COMMA_SEP
			+ StoneTable.COLUMN_NAME_STONE_MODEL + TEXT_TYPE + NOT_NULL + " )";
	
	private static final String PRODUCT_TYPE_TABLE_CREATE = CREATE_TABLE
			+ ProductTypeTable.TABLE_NAME + " ("
			+ ProductTypeTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ ProductTypeTable.COLUMN_NAME_PRODUCT_TYPE_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ ProductTypeTable.COLUMN_NAME_TYPE + TEXT_TYPE + NOT_NULL + " )";
	
	private static final String TASK_TABLE_CREATE = CREATE_TABLE
			+ StationTable.TABLE_NAME + " ("
			+ StationTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ StationTable.COLUMN_NAME_STATION + TEXT_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ StationTable.COLUMN_NAME_STATION_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + " )";
	
	private static final String TASK_TO_PRODUCT_TABLE_CREATE = CREATE_TABLE
			+ TaskTable.TABLE_NAME + " ("
			+ TaskTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ TaskTable.COLUMN_NAME_PRODUCT_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ TaskTable.COLUMN_NAME_STATION_ID + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ TaskTable.COLUMN_NAME_TASK_STATUS + INTEGER_TYPE + NOT_NULL + COMMA_SEP
			+ TaskTable.COLUMN_NAME_SORT_ORDER + INTEGER_TYPE + NOT_NULL + " )";
	
	private static final String CUSTOMER_TABLE_CREATE = CREATE_TABLE
			+ CustomerTable.TABLE_NAME + " ("
			+ CustomerTable._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
			+ CustomerTable.COLUMN_NAME_CUSTOMER_ID + INTEGER_TYPE + NOT_NULL + UNIQUE + COMMA_SEP
			+ CustomerTable.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP
			+ CustomerTable.COLUMN_NAME_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ CustomerTable.COLUMN_NAME_ADDRESS + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ CustomerTable.COLUMN_NAME_EMAIL + TEXT_TYPE + NOT_NULL + COMMA_SEP
			+ CustomerTable.COLUMN_NAME_POSTAL_ADDRESS + TEXT_TYPE + NOT_NULL +" )";

	/**
	 * Create a helper object to create, open, and/or manage the database.
	 * 
	 * @param context to use to open or create the database
	 */
	public OrderDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * Creates all the tables for the database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ORDER_TABLE_CREATE);
		db.execSQL(IMAGE_TABLE_CREATE);
		db.execSQL(PRODUCT_TABLE_CREATE);
		db.execSQL(STONE_TABLE_CREATE);
		db.execSQL(PRODUCT_TYPE_TABLE_CREATE);
		db.execSQL(TASK_TABLE_CREATE);
		db.execSQL(TASK_TO_PRODUCT_TABLE_CREATE);
		db.execSQL(CUSTOMER_TABLE_CREATE);
	}

	/**
	 * Upgrades all the tables of the database.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTable(db, OrderTable.TABLE_NAME);
		dropTable(db, ImageTable.TABLE_NAME);
		dropTable(db, ProductTable.TABLE_NAME);
		dropTable(db, StoneTable.TABLE_NAME);
		dropTable(db, ProductTypeTable.TABLE_NAME);
		dropTable(db, StationTable.TABLE_NAME);
		dropTable(db, TaskTable.TABLE_NAME);
		dropTable(db, CustomerTable.TABLE_NAME);
		onCreate(db);
	}

	/**
	 * Drops a table from the database.
	 * 
	 * @param db The database from which the table should be dropped
	 * @param tableName The table to be dropped
	 */
	private void dropTable(SQLiteDatabase db, String tableName) {
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
	}

}
