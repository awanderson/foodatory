package com.penguininc.foodatory.orm.dao.impl;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.penguininc.foodatory.orm.dao.DirectionDao;
import com.penguininc.foodatory.orm.object.Direction;

public class DirectionDaoImpl extends BaseDaoImpl<Direction, Integer>
		implements DirectionDao {
	
	public DirectionDaoImpl(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, Direction.class);
	}
	
	public DirectionDaoImpl(ConnectionSource connectionSource,
			DatabaseTableConfig<Direction> tableConfig) throws SQLException {
		super(connectionSource, tableConfig);
	}
	
}