package com.penguininc.foodatory.orm.object;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.penguininc.foodatory.orm.dao.impl.DirectionDaoImpl;

/**
 * 
 * ORMLite object class
 * 
 * The direction object is used to store a single direction
 * for a recipe. Each direction in a recipe must have 
 * a unique order
 * 
 * @author Alec Anderson
 *
 */

@DatabaseTable(tableName="direction", daoClass = DirectionDaoImpl.class)
public class Direction implements Serializable{
	
	/*
	 * Version number for Serializing
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Constant key for serializing object
	 */
	public final static String KEY = "direction_key";
	
	@DatabaseField(columnName="direction_id", 
			generatedId = true)
	int id;
	
	@DatabaseField(columnName="content")
	String content;
	
	@DatabaseField(columnName="order")
	int order;
	
	@DatabaseField(columnName = "recipe_id", canBeNull = false,
			foreign = true, foreignAutoRefresh = true)
	Recipe recipe;
	
	public Direction() {
		// needed by ormlite
	}

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
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * @return the recipe
	 */
	public Recipe getRecipe() {
		return recipe;
	}

	/**
	 * @param recipe the recipe to set
	 */
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
	
}