package com.penguininc.foodatory.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.sqlite.model.Product;

public class SimpleProductListAdapter extends ArrayAdapter<Product>
	implements Filterable{
	
	private Context context;
	private ArrayList<Product> products;
	private ArrayList<Product> original_products;
	
	public SimpleProductListAdapter(Context context, ArrayList<Product> products) {
		super(context, R.layout.list_item_dialog_product, products);
		this.context = context;
		this.products = products;
		this.original_products = new ArrayList<Product>(products);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
	           LayoutInflater mInflater = (LayoutInflater)
	                   context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	           convertView = mInflater.inflate(R.layout.list_item_dialog_product, null);
	    }
		
		TextView mProductName = (TextView) convertView.findViewById(R.id.product_name);
		//TextView mProductQty = (TextView) convertView.findViewById(R.id.product_qty);
		//TextView mProductFreshness = (TextView) convertView.findViewById(R.id.product_freshness);
	
		mProductName.setText(products.get(position).getProductName());
		//mProductFreshness.setText("Freshness Length: " + String.valueOf(products.get(position).getFreshLength()));
		 
		return convertView;
	}
	
	@Override
	public int getCount() {
	    return products.size();
	}
	
	@Override
	public Filter getFilter() {
		 
		return new Filter() {
			 
			@SuppressWarnings("unchecked")
			@Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {
	             
				// Now we have to inform the adapter about the new list filtered
				products = (ArrayList<Product>) results.values;
				Log.d("ProductListAdapter", "result lenght = " + products.size());
				notifyDataSetChanged();
	        }

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	        	FilterResults results = new FilterResults();
	        	if (constraint == null || constraint.length() == 0) {
	        	    // No filter implemented we return all the list
	        		results.values = new ArrayList<Product>(original_products);
	        	    results.count = original_products.size();
	        	}
	        	 
	        	else {
	        		// We perform filtering operation
	        	    List<Product> nProductList = new ArrayList<Product>();
	        	         
	        	    for (Product p : original_products) {
	        	    	if (p.getProductName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
	        	    		
	        	    		nProductList.add(p);
	        	    }
	        	         
	        	    results.values = nProductList;
	        	    results.count = nProductList.size();
	        	}
	        	 
	        	return results;
	             
	        }
		};
	}
	
}