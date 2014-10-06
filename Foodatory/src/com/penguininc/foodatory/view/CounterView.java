package com.penguininc.foodatory.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.penguininc.foodatory.R;


public class CounterView extends LinearLayout {
	
	private EditText valueField;
	private Button superIncrementer;
	private Button superDecrementer;
	private LinearLayout superIncrementerWrapper;
	private LinearLayout superDecrementerWrapper;
	
	public CounterView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    
	    TypedArray a = context.obtainStyledAttributes(attrs,
	        R.styleable.CounterView, 0, 0);
	    int value = a.getInteger(R.styleable.CounterView_startingValue, 1);
	    a.recycle();
	    
	    setOrientation(LinearLayout.HORIZONTAL);
	    
	    LayoutInflater inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = inflater.inflate(R.layout.view_counter, this, true);
	    
	    valueField = (EditText)v.findViewById(R.id.counter_holder);
	    valueField.setText(String.valueOf(value));
	    
	    Button increment = (Button)v.findViewById(R.id.increment);
	    increment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int i = Integer.valueOf(valueField.getText().toString());
				i++;
				valueField.setText(String.valueOf(i));
			}
		});	
	    
	    Button decrement = (Button)v.findViewById(R.id.decrement);
	    decrement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int i = Integer.valueOf(valueField.getText().toString());
				if(i > 0) {
					i--;
					valueField.setText(String.valueOf(i));
				}
				
			}
		});
	    
	    // get the super buttons
	    superIncrementer = (Button)v.findViewById(R.id.super_increment);
	    superDecrementer = (Button)v.findViewById(R.id.super_decrement);
	    
	    // get the super button parents (needed for the layout)
	    superIncrementerWrapper = 
	    		(LinearLayout)v.findViewById(R.id.super_increment_wrapper);
	    superDecrementerWrapper = 
	    		(LinearLayout)v.findViewById(R.id.super_decrement_wrapper);
	    // make them invisible by default (turned back on by setSuperValues)
	    superIncrementerWrapper.setVisibility(View.GONE);
	    superDecrementerWrapper.setVisibility(View.GONE);
	    
	}
	
	public int getValue() {
		if(valueField.getText().toString().equals("")) {
			return 1;
		}
		return Integer.valueOf(valueField.getText().toString());
	}
	
	public void setValue(int value) {
		valueField.setText(String.valueOf(value));
	}
	
	/**
	 * use this function when we don't know
	 * always if we're setting super values or not
	 * When super increments or decrements are off,
	 * we use only a third of the width when 
	 * this function is called
	 */
	public void useThirdWidth() {
		// make the parents that contain the width visible
		superIncrementerWrapper.setVisibility(View.VISIBLE);
		superDecrementerWrapper.setVisibility(View.VISIBLE);
		// hide the actual elements
		superIncrementer.setVisibility(View.GONE);
		superDecrementer.setVisibility(View.GONE);
		
	}
	
	
	/**
	 * Set the values for the super incrementer and decrementer
	 * You must call this function for the super incrementer
	 * and decrementer to appear
	 * @param incrementer value to increment and display on button
	 * @param decrementer value to decrement and display on button
	 */
	
	public void setSuperValues(final int incrementer, final int decrementer) {
		
		// change the text of the buttons first
		superIncrementer.setText("+" + String.valueOf(incrementer));
		superDecrementer.setText("+" + String.valueOf(decrementer));
		
		// set our on click listeners
		superIncrementer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int i = Integer.valueOf(valueField.getText().toString());
				i += incrementer;
				valueField.setText(String.valueOf(i));
			}
		});
		
		superDecrementer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int i = Integer.valueOf(valueField.getText().toString());
				if(i > decrementer) {
					i -= decrementer;
					valueField.setText(String.valueOf(i));
				} else {
					// can't go negative, so just set to 0
					valueField.setText(String.valueOf(0));
				}
			}
		});
		// finally make them visible again
		superIncrementerWrapper.setVisibility(View.VISIBLE);
		superDecrementerWrapper.setVisibility(View.VISIBLE);
		
	}
	
	
}