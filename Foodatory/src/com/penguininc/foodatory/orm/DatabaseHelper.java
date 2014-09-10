package com.penguininc.foodatory.orm;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.orm.dao.DirectionDao;
import com.penguininc.foodatory.orm.dao.PantryDao;
import com.penguininc.foodatory.orm.dao.ProductDao;
import com.penguininc.foodatory.orm.dao.RecipeDao;
import com.penguininc.foodatory.orm.dao.RecipeProductDao;
import com.penguininc.foodatory.orm.dao.ShoppingListDao;
import com.penguininc.foodatory.orm.object.Direction;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.orm.object.Recipe;
import com.penguininc.foodatory.orm.object.RecipeProduct;
import com.penguininc.foodatory.orm.object.ShoppingListItem;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application
	private static final String DATABASE_NAME = "foodatory.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the Pantry object
	private PantryDao pantryDao = null;
	private RuntimeExceptionDao<Pantry, Integer> pantryRuntimeDao = null;

	// the DAO object we use to access the Product object
	private ProductDao productDao = null;
	private RuntimeExceptionDao<Product, Integer> productRuntimeDao = null;
	
	// the DAO object we use to access the Recipe object
	private RecipeDao recipeDao = null;
	private RuntimeExceptionDao<Recipe, Integer> recipeRuntimeDao = null;
	
	// the DAO object we use to access the ShoppingListItem object
	private ShoppingListDao shoppingListDao= null;
	private RuntimeExceptionDao<ShoppingListItem, Integer> shoppingListRuntimeDao
			= null;

	// the DAO object we use to access the Direction object
	private DirectionDao directionDao = null;
	private RuntimeExceptionDao<Direction, Integer> directionRuntimeDao = null;
	
	//the DAO object we use to access the RecipeProduct object
	private RecipeProductDao recipeProductDao = null;
	private RuntimeExceptionDao<RecipeProduct, Integer> recipeProductRuntimeDao = null;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Pantry.class);
			TableUtils.createTable(connectionSource, Product.class);
			TableUtils.createTable(connectionSource, Recipe.class);
			TableUtils.createTable(connectionSource, ShoppingListItem.class);
			TableUtils.createTable(connectionSource, Direction.class);
			TableUtils.createTable(connectionSource, RecipeProduct.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Product.class, true);
			TableUtils.dropTable(connectionSource, Pantry.class, true);
			TableUtils.dropTable(connectionSource, Recipe.class, true);
			TableUtils.dropTable(connectionSource, ShoppingListItem.class, true);
			TableUtils.dropTable(connectionSource, Direction.class, true);
			TableUtils.dropTable(connectionSource, RecipeProduct.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Following functions return the Database Access Object for the
	 * various tables in our database. Use these if you need custom 
	 * functions added in the specific interface of the
	 * various DAOs. For example, if you need to call queryForType
	 * to get products for a specific type, you have to use this function
	 * verse getting the RuntimeException. However, then you have to catch 
	 * an SQLException, verse if you're using the generic DAO calls (e.g.
	 * queryForAll), use the RuntimeExceptionDao version so you 
	 * don't have to capture the SQLException
	 */
	public ProductDao getProductDao() throws SQLException {
		if (productDao == null) {
			productDao = getDao(Product.class);
		}
		return productDao;
	}
	
	public PantryDao getPantryDao() throws SQLException {
		if(pantryDao == null) {
			pantryDao = getDao(Pantry.class);
		}
		return pantryDao;
	}
	
	public RecipeDao getRecipeDao() throws SQLException {
		if(recipeDao == null) {
			recipeDao = getDao(Recipe.class);
		}
		return recipeDao;
	}
	
	public ShoppingListDao getShoppingListDao() throws SQLException {
		if(shoppingListDao == null) {
			shoppingListDao = getDao(ShoppingListItem.class);
		}
		return shoppingListDao;
	}
	
	public DirectionDao getDirectionDao() throws SQLException {
		if(directionDao == null) {
			directionDao = getDao (Direction.class);
		}
		return directionDao;
	}
	
	public RecipeProductDao getRecipeProductDao() throws SQLException {
		if(recipeProductDao == null) {
			recipeProductDao = getDao (RecipeProduct.class);
		}
		return recipeProductDao;
	}

	/**
	 * Following functions return the RuntimeExceptionDao for the
	 * various tables in our database. Use these if you only need the 
	 * basic implementation baked into ormlite (e.g. queryForAll). If
	 * you need custom functionality, use the custom interface DAOs, 
	 * which require you to catch an SQLException
	 */
	public RuntimeExceptionDao<Product, Integer> getProductRuntimeExceptionDao() {
		if (productRuntimeDao == null) {
			productRuntimeDao = getRuntimeExceptionDao(Product.class);
		}
		return productRuntimeDao;
	}
	
	public RuntimeExceptionDao<Pantry, Integer> getPantryRuntimeExceptionDao() {
		if(pantryRuntimeDao == null) {
			pantryRuntimeDao = getRuntimeExceptionDao(Pantry.class);
		}
		return pantryRuntimeDao;
	}

	public RuntimeExceptionDao<Recipe, Integer> getRecipeRuntimeExceptionDao() {
		if(recipeRuntimeDao == null) {
			recipeRuntimeDao = getRuntimeExceptionDao(Recipe.class);
		}
		return recipeRuntimeDao;
	}
	
	public RuntimeExceptionDao<ShoppingListItem, Integer> 
			getShoppingListRuntimeExceptionDao() {
		if(shoppingListRuntimeDao == null) {
			shoppingListRuntimeDao = getRuntimeExceptionDao(ShoppingListItem.class);
		}
		return shoppingListRuntimeDao;
	}
	
	public RuntimeExceptionDao<Direction, Integer> getDirectionRuntimeExceptionDao() {
		if(directionRuntimeDao == null) {
			directionRuntimeDao = getRuntimeExceptionDao(Direction.class);
		}
		return directionRuntimeDao;
	}
	
	public RuntimeExceptionDao<RecipeProduct, Integer> 
			getRecipeProductRuntimeExceptionDao() {
		if(recipeProductRuntimeDao == null) {
			recipeProductRuntimeDao = getRuntimeExceptionDao(RecipeProduct.class);
		}
		return recipeProductRuntimeDao;
	}
	
	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		productDao = null;
		productRuntimeDao = null;
		pantryDao = null;
		pantryRuntimeDao = null;
		recipeDao = null;
		recipeRuntimeDao = null;
		shoppingListDao = null;
		shoppingListRuntimeDao = null;
		directionDao = null;
		directionRuntimeDao = null;
		recipeProductDao = null;
		recipeProductRuntimeDao = null;
	}
}
