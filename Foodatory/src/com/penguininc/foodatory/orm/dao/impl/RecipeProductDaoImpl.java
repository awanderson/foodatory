package com.penguininc.foodatory.orm.dao.impl;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.penguininc.foodatory.orm.dao.RecipeProductDao;
import com.penguininc.foodatory.orm.object.RecipeProduct;

public class RecipeProductDaoImpl extends BaseDaoImpl<RecipeProduct, Integer>
	implements RecipeProductDao {

	public RecipeProductDaoImpl(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, RecipeProduct.class);
	}
	
	public RecipeProductDaoImpl(ConnectionSource connectionSource,
			DatabaseTableConfig<RecipeProduct> tableConfig) throws SQLException {
		super(connectionSource, tableConfig);
	}
}