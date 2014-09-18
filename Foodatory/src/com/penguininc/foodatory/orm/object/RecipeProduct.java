package com.penguininc.foodatory.orm.object;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.penguininc.foodatory.orm.dao.impl.RecipeProductDaoImpl;



/**
 * 
 * ORMLite object class
 * 
 * Used to relate products and recipes, so we know
 * a product is in a recipe
 * 
 * @author Alec Anderson
 *
 */

@DatabaseTable(tableName="recipe_product", daoClass = RecipeProductDaoImpl.class)
public class RecipeProduct implements Serializable{
	
	/*
	 * Version number for Serializing 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Constant key for serializing object
	 */
	public final static String KEY = "recipe_product_key";
	
	@DatabaseField(generatedId = true)
	int id;
	
	@DatabaseField
	int product_qty;
	
	@DatabaseField(canBeNull = false, foreign = true,
			foreignAutoRefresh = true)
	Product product;
	
	@DatabaseField(canBeNull = false, foreign = true,
			foreignAutoRefresh = true)
	Recipe recipe;
	
	public RecipeProduct() {
		// need by ormlite
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
	 * @return the product_qty
	 */
	public int getProductQty() {
		return product_qty;
	}

	/**
	 * @param product_qty the product_qty to set
	 */
	public void setProductQty(int productQty) {
		this.product_qty = productQty;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
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