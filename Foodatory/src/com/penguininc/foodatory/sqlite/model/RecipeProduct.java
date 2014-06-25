package com.penguininc.foodatory.sqlite.model;

/**
 * Class that relates products and recipes together
 * product qty is the number of products needed for a given recipe
 * @author Alec Anderson
 *
 */

public class RecipeProduct {
	
	long id;
	int product_qty;
	Product product;
	Recipe recipe;
	
	public final static String RECIPE_PRODUCT_ID = "recipe_product_id";
	
	//constructor
	public RecipeProduct() {
		
	}
	
	// getters
	public long getId() {
		return id;
	}
	
	public Recipe getRecipe() {
		return recipe;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public int getProductQty() {
		return product_qty;
	}
	
	// setters
	public void setId(long id) {
		this.id = id;
	}
	
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public void setProductQty(int qty) {
		this.product_qty = qty;
	}
	
}