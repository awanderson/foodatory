package com.penguininc.foodatory.sqlite.model;

public class Direction {
	
	long id;
	String content;
	int order;
	long recipe_id;
	
	public final static String DIRECTION_ID = "direction_id";
	public final static String DIRECTION_ORDER = "direction_order";
	
	//constructors
	public Direction() {
		
	}
	
	//setters
	public void setId(long id) {
		this.id = id;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setRecipeId(Long id) {
		this.recipe_id = id;
	}
	
	//getter
	public long getId() {
		return this.id;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public long getRecipeId() {
		return this.recipe_id;
	}
	
}