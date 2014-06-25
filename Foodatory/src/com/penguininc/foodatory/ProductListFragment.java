package com.penguininc.foodatory;

import java.util.ArrayList;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.penguininc.foodatory.adapter.ProductListAdapter;
import com.penguininc.foodatory.sqlite.helper.ProductHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.templates.BasicFragment;

public class ProductListFragment extends BasicFragment {

	private final static int GET_PRODUCTS = 0;
	private ListView listview;
	ProductListAdapter adapter;
	LoaderCallbacks<ArrayList<Product>> callbacks;
	private int mProductType;
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_listview;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		mProductType = getArguments().getInt(Product.PRODUCT_TYPE, Product.FRESH_FOOD);
		Log.d("ProductListFragment", "product type = " + mProductType);
		
		
		listview = (ListView)view.findViewById(R.id.listview);
		TextView emptyView = (TextView)view.findViewById(R.id.empty);
		if(mProductType == Product.FRESH_FOOD) {
			emptyView.setText("You have no Fresh Food!");
		} else if (mProductType == Product.DRY_GOOD) {
			emptyView.setText("You have no Dry Goods!");
		}else {
			emptyView.setText("You have no Condiments!");
		}
		emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.big_products, 0, 0);
		listview.setEmptyView(emptyView);
		callbacks = (new GenericLoaderCallbacks<Integer, ArrayList<Product>>(getActivity(), mProductType){

			@Override
			protected ArrayList<Product> doInBackground(Integer data) {
				return (new ProductHelper(context)).getAllProductsWithType(data);
			}

			@Override
			protected void loadFinished(ArrayList<Product> output) {
				adapter = new ProductListAdapter(getActivity(), output);
				listview.setAdapter(adapter);
				
			}
			
			@Override
			protected void resetLoader(Loader<ArrayList<Product>> args) {
				listview.setAdapter(null);
			}
			
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Product p = adapter.getItem(position);
				Intent i = new Intent(getActivity(), EditProductActivity.class);
				Bundle b = new Bundle();
				b.putLong(Product.PRODUCT_ID, p.getId());
				i.putExtras(b);
				startActivity(i);
			}
			
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getLoaderManager().getLoader(GET_PRODUCTS) != null) {
			getLoaderManager().restartLoader(GET_PRODUCTS, null, callbacks);
		} else {
			getLoaderManager().initLoader(GET_PRODUCTS, null, callbacks);
		}
	}
	
}