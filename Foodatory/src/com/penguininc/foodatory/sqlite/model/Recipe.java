package com.penguininc.foodatory.sqlite.model;

public class Recipe {
	
	long id;
	String name;
	String description;
	String image;
	
	public final static String RECIPE_ID = "recipe_id";
	
	//constructors
	public Recipe() {
		
	}
	
	public Recipe(String name) {
		this.name = name;
	}
	
	public Recipe(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	//getters
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getImage() {
		return image;
	}
	
	//setters
	public void setId(long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setImage(String image) {
		this.image = image;
	}
}