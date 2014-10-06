package com.penguininc.foodatory.orm.dao;

import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.penguininc.foodatory.orm.object.Pantry;

public interface PantryDao extends Dao<Pantry, Integer> {
	/**
	 * Get all the pantry entries with specified type and
	 * retention time
	 * @param type of food wanted, follow ints in Product
	 * class
	 * @param retention how many days old can the food be, 
	 * if -1, return all food of given type
	 * @return list of all pantry items
	 */
	public List<Pantry> queryForType(int type, int retention);
	
}