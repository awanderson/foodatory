package com.penguininc.foodatory.view;

/**
 * Basic tab view for use with fragments, upto three tabs
 */

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.penguininc.foodatory.R;

public class TabView<D, E, F> extends LinearLayout {
	
	private Button tab1_view;
	private Button tab2_view;
	private Button tab3_view;
	
	private Tab<D> tab1;
	private Tab<E> tab2;
	private Tab<F> tab3;
	
	private int selected_background;
	private int unselected_background;
	private int tab_holder;
	private int tab_number;
	
	public final static int STATE_ONE = 1;
	public final static int STATE_TWO = 2;
	public final static int STATE_THREE = 3;
	
	private int current_state;
	
	private Activity mActivity;
	
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
		setBackgroundResource(R.drawable.tab_holder_background);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v = inflater.inflate(R.layout.view_tab, this, true);
		
		tab1_view = (Button)v.findViewById(R.id.tab1);
		tab2_view = (Button)v.findViewById(R.id.tab2);
		tab3_view = (Button)v.findViewById(R.id.tab3);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
		        R.styleable.TabView, 0, 0);
		selected_background = a.getResourceId(R.styleable.TabView_selected_background, 0);
		unselected_background = a.getResourceId(R.styleable.TabView_unselected_background, 0);
		tab_holder = a.getResourceId(R.styleable.TabView_tab_holder, 0);
		tab_number = a.getInteger(R.styleable.TabView_tab_number, 2);
		a.recycle();
		
		//hide third tab if we're not using it
		if(tab_number == 2) {
			tab3_view.setVisibility(View.GONE);
		}
		
		tab1_view.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				displayTab1();
			}
		});
			
		//dry goods
		tab2_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				displayTab2();
			}
		});
		
		if(tab_number == 3) {
			//condiments
			tab3_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					displayTab3();
				}
			});
		}
	}
	
	public void setFrag1(Tab<D> tab,  String title) {
		tab1 = tab;
		tab1_view.setText(title);
	}
	
	public void setFrag2(Tab<E> tab, String title) {
		tab2 = tab;
		tab2_view.setText(title);
	}
	
	public void setFrag3(Tab<F> tab, String title) {
		tab3 = tab;
		tab3_view.setText(title);
		
	}
	
	public int getCurrentState() {
		return current_state;
	}
	
	public void loadState(int state) {
		if(state == STATE_ONE) {
			displayTab1();
		} else if (state == STATE_TWO){
			displayTab2();
		} else if (state == STATE_THREE){
			displayTab3();
		}
	}
	
	public void setupTabs(Activity activity) {
		this.mActivity = activity;
		
		//display tab1 first by default
		displayTab1();
	}
	
	public void displayTab1() {
		current_state = STATE_ONE;
		tab1_view.setBackgroundResource(selected_background);
		FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
		hideTab2(ft);
		hideTab3(ft);
		if(tab1.getFragment() == null) {
			ft.add(tab_holder, tab1.createFragment(mActivity));
		} else {
			ft.attach(tab1.getFragment());
		}
		ft.commit();
	}
	
	public void displayTab2() {
		current_state = STATE_TWO;
		tab2_view.setBackgroundResource(selected_background);
		FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
		hideTab1(ft);
		hideTab3(ft);
		if(tab2.getFragment() == null) {
			ft.add(tab_holder, tab2.createFragment(mActivity));
		} else {
			ft.attach(tab2.getFragment());
		}
		ft.commit();
	}
	
	public void displayTab3() {
		if(tab_number == 3) {
			current_state = STATE_THREE;
			tab3_view.setBackgroundResource(selected_background);
			FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
			hideTab1(ft);
			hideTab2(ft);
			if(tab3.getFragment() == null) {
				ft.add(tab_holder, tab3.createFragment(mActivity));
			} else {
				ft.attach(tab3.getFragment());
			}
			ft.commit();
		}
	}
	
	private void hideTab1(FragmentTransaction ft) {
		tab1_view.setBackgroundResource(unselected_background);	
		if(tab1.getFragment() != null) {
			ft.detach(tab1.getFragment());
		}
		
	}
	
	private void hideTab2(FragmentTransaction ft) {
		tab2_view.setBackgroundResource(unselected_background);
		if(tab2.getFragment() != null) {
			ft.detach(tab2.getFragment());
		}
	}
	
	private void hideTab3(FragmentTransaction ft) {
		if(tab_number == 3) {
			tab3_view.setBackgroundResource(unselected_background);
			if(tab3.getFragment() != null) {
				ft.detach(tab3.getFragment());
			}
		}
	}
	
}