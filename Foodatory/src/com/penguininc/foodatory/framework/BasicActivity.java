package com.penguininc.foodatory.framework;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.orm.DatabaseHelper;

public abstract class BasicActivity 
		extends OrmLiteBaseActivity<DatabaseHelper>
		implements BasicActivityInterface {
	
	private Fragment mFrag;
	public final static String EMBEDDED = "embedded";
	
	// if this variable is true, will print debug logs
	public final static boolean DEBUG_MODE = true;
	
	private final static String DEBUG_TAG = "BasicActivity";
	
	protected abstract Fragment createFragment();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		
		getActionBar().setIcon(R.drawable.icon_no_background);
		
		mFrag = createFragment();
		if(mFrag != null) {
			setContentView(R.layout.activity_default);
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Bundle bundle = getIntent().getExtras();
			if(bundle == null) {
				bundle = new Bundle();
			}
			
			//display the fragment dialog embedded, not as a dialog
			bundle.putBoolean(EMBEDDED, true);
			mFrag.setArguments(bundle);
			ft.add(R.id.default_frag_holder, mFrag)
				.commit();
		}
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	public void changeTitle(String title){
		getActionBar().setTitle(title);
	}
	
	
	/**
	 * Prints out debuging message if debugging mode is on
	 * 
	 * Use this over Log.d because this allows for removal
	 * of any logs simply by turning DEBUG_MODE to false
	 * 
	 * @param message you want displayed in log
	 */
	public static void debugLog(String message) {
		debugLog(DEBUG_TAG, message);
	}
	
	/**
	 * Same as debugLog but with an extra debugTag paramter,
	 * should be used over debugLog(message)
	 * 
	 * @param debugTag debug tag you want to display
	 * @param message you want displayed in log
	 */
	public static void debugLog(String debugTag, String message) {
		if(DEBUG_MODE) {
			Log.d(debugTag, message);
		}
	}
}