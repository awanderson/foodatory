package com.penguininc.foodatory.sqlite.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Inventory {
	
	private long id;
	private Product product;
	private Calendar date_added;
	private int qty;
	private Calendar date_expire;
	private boolean expanded;
	
	public final static String INVENTORY_ID = "inventory_id";
	public final static String INVENTORY_QTY = "inventory_qty";
	
	public Inventory() {
		
	}
	
	//setters
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setDateAdded(Calendar date) {
		this.date_added = date;
	}
	
	public void setDateExpire(Calendar date) {
		this.date_expire = date;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	public long getId() {
		return this.id;
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	public Calendar getDateAdded() {
		return this.date_added;
	}
	
	public Calendar getDateExpire() {
		return this.date_expire;
	}
	
	public int getQty() {
		return this.qty;
	}
	
	public static final String DATE_FORMAT = "yyyyMMddHHmmss";
	private static final SimpleDateFormat dateFormat = new
	   SimpleDateFormat(DATE_FORMAT);
	 
	public static long formatDateAsLong(Calendar cal){
	   return Long.parseLong(dateFormat.format(cal.getTime()));
	}
	 
	public static Calendar getCalendarFromFormattedLong(long l){
	   try {
	                 Calendar c = Calendar.getInstance();
	                 c.setTime(dateFormat.parse(String.valueOf(l)));
	                 return c;
	                  
	          } catch (ParseException e) {
	                 return null;
	          }
	}
	
}