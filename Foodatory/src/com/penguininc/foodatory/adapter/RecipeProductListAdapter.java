package com.penguininc.foodatory.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.orm.object.RecipeProduct;

public class RecipeProductListAdapter extends ArrayAdapter<RecipeProduct> {
	
	private Context context;
	private List<RecipeProduct> recipeProducts;
	
	public RecipeProductListAdapter(Context context, List<RecipeProduct> recipeProducts) {
		super(context, R.layout.list_item_recipe_product, recipeProducts);
		this.context = context;
		Collections.sort(recipeProducts, new RecipeProductListAdapterComparator());
		this.recipeProducts = recipeProducts;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
	           LayoutInflater mInflater = (LayoutInflater)
	                   context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	           convertView = mInflater.inflate(R.layout.list_item_recipe_product, null);
	    }
		 
		TextView mProductName = (TextView) convertView.findViewById(R.id.product_name);
		TextView mProductQty = (TextView) convertView.findViewById(R.id.product_qty);
	
		mProductName.setText(recipeProducts.get(position).getProduct().getProductName());
		mProductQty.setText(String.valueOf(recipeProducts.get(position).getProductQty()));		 
		return convertView;
	}
	
	@Override
	public void add(RecipeProduct recipeProduct) {
		super.add(recipeProduct);
		Collections.sort(recipeProducts, new RecipeProductListAdapterComparator());
	}
	
	public class RecipeProductListAdapterComparator
			implements Comparator<RecipeProduct> {
		
		@Override
		public int compare(RecipeProduct rp1, RecipeProduct rp2) {
			return rp1.getProduct().getProductName().compareTo(
					rp2.getProduct().getProductName());
		}
	}
	
}