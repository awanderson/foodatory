package com.penguininc.foodatory.orm.dao;

import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.penguininc.foodatory.orm.object.Product;

public interface ProductDao extends Dao<Product, Integer> {
	
	public List<Product> queryForType(int type);
	
}