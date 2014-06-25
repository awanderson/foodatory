package com.penguininc.foodatory.sqlite.helper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.penguininc.foodatory.sqlite.model.Direction;

/**
 * 
 * @author Alec Anderson
 * SQLite Helper class for the directions for recipes
 */

public class DirectionHelper extends SQLiteOpenHelper {
	
	
	private static final String DB_NAME = "directions.sqlite";
	private static final int VERSION = 1;
	
	//Table Name
	private static final String TABLE_DIRECTION = "directions";
	
	//Column name
	private static final String KEY_ID = "_id";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_RECIPE_ID = "recipe_id";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_ORDER = "direction_order";
	
	/*
	 * Table Create Statement
	 */
	private static final String CREATE_DIRECTION = "CREATE TABLE "
			+ TABLE_DIRECTION + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_RECIPE_ID + " INTEGER," + KEY_CONTENT + " TEXT,"
			+ KEY_ORDER + " INTEGER, " + KEY_CREATED_AT + " DATETIME" + ")";
	
	Context context;
	
	public DirectionHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//create table
		db.execSQL(CREATE_DIRECTION);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		//drop previous table
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIRECTION);
		
		//create new table
		onCreate(db);
	}
	
	/**
	 * Creating a Direciton
	 */
	public long createDirection (Direction d) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = directionToContentValues(d);
		
		//insert row
		long direction_id = db.insert(TABLE_DIRECTION, null, values);
		db.close();
		return direction_id;
	}
	
	
	/**
	 * Getting all directions for a given recipe_id
	 */
	
	public ArrayList<Direction> getDirections(long recipe_id) {
		ArrayList<Direction> directions = new ArrayList<Direction>();
		
		String selectQuery = "SELECT * FROM " + TABLE_DIRECTION + " WHERE "
				+ KEY_RECIPE_ID + " = " + recipe_id;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//add every row to list
		if(c.moveToFirst()) {
			directions.add(cursorToDirection(c));
			while(c.moveToNext()) {
				Direction d = cursorToDirection(c);
				directions.add(d);
			}
		}
		db.close();
		return directions;
	}
	
	/**
	 * update a direction
	 */
	
	public int updateDirection(Direction d) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		Log.d("DirectionHelper", "Updating " + d.getContent() + " with count " + d.getOrder());
		
		ContentValues values = directionToContentValues(d);
		int i = db.update(TABLE_DIRECTION, values, KEY_ID + " = ?", new String[]
				{String.valueOf(d.getId()) });
		db.close();
		return i;
	}
	
	/**
	 * Deleting a direction
	 */
	
	public void deleteDirection(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DIRECTION, KEY_ID + " = ?", new String[]
				{ String.valueOf(id) });
		db.close();
	}
	
	/**
	 * private method to convert a direciton to contentValues
	 * @param d direction to convert
	 * @return corresponding contentValues
	 */
	
	private ContentValues directionToContentValues(Direction d) {
		ContentValues values = new ContentValues();
		values.put(KEY_ORDER, d.getOrder());
		values.put(KEY_CONTENT, d.getContent());
		values.put(KEY_RECIPE_ID, d.getRecipeId());
		return values;
	}
	
	/**
	 * private method to convert a cursor to a direction
	 * @param c cursor to convert
	 * @return corresponding direction
	 */
	private Direction cursorToDirection(Cursor c) {
		Direction d = new Direction();
		d.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		long recipeId = c.getInt(c.getColumnIndex(KEY_RECIPE_ID));
		d.setRecipeId(recipeId);
		d.setContent(c.getString(c.getColumnIndex(KEY_CONTENT)));
		d.setOrder(c.getInt(c.getColumnIndex(KEY_ORDER)));
		return d;
	}
	
}