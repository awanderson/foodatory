package com.penguininc.foodatory.framework;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.penguininc.foodatory.MainActivity;
import com.penguininc.foodatory.interfaces.MainActivityInterface;
import com.penguininc.foodatory.view.OrmLiteFragment;

public abstract class HomeScreenFragment extends OrmLiteFragment {
	
	protected MainActivityInterface mMainActivityListener;
	
	protected abstract int getLayout();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View v = inflater.inflate(getLayout(), container, false);
		setHasOptionsMenu(true);
		return v;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try{
			mMainActivityListener = (MainActivity)activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString()
	                + " must implement MainActivityInterface");
		}
	}
	
}