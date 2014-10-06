package com.penguininc.foodatory.orm.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.penguininc.foodatory.orm.DatabaseHelper;
import com.penguininc.foodatory.orm.object.Product;

public interface ProductDao extends Dao<Product, Integer> {
	
	public List<Product> queryForType(int type);
	public void delete(Product product, DatabaseHelper dbHelper)
			throws SQLException;
	
}