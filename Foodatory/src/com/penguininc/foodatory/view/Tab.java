package com.penguininc.foodatory.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class Tab<F> {
	
	private Class<F> clz;
	private Fragment frag;
	private Bundle bundle;
	
	public Tab(Class<F> clz, Bundle bundle) {
		this.clz = clz;
		this.bundle = bundle;
		frag = null;
	}
	
	public Fragment getFragment() {
		return frag;
	}
	
	public Fragment createFragment(Activity activity) {
		frag = Fragment.instantiate(activity, clz.getName());
		frag.setArguments(bundle);
		return frag;
	}
	
}