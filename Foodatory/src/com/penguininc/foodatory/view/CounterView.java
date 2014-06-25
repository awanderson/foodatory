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
	
	public CounterView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    
	    TypedArray a = context.obtainStyledAttributes(attrs,
	        R.styleable.CounterView, 0, 0);
	    int value = a.getInteger(R.styleable.CounterView_startingValue, 1);
	    a.recycle();
	    
	    setOrientation(LinearLayout.VERTICAL);
	    
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
	
	
}