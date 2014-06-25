package com.penguininc.foodatory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.sqlite.helper.ProductHelper.ProductCursor;
import com.penguininc.foodatory.sqlite.model.Product;

public class ProductCursorAdapter extends CursorAdapter {
	
	private ProductCursor mProductCursor;
	
	public ProductCursorAdapter(Context context, ProductCursor c) {
		super(context, c, 0);
		mProductCursor = c;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Product product = mProductCursor.getProduct();
		TextView mProductName = (TextView) view.findViewById(R.id.product_name);
		TextView mProductQty = (TextView) view.findViewById(R.id.product_qty);
		TextView mProductFreshness = (TextView) view.findViewById(R.id.product_freshness);
	
		mProductName.setText(product.getProductName());
		mProductFreshness.setText("Freshness Length: " + String.valueOf(product.getFreshLength()));
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.list_item_product, parent, false);
	}
	
	
}