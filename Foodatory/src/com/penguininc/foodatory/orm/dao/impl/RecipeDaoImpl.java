package com.penguininc.foodatory.orm.dao.impl;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.penguininc.foodatory.orm.dao.RecipeDao;
import com.penguininc.foodatory.orm.object.Recipe;

public class RecipeDaoImpl extends BaseDaoImpl<Recipe, Integer>
		implements RecipeDao {
	
	public RecipeDaoImpl(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, Recipe.class);
	}
	
	public RecipeDaoImpl(ConnectionSource connectionSource,
			DatabaseTableConfig<Recipe> tableConfig) throws SQLException {
		super(connectionSource, tableConfig);
	}
	
	@Override
	public int delete(Recipe recipe) 
		throws SQLException{
		
		// delete our image file if we can
		if(recipe.getImage() != null) {
			try {
				File image = new File(recipe.getImage());
				if(image.exists()) {
					image.delete();
				}
			} catch(Exception e) {
			
			}
			
		}
		
		
		return super.delete(recipe);
	}
	
}