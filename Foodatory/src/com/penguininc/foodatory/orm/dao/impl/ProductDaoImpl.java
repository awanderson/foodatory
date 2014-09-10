package com.penguininc.foodatory.orm.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.penguininc.foodatory.orm.dao.ProductDao;
import com.penguininc.foodatory.orm.object.Product;

public class ProductDaoImpl extends BaseDaoImpl<Product, Integer> 
	implements ProductDao {

	public ProductDaoImpl(ConnectionSource connectionSource)
		      throws SQLException {
		super(connectionSource, Product.class);
	}
	
	public ProductDaoImpl(ConnectionSource connectionSource, 
			DatabaseTableConfig<Product> tableConfig) 
			throws SQLException{
		super(connectionSource, tableConfig);
	}

	@Override
	public List<Product> queryForType(int type) {
		
		// get blank array list first
		List<Product> products = new ArrayList<Product>();
		
		CloseableIterator<Product> iterator = 
				this.closeableIterator();
		try {
			while (iterator.hasNext()) {
				Product product = iterator.next();
				if(product.getType() == type) {
					products.add(product);
				}
			}
		} finally {
			try {
				iterator.close();
			} catch (SQLException e) {
			}
		}
		
		return products;
	}
	
}