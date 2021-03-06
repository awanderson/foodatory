package com.penguininc.foodatory.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.orm.object.Product;

public class ProductListAdapter extends ArrayAdapter<Product>
	implements Filterable{
	
	private Context context;
	private List<Product> products;
	private List<Product> original_products;
	
	public ProductListAdapter(Context context, List<Product> products) {
		super(context, R.layout.list_item_product, products);
		this.context = context;
		Collections.sort(products, new ProductListAdapterComparator());
		this.products = products;
		this.original_products = new ArrayList<Product>(products);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		if (convertView == null) {
			mHolder = new ViewHolder();
	        LayoutInflater mInflater = (LayoutInflater)
	               context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        convertView = mInflater.inflate(R.layout.list_item_product, null);
	        mHolder.product_freshness = (TextView)convertView.findViewById(R.id.product_freshness);
	        mHolder.product_name = (TextView)convertView.findViewById(R.id.product_name);
	        mHolder.product_qty = (TextView)convertView.findViewById(R.id.product_qty);
	        mHolder.product_freshness_wrapper = (LinearLayout)convertView.
	        		findViewById(R.id.product_freshness_wrapper);
	        mHolder.product_qty_wrapper = (LinearLayout)convertView.
	        		findViewById(R.id.product_qty_wrapper);
	        convertView.setTag(mHolder);
	    } else {
	    	mHolder = (ViewHolder)convertView.getTag();
	    }
		
		Product p = products.get(position);
	
		mHolder.product_name.setText(p.getProductName());
		// hide our freshness length if we don't have fresh food
		if(p.getType() != Product.FRESH_FOOD) {
			mHolder.product_freshness_wrapper.setVisibility(View.GONE);
		}
		// hide our qty if we have a condiment
		if(p.getType() == Product.CONDIMENT) {
			mHolder.product_qty_wrapper.setVisibility(View.GONE);
		}
		mHolder.product_qty.setText(String.valueOf(p.getQty()));
		mHolder.product_freshness.setText(String.valueOf(p.getFreshLength()));
		
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
	
	public static class ViewHolder {
		TextView product_name, product_qty, product_freshness;
		LinearLayout product_qty_wrapper, product_freshness_wrapper;
	}
	
	@Override
	public void add(Product product) {
		super.add(product);
		Collections.sort(products, new ProductListAdapterComparator());
	}
	
	public class ProductListAdapterComparator
			implements Comparator<Product> {
		
		@Override
		public int compare(Product p1, Product p2) {
			return p1.getProductName().compareTo(
					p2.getProductName());
					
		}
		
	}
	
}