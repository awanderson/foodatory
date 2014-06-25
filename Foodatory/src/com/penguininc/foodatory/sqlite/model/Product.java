package com.penguininc.foodatory.sqlite.model;

import java.io.Serializable;

public class Product implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long id;
	String product_name;
	int qty;
	long fresh_length;
	int type;
	
	public final static String PRODUCT_ID = "product_id";
	public final static String PRODUCT_QTY = "product_qty";
	public final static String PRODUCT_DELETE = "product_delete";
	public final static String PRODUCT_TYPE="product_type";
	public final static String PRODUCT_FRESHNESS = "product_freshness";
	public final static String PRODUCT_NAME = "product_name";
	public final static String PRODUCT = "product";
	
	
	public final static int FRESH_FOOD = 0;
	public final static int DRY_GOOD = 1;
	public final static int CONDIMENT = 2;
	
	
	//constructors
	public Product() {
		
	}
	
	public Product(String product_name) {
		this.product_name = product_name;
	}
	
	public Product(long id, String product_name) {
		this.id = id;
		this.product_name = product_name;
	}
	
	//setter
	public void setId(long id) {
		this.id = id;
	}
	
	public void setProductName(String product_name) {
		this.product_name = product_name;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public void setFreshLength(long fresh_length) {
		this.fresh_length = fresh_length;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	//getter
	public long getId() {
		return this.id;
	}
	
	public String getProductName() {
		return this.product_name;
	}
	
	public int getQty() {
		return this.qty;
	}
	
	public long getFreshLength() {
		return this.fresh_length;
	}
	
	public int getType() {
		return this.type;
	}
}