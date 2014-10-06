package com.penguininc.foodatory.dailog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.orm.object.RecipeProduct;
import com.penguininc.foodatory.view.CounterView;

/**
 * Dialog box to pick a quantity. Quantity user selected
 * is returned in the bundle under the CHOSEN_QUANTITY
 * key
 * 
 * Any argument passed in the bundle will be returned
 * to the target fragment
 * 
 * Set the behavior of box by setting the keys
 * in the bundle when you launch the fragment
 * 
 * 
 * The following keys expect the following type:
 * 
 * STARTING_VALUE_KEY -> int
 * MULTIPLIER_KEY -> int >= 1
 * DELETE_TOGGLE_KEY -> boolean
 * DELETE_TARGET_KEY -> int != 0
 * SAVE_TARGET_KEY -> int != 0
 * SUPER_INCREMENTER_KEY -> int > 0
 * SUPER_DECREMENTER_KEY -> int > 0
 * 
 * @author Alec Anderson
 *
 */

public class QuantityPickerDialog extends DialogFragment {
	
	CounterView counterView;
	int multiplier;
	int deleteTargetKey;
	int saveTargetKey;
	boolean deleteToggle;
	
	
	/* 
	 * Various keys to send in bundle to set the behavior
	 * of the QuantityPickerDialog
	 */
	public final static String STARTING_VALUE_KEY = "starting_value";
	public final static String MULTIPLIER_KEY = "multiplier";
	public final static String DELETE_TOGGLE_KEY = "delete_key";
	public final static String DELETE_TARGET_KEY = "delete_target_key";
	public final static String SAVE_TARGET_KEY = "save_target_key";
	public final static String SUPER_INCREMENTER_KEY = "super_incrementer_key";
	public final static String SUPER_DECREMENTER_KEY = "super_decremeneter_key";
	
	/*
	 * Default settings for our various values
	 */
	public final static int DEFAULT_STARTING_VALUE = 1;
	public final static int DEFAULT_MULTIPLIER = 1;
	public final static boolean DEFAULT_DELETE_TOGGLE = false;
	public final static int DEFAULT_DELETE_TARGET = Activity.RESULT_OK;
	public final static int DEFAULT_SAVE_TARGET = Activity.RESULT_OK;
	
	
	/*
	 * Key that contains the value user selected
	 */
	public final static String CHOSEN_QUANTITY = "chosen_quantity";
	
	public final static String DEBUG_TAG = "QuantityPickerDialog";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//no title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.dialog_quantity_picker, container);
		
		counterView = (CounterView)view.findViewById(R.id.quantity);
		
		// get the data sent and send it back
		final Bundle sentArgs = getArguments();
		RecipeProduct rp = (RecipeProduct)
				sentArgs.getSerializable(RecipeProduct.KEY);
		/*
		 * Load our various settings
		 */
		// try loading the multiplier
		int multiplier = getArguments().getInt(MULTIPLIER_KEY);
		if(multiplier < 1) {
			multiplier = DEFAULT_MULTIPLIER;
		}
		
		// try loading the starting value
		int startingValue = getArguments().getInt(STARTING_VALUE_KEY);
		if(startingValue != 0) {
			counterView.setValue(startingValue);
		} else {
			counterView.setValue(DEFAULT_STARTING_VALUE);
		}
		
		// try loading the targets
		deleteTargetKey = getArguments().getInt(DELETE_TARGET_KEY);
		saveTargetKey = getArguments().getInt(SAVE_TARGET_KEY);
		if(deleteTargetKey == 0) {
			deleteTargetKey = DEFAULT_DELETE_TARGET;
		}
		if(saveTargetKey == 0) {
			saveTargetKey = DEFAULT_SAVE_TARGET;
		}
		
		// try loading the super incrementer and decrementer
		int superIncrementer = getArguments().getInt(SUPER_INCREMENTER_KEY);
		int superDecrementer = getArguments().getInt(SUPER_DECREMENTER_KEY);
		if(superIncrementer > 0 && superDecrementer > 0) {
			counterView.setSuperValues(superIncrementer, superDecrementer);
		} else {
			counterView.useThirdWidth();
		}
		
		// delete button toggle
		deleteToggle = getArguments().getBoolean(DELETE_TOGGLE_KEY);
		
		Button save = (Button)view.findViewById(R.id.save);
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra(CHOSEN_QUANTITY, counterView.getValue());
				i.putExtras(sentArgs);
				getTargetFragment().onActivityResult(getTargetRequestCode(), 
						saveTargetKey, i);
				dismiss();
			}
		});
		
		// only display delete button if we're editing existing delete button
		if(deleteToggle) {
			Button delete = (Button)view.findViewById(R.id.delete);
			delete.setVisibility(View.VISIBLE);
			delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtras(sentArgs);
					getTargetFragment().onActivityResult(getTargetRequestCode(),
							deleteTargetKey, i);
					dismiss();
					
				}
			});
		}
		
		return view;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        
        getDialog().getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT);
	}
	
}