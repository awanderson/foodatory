package com.penguininc.foodatory.sqlite.helper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.penguininc.foodatory.sqlite.model.ShoppingListItem;

public class ShoppingListHelper extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "shoppinglist.sqlite";
	private static final int VERSION = 3;
	
	//Table Name
	private static final String TABLE_SHOPPING_LIST = "shopping_list";
	
	//Column Names
	private static final String KEY_ID = "_id";
	private static final String KEY_CHECKED = "checked";
	private static final String KEY_PRODUCT_ID = "product_id";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_QTY = "qty";
	
	//Array list that stores the shoppinglist to save db reads
	static ArrayList<ShoppingListItem> shopping_list;
	
	//Table create statement
	private static final String CREATE_TABLE_SHOPPING_LIST = "CREATE TABLE "
			+ TABLE_SHOPPING_LIST + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_CREATED_AT + " LONG," + KEY_QTY + " INTEGER,"
			+ KEY_PRODUCT_ID + " LONG," + KEY_CHECKED + " INTEGER)";
	
	Context mContext;
	
	public ShoppingListHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//create table
		db.execSQL(CREATE_TABLE_SHOPPING_LIST);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_LIST);
		//create new table
		onCreate(db);
	}
	
	/**
	 * Creating a shoppingListItem
	 */
	public long createShoppingListItem(ShoppingListItem s) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = shoppingListItemToContentValues(s);
		
		//insert row
		long shoppingListItemId = db.insert(TABLE_SHOPPING_LIST, null, values);
		s.setId(shoppingListItemId);
		
		//insert to list if it exists
		if(shopping_list != null) {
			//get full product from producthelper
			s.setProduct((new ProductHelper(mContext)).getProduct(s.getProduct().getId()));
			shopping_list.add(s);
		}
		db.close();
		return shoppingListItemId;
	}
	
	/** 
	 * Getting a single shopping list item
	 */
	
	public ShoppingListItem getShoppingListItem(long shoppingListItemId) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		//return offline shoppinglist item if possible
		if(shopping_list != null) {
			for(ShoppingListItem s : shopping_list) {
				if(s.getId() == shoppingListItemId) {
					return s;
				}
			}
		}
		
		String selectQuery = "SELECT * FROM " + TABLE_SHOPPING_LIST + " WHERE "
				+ KEY_ID + " = " + shoppingListItemId;
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c != null)
			c.moveToFirst();
		db.close();
		return cursorToShoppingListItem(c);
	}
	
	
	/**
	 * Getting the whole shopping list
	 */
	
	public ArrayList<ShoppingListItem> getShoppingList() {
		
		//try offline first
		if(shopping_list != null) {
			return (new ArrayList<ShoppingListItem>(shopping_list));
		}
		
		ArrayList<ShoppingListItem> shoppingList = new ArrayList<ShoppingListItem>();
		String selectQuery = "SELECT * FROM " + TABLE_SHOPPING_LIST;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//add every row to list
		if(c.moveToFirst()) {
			c.moveToPrevious();
			while(c.moveToNext()) {
				ShoppingListItem s = cursorToShoppingListItem(c);
				Log.d("ShoppingListHelper", c.toString());
				Log.d("ShoppingListHelper", s.getProduct().getProductName());
				shoppingList.add(s);
			}
		}
		db.close();
		//save for offline use
		shopping_list = shoppingList;
		//send copy, not reference to list
		return (new ArrayList<ShoppingListItem>(shopping_list));
	}
	
	
	/**
	 * updating a shopping list item
	 */
	public int updateShoppingListItem(ShoppingListItem s) {
		
		//updates offline list
		if(shopping_list != null) {
			int j = 0;
			for(ShoppingListItem shoppingListItem : shopping_list) {
				if(shoppingListItem.getId() == s.getId()) {
					//replace old inventory with new inventory
					shopping_list.set(j, s);
					break;
				}
				j++;
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = shoppingListItemToContentValues(s);
		int j = db.update(TABLE_SHOPPING_LIST, values, KEY_ID + " = ?", new String[]
				{String.valueOf(s.getId()) });
		db.close();
		return j;
	}
	
	/**
	 * deleting a shopping list item
	 */
	
	public void deleteShoppingListItem(long shoppingListItemId) {
		//update offline list
		if(shopping_list != null) {
			for(ShoppingListItem s : shopping_list) {
				if(s.getId() == shoppingListItemId) {
					shopping_list.remove(s);
					break;
				}
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SHOPPING_LIST, KEY_ID +" = ?", new String[]
				{ String.valueOf(shoppingListItemId) });
		db.close();
	}
	
	
	/**
	 * deleting every shopping list item with given product id
	 */
	
	public void removeProductFromShoppingList(long product_id) {
		
		//check offline list first
		if(shopping_list != null) {
			for(ShoppingListItem s : shopping_list) {
				if(s.getProduct().getId() == product_id) {
					shopping_list.remove(s);
				}
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SHOPPING_LIST, KEY_PRODUCT_ID + " = ?", new String[]
				{ String.valueOf(product_id) });
		db.close();
	}
	
	/**
	 * private method to convert a shopping list item to content values
	 */
	private ContentValues shoppingListItemToContentValues(ShoppingListItem s) {
		
		ContentValues values = new ContentValues();
		values.put(KEY_QTY, s.getQty());
		values.put(KEY_CHECKED, ((s.isChecked())? 1 : 0));
		Log.d("ShoppingListHelper", "content value p id = " + s.getProduct().getId());
		values.put(KEY_PRODUCT_ID, s.getProduct().getId());
		return values;
	}
	
	private ShoppingListItem cursorToShoppingListItem(Cursor c) {
		ShoppingListItem s = new ShoppingListItem();
		s.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		s.setChecked((c.getInt(c.getColumnIndex(KEY_CHECKED)) == 1)? true : false);
		int product_id = (c.getInt(c.getColumnIndex(KEY_PRODUCT_ID)));
		Log.d("ShoppingListHelper", "product id = " + product_id);
		s.setProduct((new ProductHelper(mContext)).getProduct(product_id));
		s.setQty(c.getInt(c.getColumnIndex(KEY_QTY)));
		return s;
	}
}
