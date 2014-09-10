package com.penguininc.foodatory.orm.dao.impl;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.penguininc.foodatory.orm.dao.ShoppingListDao;
import com.penguininc.foodatory.orm.object.ShoppingListItem;

public class ShoppingListDaoImpl extends 
		BaseDaoImpl<ShoppingListItem, Integer>
		implements ShoppingListDao {
	
	public ShoppingListDaoImpl(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, ShoppingListItem.class);
	}
	
	public ShoppingListDaoImpl(ConnectionSource connectionSource,
			DatabaseTableConfig<ShoppingListItem> tableConfig)
			throws SQLException {
		super(connectionSource, tableConfig);
		
	}
	
}