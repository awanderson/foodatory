package com.penguininc.foodatory.orm.object;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.penguininc.foodatory.orm.dao.impl.RecipeDaoImpl;

@DatabaseTable(tableName="recipe", daoClass=RecipeDaoImpl.class)
public class Recipe implements Serializable {
	
	/*
	 * Version number for Serializing 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * Constant key for serializing object
	 */
	public final static String KEY = "recipe_key";
	
	@DatabaseField(generatedId=true)
	int id;
	
	@DatabaseField
	String name;
	
	@DatabaseField
	String description;
	
	@DatabaseField
	String image;

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
	
	
	
}