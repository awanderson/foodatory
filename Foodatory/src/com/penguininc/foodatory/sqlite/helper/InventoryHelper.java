package com.penguininc.foodatory.sqlite.helper;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.penguininc.foodatory.sqlite.model.Inventory;
import com.penguininc.foodatory.sqlite.model.Product;

public class InventoryHelper extends SQLiteOpenHelper {
	
	//Logcat tag
	private static final String LOG = "InventoryHelper";
	
	private static final String DB_NAME = "inventory.sqlite";
	private static final int VERSION = 1;
	
	//Tables Names
	private static final String TABLE_INVENTORY = "inventory";
	
	//Common column names
	private static final String KEY_ID = "_id";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_PRODUCT_ID = "product_id";
	private static final String KEY_QTY = "qty";
	private static final String KEY_EXPIRE_DATE = "expire_date";
	
	//Array list that stores inventory to save db reads
	static ArrayList<Inventory> inventory_list;
	
	//Table create statement
	private static final String CREATE_TABLE_INVENTORY = "CREATE TABLE "
			+ TABLE_INVENTORY + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_CREATED_AT + " LONG," + KEY_QTY + " INTEGER,"
			+ KEY_PRODUCT_ID + " INTEGER,"
			+ KEY_EXPIRE_DATE + " LONG)";
	
	
	Context mContext;
	
	
	public InventoryHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//create tables
		db.execSQL(CREATE_TABLE_INVENTORY);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVerison, int newVersion) {
		// on upgrade drop older tables, probably want to change this in future verisons
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        // create new tables
        onCreate(db);
	}
	
	/**
	 * Creating a Inventory
	 */
	public long createInventory(Inventory i) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = inventoryToContentValues(i);
		
		
		
		//insert row
		long inventory_id = db.insert(TABLE_INVENTORY, null, values);
		i.setId(inventory_id);
		
		//insert to list if it exists
		if(inventory_list != null) {
			i.setProduct((new ProductHelper(mContext)).getProduct(i.getProduct().getId()));
			inventory_list.add(i);
		}
		
		db.close();
		return inventory_id;
	}
	
	/**
	 * Getting a single inventory
	 */
	
	public Inventory getInventory(long inventory_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		//return offline inventory if posible
		if(inventory_list != null) {
			for(Inventory i : inventory_list) {
				if(i.getId() == inventory_id) {
					return i;
				}
			}
		}
		
		String selectQuery = "SELECT * FROM " + TABLE_INVENTORY + " WHERE "
				+ KEY_ID + " = " + inventory_id;
		
		
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c != null)
			c.moveToFirst();
		db.close();
		return cursorToInventory(c);
	}
	
	/**
	 * Getting all inventories
	 * @param retention is the number of days to keep expired food, -1 meaning keep forever
	 */
	public ArrayList<Inventory> getAllInventories(int retention) {
		
		//try offline first
		if(inventory_list != null) {
			return (new ArrayList<Inventory>(inventory_list));
		}
		
		ArrayList<Inventory> inventories = new ArrayList<Inventory>();
		String selectQuery = "SELECT * FROM " + TABLE_INVENTORY;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		
		//add every row to list
		if(c.moveToFirst()) {
			c.moveToPrevious();
			//inventories.add(cursorToInventory(c));
			while(c.moveToNext()) {
				Inventory i = cursorToInventory(c);
				if(i.getProduct().getType() == Product.FRESH_FOOD) {
					int days = (int)( (Calendar.getInstance().getTime().getTime() - i.getDateExpire().getTime().getTime()) / (1000 * 60 * 60 * 24));
					Log.d("InventoryHelper", "days = " + days);
					//only add the inventory if it's days not fresh is less than our retention time
					if(retention != -1 && days < retention) {
						inventories.add(i);
					}
				} else {
					inventories.add(i);
				}
				
			}
		}
		db.close();
		//save inventory list for offline use
		inventory_list = inventories;
		//send a copy
		return (new ArrayList<Inventory>(inventory_list));
	}
	
	/**
	 * Getting all products of a certain type
	 * @param retention is the number of days to keep expired food, -1 meaning keep forever
	 */
	
	public ArrayList<Inventory> getAllInventoriesWithType(int type, int retention) {
		
		//get entire list (auto gets offline list)
		inventory_list = getAllInventories(retention);
		ArrayList<Inventory> sorted_inventories = new ArrayList<Inventory>();
		for(Inventory i : inventory_list) {
			if(i.getProduct().getType() == type) {
				sorted_inventories.add(i);
			}
		}
		return sorted_inventories;
		
	}
	
	/**
	 * update a product
	 */
	public int updateInventory(Inventory i) {
		
		//updates offline list
		if(inventory_list != null) {
			int j = 0;
			for(Inventory inventory : inventory_list) {
				if(inventory.getId() == i.getId()) {
					//replace old inventory with new inventory
					inventory_list.set(j, i);
					break;
				}
				j++;
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = inventoryToContentValues(i);
		int j = db.update(TABLE_INVENTORY, values, KEY_ID + " = ?", new String[]
				{String.valueOf(i.getId()) });
		db.close();
		return j;
	}
	
	/**
	 * deleting an inventory
	 */
	
	public void deleteInventory(long inventory_id) {
		
		//update offline list
		if(inventory_list != null) {
			for(Inventory i : inventory_list) {
				if(i.getId() == inventory_id) {
					inventory_list.remove(i);
					break;
				}
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_INVENTORY, KEY_ID + " = ?", new String[]
				{ String.valueOf(inventory_id) });
		db.close();
	}
	
	/**
	 * remove all inventories with given product id
	 */
	public void removeProductFromInventory(long product_id) {
		
		//check offline list first
		if(inventory_list != null) {
			for(Inventory i : inventory_list) {
				if(i.getProduct().getId() == product_id) {
					inventory_list.remove(i);
				}
			}
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_INVENTORY, KEY_PRODUCT_ID + " = ?", new String[]
				{ String.valueOf(product_id) });
		db.close();
	}
	
	/**
	 * private method to convert an inventory to content values
	 * @param i inventory to convert to
	 * @return corresponding to contentvalues
	 */
	private ContentValues inventoryToContentValues(Inventory i) {
		
		ContentValues values = new ContentValues();
		values.put(KEY_QTY, i.getQty());
		values.put(KEY_EXPIRE_DATE, Inventory.formatDateAsLong(i.getDateExpire()));
		values.put(KEY_CREATED_AT, Inventory.formatDateAsLong(i.getDateAdded()));
		values.put(KEY_PRODUCT_ID, i.getProduct().getId());
		return values;
	}
	
	private Inventory cursorToInventory(Cursor c) {
		Inventory i = new Inventory();
		i.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		i.setDateAdded(Inventory.getCalendarFromFormattedLong(c.getLong(c.getColumnIndex(KEY_CREATED_AT))));
		i.setDateExpire(Inventory.getCalendarFromFormattedLong(c.getLong(c.getColumnIndex(KEY_EXPIRE_DATE))));
		int product_id = (c.getInt(c.getColumnIndex(KEY_PRODUCT_ID)));
		i.setProduct((new ProductHelper(mContext)).getProduct(product_id));
		i.setQty(c.getInt(c.getColumnIndex(KEY_QTY)));
		return i;
	}
	
}