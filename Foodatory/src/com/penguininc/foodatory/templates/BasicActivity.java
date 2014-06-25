package com.penguininc.foodatory.templates;


import android.os.Bundle;
import android.view.ViewGroup;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.penguininc.foodatory.R;

public abstract class BasicActivity extends Activity
	implements BasicActivityInterface {
	
	private Fragment mFrag;
	public final static String EMBEDDED = "embedded";
	
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
	
	public void noButton(){}
	
	
}