package com.penguininc.foodatory.dailog;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.utilities.StringUtilities;

public class NewRecipeDialog extends DialogFragment {
	
	public final static String RECIPE_NAME = "recipe_name";
	public final static String RECIPE_DESCRIPTION = "recipe_description";
	
	private EditText mRecipeName;
	private EditText mRecipeDescription;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//no title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		View view = inflater.inflate(R.layout.dialog_new_recipe, container);
		
		mRecipeName = (EditText)view.findViewById(R.id.recipe_name);
		mRecipeDescription = (EditText)view.findViewById(R.id.recipe_description);
		
		Button save = (Button)view.findViewById(R.id.save);
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				if(StringUtilities.checkBasicString(mRecipeName.getText().toString())) {
					i.putExtra(RECIPE_NAME, mRecipeName.getText().toString());
					i.putExtra(RECIPE_DESCRIPTION, mRecipeDescription.getText().toString());
					getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
					dismiss();
				} else {
					// generate toast
					Toast.makeText(getActivity(),"Name Needed",Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		/*
		Button cancel = (Button)view.findViewById(R.id.cancel);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
				dismiss();
			}
		});
		*/
		return view;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        
        getDialog().getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT);
	}
	
}