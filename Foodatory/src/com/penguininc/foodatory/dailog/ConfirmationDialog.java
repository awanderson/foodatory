package com.penguininc.foodatory.dailog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.penguininc.foodatory.R;

public class ConfirmationDialog extends DialogFragment {
	
	public final static String CAPTION = "caption";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//no title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = inflater.inflate(R.layout.dialog_confirmation, container);
		String caption = getArguments().getString(CAPTION, "Are you sure?");
		 
		TextView captionHolder = (TextView)view.findViewById(R.id.caption);
		captionHolder.setText(caption);
		
		Button yes = (Button)view.findViewById(R.id.yes);
		
		yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtras(getArguments());
				dismiss();
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
			}
		});
		
		Button no = (Button)view.findViewById(R.id.no);
		
		no.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtras(getArguments());
				dismiss();
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, i);
			
			}
		});
		return view;
	}
	
}