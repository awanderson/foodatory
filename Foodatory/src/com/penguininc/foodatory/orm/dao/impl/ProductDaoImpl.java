package com.penguininc.foodatory.orm.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.penguininc.foodatory.orm.DatabaseHelper;
import com.penguininc.foodatory.orm.dao.ProductDao;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.orm.object.RecipeProduct;
import com.penguininc.foodatory.orm.object.ShoppingListItem;

public class ProductDaoImpl extends BaseDaoImpl<Product, Integer> 
	implements ProductDao {

	public ProductDaoImpl(ConnectionSource connectionSource)
		      throws SQLException {
		super(connectionSource, Product.class);
	}
	
	public ProductDaoImpl(ConnectionSource connectionSource, 
			DatabaseTableConfig<Product> tableConfig) 
			throws SQLException{
		super(connectionSource, tableConfig);
	}

	@Override
	public List<Product> queryForType(int type) {
		
		// get blank array list first
		List<Product> products = new ArrayList<Product>();
		
		CloseableIterator<Product> iterator = 
				this.closeableIterator();
		try {
			while (iterator.hasNext()) {
				Product product = iterator.next();
				if(product.getType() == type) {
					products.add(product);
				}
			}
		} finally {
			try {
				iterator.close();
			} catch (SQLException e) {
			}
		}
		
		return products;
	}
	
	@Override
	public void delete(Product product, DatabaseHelper dbHelper) 
			throws SQLException{
		
		// delete all pantry items first
		RuntimeExceptionDao<Pantry, Integer> pantryDao = 
				dbHelper.getPantryRuntimeExceptionDao();
		DeleteBuilder<Pantry, Integer> dbPantry = pantryDao.deleteBuilder();
		dbPantry.where().eq("product_id", product.getId());
		pantryDao.delete(dbPantry.prepare());
		
		// delete all shoppingListItems
		RuntimeExceptionDao<ShoppingListItem, Integer> shoppingListDao = 
				dbHelper.getShoppingListRuntimeExceptionDao();
		DeleteBuilder<ShoppingListItem, Integer> dbSLI = 
				shoppingListDao.deleteBuilder();
		dbSLI.where().eq("product_id", product.getId());
		shoppingListDao.delete(dbSLI.prepare());
		
		// delete all recipeproducts
		RuntimeExceptionDao<RecipeProduct, Integer> recipeProductDao = 
				dbHelper.getRecipeProductRuntimeExceptionDao();
		DeleteBuilder<RecipeProduct, Integer> dbRecipeProduct = 
				recipeProductDao.deleteBuilder();
		dbRecipeProduct.where().eq("product_id", product.getId());
		recipeProductDao.delete(dbRecipeProduct.prepare());
			
		// delete actual product
		this.delete(product);
		
		
	}
}