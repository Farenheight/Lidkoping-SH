package com.example.lidkopingsh.database;

import android.provider.BaseColumns;

public class DataContract {

	private DataContract() {}
	
	public static abstract class OrderTable implements BaseColumns {
		public static final String TABLE_NAME = "ordertable";
		public static final String COLUMN_NAME_ORDER_NUMBER = "order_number";
		public static final String COLUMN_NAME_ORDER_DATE = "order_date";
		public static final String COLUMN_NAME_CEMETERY = "cemetery";
		public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";
		public static final String COLUMN_NAME_TIME_CREATED = "time_created";
        public static final String COLUMN_NAME_TIME_LAST_UPDATE = "time_last_update";
	}
	
	public static abstract class ProductTable implements BaseColumns {
        public static final String TABLE_NAME = "product";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ORDER_NUMBER = "order_number";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_MATERIAL_COLOR = "material_color";
        public static final String COLUMN_NAME_FRONT_WORK = "front_work";
	}
	
	public static abstract class StoneTable implements BaseColumns {
        public static final String TABLE_NAME = "stone";
        public static final String COLUMN_NAME_STONE_ID = "stone_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_STONE_MODEL = "stone_model";
        public static final String COLUMN_NAME_SIDE_BACK_WORK = "side_back_work";
        public static final String COLUMN_NAME_TEXTSTYLE = "textstyle";
        public static final String COLUMN_NAME_ORNAMENT = "ornament";
	}
	
	public static abstract class TaskTable implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_TASK = "name";
	}
	
	public static abstract class TaskToProductTable implements BaseColumns {
        public static final String TABLE_NAME = "task_to_product";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_TASK_COMPLETED = "completed";
        public static final String COLUMN_NAME_SORT_ORDER = "sort_order";
	}
	
	public static abstract class CustomerTable implements BaseColumns {
        public static final String TABLE_NAME = "customer";
        public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_POSTAL_ADDRESS = "postal_address";
	}
}
