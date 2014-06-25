package com.penguininc.foodatory.dailog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.RecipeProductManagerFragment;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.sqlite.model.RecipeProduct;
import com.penguininc.foodatory.view.CounterView;

public class QuantityPickerDialog extends DialogFragment {
	
	CounterView productQty;
	int multiplier;
	public final static int EDIT_VALUE = 1;
	public final static String EDIT_KEY = "edit_key";
	
	public final static String STARTING_VALUE_KEY = "starting_value";
	private final static int DEFAULT_STARTING_VALUE = 1;
	
	public final static String CHOSEN_QUANTITY = "chosen_quantity";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//no title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
		View view = inflater.inflate(R.layout.dialog_quantity_picker, container);
		
		productQty = (CounterView)view.findViewById(R.id.product_qty);
		/*
		if(getArguments().getInt(Product.PRODUCT_QTY) == 0) {
			multiplier = getArguments().getInt(Product.PRODUCT_QTY, 1);
		} else {
			multiplier = 1;
		}
		*/
		
		multiplier = 1;
		
		int startingValue = getArguments().getInt(STARTING_VALUE_KEY);
		
		if(startingValue != 0) {
			productQty.setValue(startingValue);
		} else {
			productQty.setValue(DEFAULT_STARTING_VALUE);
		}
		
		Button save = (Button)view.findViewById(R.id.save);
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				//send back the arguments we got
				i.putExtras(getArguments());
				
				//change result code if we're editing an existing recipeproduct
				if(getArguments().getInt(EDIT_KEY) == EDIT_VALUE) {
					//put current recipe id in bundle
					i.putExtra(RecipeProduct.RECIPE_PRODUCT_ID, getArguments().getLong(RecipeProduct.RECIPE_PRODUCT_ID));
					getTargetFragment().onActivityResult(getTargetRequestCode(), RecipeProductManagerFragment.EDIT_PRODUCT_QUANTITY, i);
				} else {
					i.putExtra(CHOSEN_QUANTITY, productQty.getValue());
					getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
				}
				dismiss();
			}
		});
		
		// only display delete button if we're editing existing delete button
		if(getArguments().getInt(EDIT_KEY) == EDIT_VALUE) {
			Button delete = (Button)view.findViewById(R.id.delete);
			delete.setVisibility(View.VISIBLE);
			delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					//put current recipe id in bundle
					i.putExtra(RecipeProduct.RECIPE_PRODUCT_ID, getArguments().getLong(RecipeProduct.RECIPE_PRODUCT_ID));
					i.putExtra(Product.PRODUCT_ID, getArguments().getLong(Product.PRODUCT_ID));
					i.putExtra(Product.PRODUCT_DELETE, true);
					getTargetFragment().onActivityResult(getTargetRequestCode(), RecipeProductManagerFragment.DELETE_PRODUCT, i);
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