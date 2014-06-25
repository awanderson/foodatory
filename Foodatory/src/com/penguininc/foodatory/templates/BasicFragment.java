package com.penguininc.foodatory.templates;


import com.penguininc.foodatory.interfaces.BasicFragmentInterface;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BasicFragment extends Fragment 
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