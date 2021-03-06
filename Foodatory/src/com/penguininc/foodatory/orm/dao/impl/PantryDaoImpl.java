package com.penguininc.foodatory.orm.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.penguininc.foodatory.orm.dao.PantryDao;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.orm.object.Product;

public class PantryDaoImpl extends BaseDaoImpl<Pantry, Integer>
	implements PantryDao {

	private final static String DEBUG_TAG = "PantryDaoImpl";
	
	public PantryDaoImpl(ConnectionSource connectionSource)
			throws SQLException {
		super(connectionSource, Pantry.class);
	}
	
	public PantryDaoImpl(ConnectionSource connectionSource,
			DatabaseTableConfig<Pantry> tableConfig) throws SQLException {
		super(connectionSource, tableConfig);
	}

	@Override
	public List<Pantry> queryForType(int type, int retention) {
		
		// get blank array list first
		List<Pantry> pantries = new ArrayList<Pantry>();
		
		CloseableIterator<Pantry> iterator = 
				this.closeableIterator();
		
		try {
			while(iterator.hasNext()) {
				Pantry pantry = iterator.next();
				// first check if our product still exists
				if(pantry.getProduct() == null) {
					this.delete(pantry);
				} else if(pantry.getProduct().getType() == type) {
					// if we have a fresh food we have to check if its
					// within our retention
					if(retention != -1 && pantry.getProduct().getType()
							== Product.FRESH_FOOD) {
						// right type, now check if it's within our retention time
						int days = (int)( (Calendar.getInstance().getTime().getTime()
								- pantry.getDateExpire().getTime())
								/ (1000 * 60 * 60 * 24));
						
						Log.d(DEBUG_TAG, "retention = " + retention + " days = " + days);
						
						if(days <= (retention+1)) {
							pantries.add(pantry);
						}
					} else {
						pantries.add(pantry);
					}
					
					
				}
			}
		} catch (SQLException e) {
			// called if delete doesn't complete
		} finally {
			try {
				iterator.close();
			} catch (SQLException e) {
				
			}
		}
		
		return pantries;
	}
	
}