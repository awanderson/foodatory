package com.penguininc.foodatory.orm.object;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.penguininc.foodatory.orm.dao.impl.RecipeDaoImpl;

@DatabaseTable(tableName="recipe", daoClass=RecipeDaoImpl.class)
public class Recipe implements Serializable {
	
	private static final String DEBUG_TAG = "Recipe";
	
	/*
	 * Version number for Serializing 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Constant key for serializing object
	 */
	public final static String KEY = "recipe_key";
	
	/*
	 * Constants for color
	 */
	public final static int BLUE = 0;
	public final static int GREEN = 1;
	public final static int RED = 2;
	public final static int BLACK = 3;
	
	
	@DatabaseField(generatedId=true)
	int id;
	
	@DatabaseField
	String name;
	
	@DatabaseField
	String description;
	
	@DatabaseField
	String image;
	
	@ForeignCollectionField(eager = true)
	ForeignCollection<Direction> directions;
	
	@ForeignCollectionField(eager = true)
	ForeignCollection<RecipeProduct> recipeProducts;
	
	@DatabaseField
	int color;
	

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	
	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * @param color the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}
	/**
	 * @return the directions
	 */
	public List<Direction> getDirections() {
		if(directions == null) {
			return null;
		}
		CloseableIterator<Direction> iterator = directions.closeableIterator();
		List<Direction> directionsList = new ArrayList<Direction>();
		try {
			while (iterator.hasNext()) {
				Direction direction = iterator.next();
				directionsList.add(direction);
			}
		} finally {
			try {
				iterator.close();
			} catch (SQLException e) {
				
			}
		}
		return directionsList;
		
	}
	
	/**
	 * 
	 * @param direction to add
	 */
	public void addDirection(Direction direction) {
		directions.add(direction);
	}
	
	/**
	 * 
	 * @return the recipeProducts in a list
	 */
	public List<RecipeProduct> getRecipeProducts() {
		if(recipeProducts == null) {
			return null;
		}
		CloseableIterator<RecipeProduct> iterator = recipeProducts.closeableIterator();
		List<RecipeProduct> recipeProductsList = new ArrayList<RecipeProduct>();
		try {
			while(iterator.hasNext()) {
				RecipeProduct rp = iterator.next();
				// make sure our product still exists
				if(rp.getProduct() != null) {
					recipeProductsList.add(rp);
					Log.d(DEBUG_TAG, "not null product");
				} else {
					Log.d(DEBUG_TAG, "null product");
				}
			}
		} finally {
			try {
				iterator.close();
			} catch (SQLException e) {
				
			}
		}
		return recipeProductsList;
	}
	
	/**
	 * 
	 * @param rp recipeProduct to add
	 */
	public void addRecipeProduct(RecipeProduct rp) {
		recipeProducts.add(rp);
	}
}