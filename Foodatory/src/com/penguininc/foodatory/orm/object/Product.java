package com.penguininc.foodatory.orm.object;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.penguininc.foodatory.orm.dao.impl.ProductDaoImpl;

/**
 * 
 * ORMLite object class
 * 
 * Product contains information for a given item
 * Having a product in the database does not mean
 * that the person has the item in there "pantry"
 * If a person has an item in there "pantry", a 
 * pantry object is created which holds a reference
 * to a product object
 * 
 * @author Alec Anderson
 *
 */

@DatabaseTable(tableName = "product", daoClass = ProductDaoImpl.class)
public class Product 
	implements Serializable {
	
	/*
	 * Version number for Serializing 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Constant for serializing object
	 */
	public final static String KEY = "PRODUCT_KEY";
	
	@DatabaseField(columnName="product_id", generatedId = true)
	int id;
	@DatabaseField(columnName="product_name")
	String product_name;
	@DatabaseField(columnName="qty")
	int qty;
	@DatabaseField(columnName="fresh_length")
	int fresh_length;
	@DatabaseField(columnName="type")
	int type;
	
	/*
	 * Constants for type of food
	 */
	public final static int FRESH_FOOD = 0;
	public final static int DRY_GOOD = 1;
	public final static int CONDIMENT = 2;
	public final static String PRODUCT_TYPE = "product_type";
	
	
	public Product() {
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
	 * @return the product_name
	 */
	public String getProductName() {
		return product_name;
	}


	/**
	 * @param product_name the product_name to set
	 */
	public void setProductName(String productName) {
		this.product_name = productName;
	}


	/**
	 * @return the qty
	 */
	public int getQty() {
		return qty;
	}


	/**
	 * @param qty the qty to set
	 */
	public void setQty(int qty) {
		this.qty = qty;
	}


	/**
	 * @return the fresh_length
	 */
	public int getFreshLength() {
		return fresh_length;
	}


	/**
	 * @param freshLength the fresh_length to set
	 */
	public void setFreshLength(int freshLength) {
		this.fresh_length = freshLength;
	}


	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	
	
}