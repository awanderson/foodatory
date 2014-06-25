package com.penguininc.foodatory.sqlite.helper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.sqlite.model.RecipeProduct;

public class RecipeProductHelper extends SQLiteOpenHelper {
	
	//Logcat tag
	private static final String LOG = "RecipeProductHelper";
	
	private static final String DB_NAME = "recipe_product.sqlite";
	private static final int VERSION = 1;
	
	//Tables Names
	private static final String TABLE_RECIPE_PRODUCT = "recipe_products";
	
	//Common column names
	private static final String KEY_ID = "_id";
	private static final String KEY_CREATED_AT = "created_at";
	
	//RECIPE_PRODUCT table - column names
	private static final String KEY_PRODUCT_ID = "product_id";
	private static final String KEY_RECIPE_ID = "recipe_id";
	private static final String KEY_PRODUCT_QTY = "product_qty";
	
	/*
	 * Table Create Statements
	 */
	//Recipe Product table create statement
	private static final String CREATE_TABLE_RECIPE_PRODUCT = "CREATE TABLE "
			+ TABLE_RECIPE_PRODUCT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_PRODUCT_ID + " INTEGER," + KEY_RECIPE_ID + " INTEGER,"
			+ KEY_PRODUCT_QTY + " INTEGER,"	+ KEY_CREATED_AT + " DATETIME" + ")";
	
	
	Context context;
	
	public static final long ALL_PRODUCTS = -1;
	
	public RecipeProductHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//create tables
		db.execSQL(CREATE_TABLE_RECIPE_PRODUCT);
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVerison, int newVersion) {
		// on upgrade drop older tables, probably want to change this in future verisons
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_PRODUCT);
 
        // create new tables
        onCreate(db);
        
	}
	
	/**
	 * Creating a RecipeProduct
	 */
	public long createRecipeProduct(RecipeProduct r) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = recipeProductToContentValues(r);
		
		//insert row
		long recipe_product_id = db.insert(TABLE_RECIPE_PRODUCT, null, values);
		Log.d("RecipeProductHelper","Created recipe product recipe_id = " + recipe_product_id);
		db.close();
		return recipe_product_id;
	}
	
	/**
	 * Getting a single recipe_product
	 */
	public RecipeProduct getRecipeProduct(long id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		String selectQuery = "SELECT * FROM " + TABLE_RECIPE_PRODUCT + " WHERE "
				+ KEY_ID + " = " + id;
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		if(c != null)
			c.moveToFirst();
		db.close();
		return cursorToRecipeProduct(c);
	}
	
	/**
	 * Getting all recipe_products for a given recipe
	 */
	
	public ArrayList<RecipeProduct> getAllProducts(long recipe_id) {
		
		ArrayList<RecipeProduct> recipeProducts = new ArrayList<RecipeProduct>();
		String selectQuery = "SELECT * FROM " + TABLE_RECIPE_PRODUCT + " WHERE "
				+ KEY_RECIPE_ID + " = " + recipe_id;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//add every row to list
		if(c.moveToFirst()) {
			recipeProducts.add(cursorToRecipeProduct(c));
			while(c.moveToNext()) {
				RecipeProduct r = cursorToRecipeProduct(c);
				recipeProducts.add(r);
			}
		}
		db.close();
		return recipeProducts;
	}
	
	/**
	 * Getting all recipes_products for a given product
	 */
	
	public ArrayList<RecipeProduct> getAllRecipes(long product_id) {
		ArrayList<RecipeProduct> recipeProducts = new ArrayList<RecipeProduct>();
		String selectQuery = "SELECT * FROM " + TABLE_RECIPE_PRODUCT + " WHERE "
				+ KEY_PRODUCT_ID + " = " + product_id;
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		//add every row to list
		if(c.moveToFirst()) {
			recipeProducts.add(cursorToRecipeProduct(c));
			while(c.moveToNext()) {
				RecipeProduct r = cursorToRecipeProduct(c);
				recipeProducts.add(r);
			}
		}
		db.close();
		return recipeProducts;
	}
	
	/**
	 * Getting all products not in a recipe
	 */
	
	public ArrayList<Product> getProductsNoInRecipe(long recipe_id) {
		
		ArrayList<Product> allProducts = (new ProductHelper(context)).getAllProducts();
		
		//subtract products in a recipe
		if(recipe_id != ALL_PRODUCTS) {
			
			String selectQuery = "SELECT * FROM " + TABLE_RECIPE_PRODUCT + " WHERE "
					+ KEY_RECIPE_ID + " = " + recipe_id;
			
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor c = db.rawQuery(selectQuery, null);
			
			//get a copy of the list
			ArrayList<Product> allProductsCopy = new ArrayList<Product>(allProducts);
			
			while(c.moveToNext()) {
				RecipeProduct r = cursorToRecipeProduct(c);
				for(Product p : allProductsCopy) {
					Log.d("RecipeProductHelper", "comparing " + p.getProductName() + " to " + r.getProduct().getProductName());
					if(p.getId() == r.getProduct().getId()) {
						allProducts.remove(p);
						Log.d("RecipeProductHelper", "subtracting product + " + p.getProductName());
					}
				}
			}
			
			db.close();
		}
		
		return allProducts;
		
	}
	
	/**
	 * update a recipeProduct
	 */
	
	public int updateRecipeProduct(RecipeProduct r) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = recipeProductToContentValues(r);
		int i = db.update(TABLE_RECIPE_PRODUCT, values, KEY_ID + " = ?", new String[]
				{String.valueOf(r.getId()) });
		db.close();
		return i;
	}
	
	/**
	 * Deleting a recipeProduct
	 */
	
	public void deleteRecipeProduct(long id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECIPE_PRODUCT, KEY_ID + " = ?",  new String[]
				{ String.valueOf(id) });
		db.close();
	}
	
	/**
	 * deleting all recipeProducts with certain recipe_id
	 */
	
	public void deleteRecipe(long recipe_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECIPE_PRODUCT, KEY_RECIPE_ID + " = ?", new String[]
				{ String.valueOf(recipe_id) });
		db.close();
	}
	
	/**
	 * deleting all recipeProducts with certain product_id
	 */
	
	public void deleteProduct(long product_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECIPE_PRODUCT, KEY_PRODUCT_ID + " = ?", new String[]
				{ String.valueOf(product_id) });
		db.close();
	}
	
	/**
	 * private method to convert recipeProducts to content values
	 * @param r recipeProduct to convert to
	 * @return correspoinding contentValues
	 */
	private ContentValues recipeProductToContentValues(RecipeProduct r) {
		ContentValues values = new ContentValues();
		values.put(KEY_PRODUCT_ID, r.getProduct().getId());
		values.put(KEY_RECIPE_ID, r.getRecipe().getId());
		values.put(KEY_PRODUCT_QTY, r.getProductQty());
		return values;
	}
	
	/**
	 * private method to convert a cursor to a recipeProduct
	 * @param c cursor to convert to recipeProduct
	 * @return corresponding recipeProduct
	 */
	private RecipeProduct cursorToRecipeProduct(Cursor c) {
		RecipeProduct r = new RecipeProduct();
		r.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		long productId = c.getInt(c.getColumnIndex(KEY_PRODUCT_ID));
		r.setProduct((new ProductHelper(context)).getProduct(productId));
		long recipeId = c.getInt(c.getColumnIndex(KEY_RECIPE_ID));
		r.setRecipe((new RecipeHelper(context)).getRecipe(recipeId));
		r.setProductQty(c.getInt(c.getColumnIndex(KEY_PRODUCT_QTY)));
		return r;
	}
	
	
}