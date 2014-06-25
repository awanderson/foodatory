package com.penguininc.foodatory.sqlite.helper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.penguininc.foodatory.sqlite.model.Product;

public class ProductHelper extends SQLiteOpenHelper {
	
	Context context;
	
	//Logcat tag
	private static final String LOG = "DatabaseHelper";
	
	private static final String DB_NAME = "products.sqlite";
	private static final int VERSION = 1;
	
	//Tables Names
	private static final String TABLE_PRODUCT = "product";
	
	//Common column names
	private static final String KEY_ID = "_id";
	private static final String KEY_CREATED_AT = "created_at";
	
	//PRODUCT Table - column names
	private static final String KEY_PRODUCT_NAME = "product_name";
	private static final String KEY_QTY = "quantity";
	private static final String KEY_FRESH_LENGTH = "fresh_length";
	private static final String KEY_TYPE = "type";
	
	//Array list that stores products to save db reads
	static ArrayList<Product> product_list;
	
	
	/*
	 * Table Create Statements
	 */
	//Product table create statement
	private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
			+ TABLE_PRODUCT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_TYPE + " INTEGER,"
			+ KEY_FRESH_LENGTH + " INTEGER," + KEY_PRODUCT_NAME + " TEXT,"
			+ KEY_QTY + " INTEGER, " + KEY_CREATED_AT + " DATETIME" + ")";
	
	
	public ProductHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//create tables
		db.execSQL(CREATE_TABLE_PRODUCT);
		Log.d("ProductHelper", "creating db");
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVerison, int newVersion) {
		// on upgrade drop older tables, probably want to change this in future verisons
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
 
        // create new tables
        onCreate(db);
		
	}
	
	/**
	 * Creating a Product
	 */
	public long createProduct(Product p) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = productToContentValues(p);
		
		//insert row
		long product_id = db.insert(TABLE_PRODUCT, null, values);
		p.setId(product_id);
		
		//insert to list if it exists
		if(product_list != null) {
			product_list.add(p);
		}
				
		db.close();
		Log.d("ProductHelper", "insert product id = " + product_id);
		return product_id;
	}
	
	/**
	 * Getting a single product
	 */
	public Product getProduct(long product_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		//return offline product if possible
		if(product_list != null) {
			for(Product p : product_list) {
				if(p.getId() == product_id) {
					return p;
				}
			}
		}
		
		String selectQuery = "SELECT * FROM " + TABLE_PRODUCT + " WHERE "
				+ KEY_ID + " = " + product_id;
		
		Cursor c = db.rawQuery(selectQuery, null);
		Log.d("ProductHelper", "Getting product with id = " + product_id);
		if(c != null)
			c.moveToFirst();
		db.close();
		return cursorToProduct(c);
	}
	
	/**
	 * getting all products
	 */
	public ArrayList<Product> getAllProducts() {
		
		//try offline first
		if(product_list != null) {
			return (new ArrayList<Product>(product_list));
		}
		
		ArrayList<Product> products = new ArrayList<Product>();
		String selectQuery = "SELECT * FROM " + TABLE_PRODUCT;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//add every row to list
		if(c.moveToFirst()) {
			products.add(cursorToProduct(c));
			while(c.moveToNext()) {
				Product p = cursorToProduct(c);
				products.add(p);
			}
		}
		db.close();
		//save product list for offline use
		product_list = products;
		//send a copy
		return (new ArrayList<Product>(product_list));
	}
	
	/**
	 * getting all products of a certain type
	 */
	
	public ArrayList<Product> getAllProductsWithType(int type) {
		
		//get entire list (auto gets offline list)
		product_list = getAllProducts();
		ArrayList<Product> sorted_products = new ArrayList<Product>();
		for(Product p : product_list) {
			if(p.getType() == type) {
				sorted_products.add(p);
			}
		}
		return sorted_products;
		
	}
	
	/**
	 * update a product
	 */
	public int updateProduct(Product p) {
		
		//updates offline list
		if(product_list != null) {
			int i = 0;
			for(Product product : product_list) {
				if(product.getId() == p.getId()) {
					//replace old product with new product
					product_list.set(i, p);
					break;
				}
				i++;
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = productToContentValues(p);
		int i = db.update(TABLE_PRODUCT, values, KEY_ID + " = ?", new String[]
				{String.valueOf(p.getId()) });
		db.close();
		return i;
		
		
	}
	
	/**
	 * deleting a product
	 */
	
	public void deleteProduct(long product_id) {
		
		//updates offline list
		if(product_list != null) {
			for(Product p : product_list) {
				if(p.getId() == product_id) {
					product_list.remove(p);
					break;
				}
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PRODUCT, KEY_ID + " = ?", new String[]
				{ String.valueOf(product_id) });
		(new RecipeProductHelper(context)).deleteProduct(product_id);
		(new InventoryHelper(context)).removeProductFromInventory(product_id);
		(new ShoppingListHelper(context)).removeProductFromShoppingList(product_id);
		db.close();
		
		
	}
	
	
	/**
	 * private method to convert a product to content values
	 * @param p product to convert to
	 * @return corresponding to contentvalues
	 */
	
	private ContentValues productToContentValues(Product p) {
		
		ContentValues values = new ContentValues();
		values.put(KEY_PRODUCT_NAME, p.getProductName());
		values.put(KEY_QTY, p.getQty());
		values.put(KEY_FRESH_LENGTH, p.getFreshLength());
		values.put(KEY_TYPE, p.getType());
		return values;
	}
	
	/**
	 * private method to convert a cursor to a product
	 * @param c cursor to convert to product
	 * @return corresponding product
	 */
	private Product cursorToProduct(Cursor c) {
		Product p = new Product();
		Log.d("ProductHelper", "in cursor to product");
		p.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		p.setFreshLength(c.getInt(c.getColumnIndex(KEY_FRESH_LENGTH)));
		p.setProductName(c.getString(c.getColumnIndex(KEY_PRODUCT_NAME)));
		p.setQty(c.getInt(c.getColumnIndex(KEY_QTY)));
		p.setType(c.getInt(c.getColumnIndex(KEY_TYPE)));
		return p;
	}
	
	/**
	 * Getting cursor
	 */
	public ProductCursor getCursor() {
		String selectQuery = "SELECT * FROM " + TABLE_PRODUCT;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		db.close();
		return new ProductCursor(c);
	}
	
	
	
	/**
	 * Convenience class to wrap a cursor that returns rows from 
	 * the "product" table
	 * @author Alec Anderson
	 *
	 */
	
	public static class ProductCursor extends CursorWrapper {
		
		public ProductCursor(Cursor c) {
			super(c);
		}
		
		public Product getProduct() {
			if(isBeforeFirst() || isAfterLast()) {
				return null;
			}
			Product p = new Product();
			p.setId(getInt(getColumnIndex(KEY_ID)));
			p.setFreshLength(getInt(getColumnIndex(KEY_FRESH_LENGTH)));
			p.setProductName(getString(getColumnIndex(KEY_PRODUCT_NAME)));
			p.setQty(getInt(getColumnIndex(KEY_QTY)));
			p.setType(getInt(getColumnIndex(KEY_TYPE)));
			return p;
		}
		
	}
}