package com.penguininc.foodatory.orm.object;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.penguininc.foodatory.orm.dao.impl.ShoppingListDaoImpl;

@DatabaseTable(tableName="shopping_list", daoClass=ShoppingListDaoImpl.class)
public class ShoppingListItem
		implements Serializable {

	/*
	 * Version number for Serializing 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Constant key for serializing object
	 */
	public final static String KEY = "shopping_list_item_key";
	
	@DatabaseField(columnName="shopping_list_id",
			generatedId = true)
	int id;
	
	@DatabaseField(columnName="checked")
	boolean checked;
	
	@DatabaseField(columnName="product_id", canBeNull = false,
			foreign = true,	foreignAutoRefresh = true)
	Product product;
	
	@DatabaseField(columnName="qty")
	int qty;
	
	public ShoppingListItem() {
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
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
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