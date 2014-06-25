package com.penguininc.foodatory.interfaces;

import android.app.Activity;

public interface BasicFragmentInterface {
	
	public void onAttach(Activity activity);
	
	public void changeTitle(String title);
}