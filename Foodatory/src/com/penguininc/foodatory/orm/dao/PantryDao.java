package com.penguininc.foodatory.orm.dao;

import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.penguininc.foodatory.orm.object.Pantry;

public interface PantryDao extends Dao<Pantry, Integer> {
	
	public List<Pantry> queryForType(int type, int retention);
	
}