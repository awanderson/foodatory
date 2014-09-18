package com.penguininc.foodatory.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.listener.ProductTypeSwitchListener;
import com.penguininc.foodatory.orm.object.Product;

public class ProductTypeView extends LinearLayout {
	
	public int type;
	final private int white;
	final private int black;
	private ProductTypeSwitchListener listener;
	
	final private TextView fresh_food;
	final private TextView dry_good;
	final private TextView condiment;
	
	final private Drawable bread_slice;
	final private Drawable white_bread_slice;
	
	final private Drawable can;
	final private Drawable white_can;
	
	final private Drawable salt_shaker;
	final private Drawable white_salt_shaker;
	
	public ProductTypeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(LinearLayout.HORIZONTAL);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v = inflater.inflate(R.layout.view_product_type, this, true);
		
		fresh_food = (TextView)v.findViewById(R.id.fresh_food);
		dry_good = (TextView)v.findViewById(R.id.dry_good);
		condiment = (TextView)v.findViewById(R.id.condiment);
		
		white = this.getResources().getColor(android.R.color.white);
		black = this.getResources().getColor(android.R.color.black);
		
		// get images
		bread_slice = getContext().getResources().getDrawable(R.drawable.bread_slice);
		bread_slice.setBounds(60, 60, 60, 60);
		
		white_bread_slice  = getContext().getResources().getDrawable(R.drawable.white_bread_slice);
		white_bread_slice.setBounds(60, 60, 60, 60);
		
		can = getContext().getResources().getDrawable(R.drawable.can);
		can.setBounds(60, 60, 60, 60);
		
		white_can = getContext().getResources().getDrawable(R.drawable.white_can);
		white_can.setBounds(60, 60, 60, 60);
		
		salt_shaker = getContext().getResources().getDrawable(R.drawable.salt_shaker);
		salt_shaker.setBounds(60, 60, 60, 60);
		
		white_salt_shaker = getContext().getResources().getDrawable(R.drawable.white_salt_shaker);
		white_salt_shaker.setBounds(60, 60, 60, 60);
		fresh_food.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(type != Product.FRESH_FOOD) {
					changeToFreshFood();
				}
			}
		});
		
		dry_good.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(type != Product.DRY_GOOD) {
					changeToDryGood();
				}
			}
			
		});
		
		condiment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(type != Product.CONDIMENT) {
					changeToCondiment();
				}
			}
			
		});
		
		//default type
		type = Product.FRESH_FOOD;
		changeToFreshFood();
		
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		if(type == Product.FRESH_FOOD) {
			changeToFreshFood();
		} else if(type == Product.DRY_GOOD) {
			changeToDryGood();
		} else if(type == Product.CONDIMENT) {
			changeToCondiment();
		}
	}
	
	private void changeToFreshFood() {
		type = Product.FRESH_FOOD;
		fresh_food.setBackgroundResource(R.drawable.dark_grey_background);
		dry_good.setBackgroundResource(R.drawable.dummy_background);
		condiment.setBackgroundResource(R.drawable.dummy_background);
		fresh_food.setTextColor(white);
		dry_good.setTextColor(black);
		condiment.setTextColor(black);
		fresh_food.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.white_bread_slice, 0, 0);
		dry_good.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.can, 0, 0);
		condiment.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.salt_shaker, 0, 0);
		if(listener != null) 		
			listener.onItemSwitch(Product.FRESH_FOOD);
	
	}
	
	private void changeToDryGood() {
		type = Product.DRY_GOOD;
		fresh_food.setBackgroundResource(R.drawable.dummy_background);
		dry_good.setBackgroundResource(R.drawable.dark_grey_background);
		condiment.setBackgroundResource(R.drawable.dummy_background);
		fresh_food.setTextColor(black);
		dry_good.setTextColor(white);
		condiment.setTextColor(black);
		fresh_food.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bread_slice, 0, 0);
		dry_good.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.white_can, 0, 0);
		condiment.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.salt_shaker, 0, 0);
		if(listener != null) 	
			listener.onItemSwitch(Product.DRY_GOOD);
	}
	
	private void changeToCondiment() {
		type = Product.CONDIMENT;
		fresh_food.setBackgroundResource(R.drawable.dummy_background);
		dry_good.setBackgroundResource(R.drawable.dummy_background);
		condiment.setBackgroundResource(R.drawable.dark_grey_background);
		fresh_food.setTextColor(black);
		dry_good.setTextColor(black);
		condiment.setTextColor(white);
		fresh_food.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bread_slice, 0, 0);
		dry_good.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.can, 0, 0);
		condiment.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.white_salt_shaker, 0, 0);
		if(listener != null) 			
			listener.onItemSwitch(Product.CONDIMENT);
	}
	
	public void setOnItemSwitch(ProductTypeSwitchListener listener) {
		this.listener = listener;
	}
	
}