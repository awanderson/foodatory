package com.penguininc.foodatory.sqlite.helper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.penguininc.foodatory.sqlite.model.Recipe;

public class RecipeHelper extends SQLiteOpenHelper {
	
	Context context;
	
	//Logcat tag
	private static final String LOG = "RecipeHelper";
	
	private static final String DB_NAME = "recipe.sqlite";
	private static final int VERSION = 1;
	
	//Tables Names
	private static final String TABLE_RECIPE = "recipe";
	
	//Common column names
	private static final String KEY_ID = "_id";
	private static final String KEY_CREATED_AT = "created_at";
	
	//RECIPE Table - column names
	private static final String KEY_RECIPE_NAME = "recipe_name";
	private static final String KEY_RECIPE_DESCRIPTION = "description";
	private static final String KEY_RECIPE_IMAGE = "image";

	/*
	 * Table Create Statements
	 */
	//Recipe table create statement
	private static final String CREATE_TABLE_RECIPE = "CREATE TABLE "
			+ TABLE_RECIPE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_RECIPE_NAME + " TEXT," + KEY_RECIPE_DESCRIPTION + " TEXT,"
			+ KEY_RECIPE_IMAGE + " TEXT,"
			+ KEY_CREATED_AT + " DATETIME "	+ ")";
	
	
	public RecipeHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//create tables
		db.execSQL(CREATE_TABLE_RECIPE);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVerison, int newVersion) {
		// on upgrade drop older tables, probably want to change this in future verisons
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
 
        // create new tables
        onCreate(db);
        
		
	}
	
	/**
	 * Creating a Recipe
	 */
	public long createRecipe(Recipe r) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = recipeToContentValues(r);
		
		//insert row
		long recipe_id = db.insert(TABLE_RECIPE, null, values);
		db.close();
		return recipe_id;
	}
	
	/**
	 * Getting a single recipe
	 */
	public Recipe getRecipe(long recipe_id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		String selectQuery = "SELECT * FROM " + TABLE_RECIPE + " WHERE "
				+ KEY_ID + " = " + recipe_id;
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c != null)
			c.moveToFirst();
		db.close();
		return cursorToRecipe(c);
	}
	
	/**
	 * getting all recipes
	 */
	
	public ArrayList<Recipe> getAllRecipes() {
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		String selectQuery = "SELECT * FROM " + TABLE_RECIPE;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//add every row to list
		if(c.moveToFirst()) {
			recipes.add(cursorToRecipe(c));
			while(c.moveToNext()) {
				Recipe r = cursorToRecipe(c);
				recipes.add(r);
			}
		}
		db.close();
		return recipes;
	}
	
	 
	/**
	 * update a recipe
	 */
	
	public int updateRecipe(Recipe r) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = recipeToContentValues(r);
		int i = db.update(TABLE_RECIPE, values, KEY_ID + " = ?", new String[] 
				{String.valueOf(r.getId()) });
		db.close();
		return i;
		
	}
	
	/**
	 * deleting a recipe
	 */
	
	public void deleteRecipe(long recipe_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECIPE, KEY_ID + " = ?", new String[]
				{ String.valueOf(recipe_id) });
		(new RecipeProductHelper(context)).deleteRecipe(recipe_id);
		db.close();
	}
	
	
	/**
	 * private method to convert a recipe to content values
	 * @param r recipe to convert to
	 * @return corresponding to contentValues
	 */
	
	private ContentValues recipeToContentValues(Recipe r) {
		ContentValues values = new ContentValues();
		values.put(KEY_RECIPE_NAME, r.getName());
		values.put(KEY_RECIPE_DESCRIPTION, r.getDescription());
		values.put(KEY_RECIPE_IMAGE, r.getImage());
		return values;
	}
	
	/**
	 * private method to convert a cursor to a recipe
	 * @param c cursor to convert to recipe
	 * @return corresponding recipe
	 */
	private Recipe cursorToRecipe(Cursor c) {
		Recipe r = new Recipe();
		r.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		r.setName(c.getString(c.getColumnIndex(KEY_RECIPE_NAME)));
		r.setDescription(c.getString(c.getColumnIndex(KEY_RECIPE_DESCRIPTION)));
		r.setImage(c.getString(c.getColumnIndex(KEY_RECIPE_IMAGE)));
		return r;
	}
}