package com.penguininc.foodatory.orm.object;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.penguininc.foodatory.orm.dao.impl.PantryDaoImpl;

/**
 * 
 * ORMLite object class
 * 
 * The pantry object contains all of the products
 * that you currently own. It contains how much
 * you have of it and when it's going to go bad
 * 
 * @author Alec Anderson
 *
 */

@DatabaseTable(tableName="pantry", daoClass = PantryDaoImpl.class)
public class Pantry 
	implements Serializable{
	
	/*
	 * Version number for Serializing 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Constant key for serializing object
	 */
	public final static String KEY = "PANTRY_KEY";
	
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(canBeNull = false, foreign=true,
			foreignAutoRefresh = true)
	Product product;
	@DatabaseField
	Date date_added;
	@DatabaseField
	Date date_expire;
	@DatabaseField
	int qty;
	
	public Pantry() {
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
	 * @return the date_added
	 */
	public Date getDateAdded() {
		return date_added;
	}

	/**
	 * @param date_added the date_added to set
	 */
	public void setDateAdded(Date dateAdded) {
		this.date_added = dateAdded;
	}

	/**
	 * @return the date_expire
	 */
	public Date getDateExpire() {
		return date_expire;
	}

	/**
	 * @param date_expire the date_expire to set
	 */
	public void setDateExpire(Date dateExpire) {
		this.date_expire = dateExpire;
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

}