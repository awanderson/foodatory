package com.penguininc.foodatory.framework;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.penguininc.foodatory.interfaces.BasicFragmentInterface;
import com.penguininc.foodatory.view.OrmLiteFragment;

public abstract class BasicFragment extends OrmLiteFragment 
	implements BasicFragmentInterface {

	protected BasicActivityInterface mBasicActivityListener;
	
	protected abstract int getLayout();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
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
			mBasicActivityListener = (BasicActivity)activity;
		}
		catch (ClassCastException e){
			mBasicActivityListener = null;
		}
	}
	
	public void changeTitle(String title) {
		if(mBasicActivityListener != null) {
			mBasicActivityListener.changeTitle(title);
		}
	}
	
}