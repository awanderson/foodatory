package com.penguininc.foodatory;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.penguininc.foodatory.listener.ProductTypeSwitchListener;
import com.penguininc.foodatory.sqlite.helper.ProductHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.templates.BasicFragment;
import com.penguininc.foodatory.view.CounterView;
import com.penguininc.foodatory.view.ProductTypeView;

public class EditProductFragment extends BasicFragment {

	long mProductId;
	
	EditText mProductName;
	CounterView mProductQty;
	CounterView mProductFreshness;
	ProductTypeView mProductType;
	LinearLayout mProductQtyWrapper;
	LinearLayout mProductFreshnessWrapper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		
		Bundle bundle = getArguments();
		
		//keep keyboard down
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//set title
		changeTitle("Edit Product");
		
		mProductId = bundle.getLong(Product.PRODUCT_ID);
		View view = super.onCreateView(inflater, container, savedInstanceState);
		mProductName = (EditText)view.findViewById(R.id.product_name);
		mProductType = (ProductTypeView)view.findViewById(R.id.product_type);
		mProductType.setOnItemSwitch(new ProductTypeSwitchListener() {
			
			@Override
			public void onItemSwitch(int product_type) {
				if(product_type == Product.FRESH_FOOD) {
					mProductQtyWrapper.setVisibility(View.VISIBLE);
					mProductFreshnessWrapper.setVisibility(View.VISIBLE);
				} else if(product_type == Product.DRY_GOOD) {
					mProductQtyWrapper.setVisibility(View.VISIBLE);
					mProductFreshnessWrapper.setVisibility(View.GONE);
				} else if(product_type == Product.CONDIMENT) {
					mProductQtyWrapper.setVisibility(View.GONE);
					mProductFreshnessWrapper.setVisibility(View.GONE);
				}
			}
		});
		
		
		mProductQty = (CounterView)view.findViewById(R.id.product_qty);
		mProductQtyWrapper = (LinearLayout)view.findViewById(R.id.product_qty_wrapper);
		mProductFreshness = (CounterView)view.findViewById(R.id.product_freshness);
		mProductFreshnessWrapper = (LinearLayout)view.findViewById(R.id.product_freshness_wrapper);
		
		//load current product info
		LoaderCallbacks<Product> callbacks = (new GenericLoaderCallbacks<Long, Product>(getActivity(), mProductId){

			@Override
			protected Product doInBackground(Long data) {
				return (new ProductHelper(context)).getProduct(data);
			}

			@Override
			protected void loadFinished(Product p) {
				mProductName.setText(p.getProductName());
				mProductQty.setValue(p.getQty());
				mProductFreshness.setValue((int)p.getFreshLength());
				mProductType.setType(p.getType());
				
			}
			

			@Override
			protected void resetLoader(Loader<Product> args) {
				
			}
			
		});
		
		getLoaderManager().initLoader(0, null, callbacks);
		
		Button save = (Button)view.findViewById(R.id.save);
		save.setVisibility(View.GONE);
		
		return view;
	}
	
	
	
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_new_product;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.edit_product, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_delete) {
			LoaderCallbacks<Void> callbacks = (new GenericLoaderCallbacks<Long, Void>(getActivity(), mProductId){

				@Override
				protected Void doInBackground(Long data) {
					(new ProductHelper(context)).deleteProduct(data);
					return null;
				}

				@Override
				protected void loadFinished(Void output) {
					getActivity().finish();
					Toast.makeText(getActivity(),"Product Removed",Toast.LENGTH_SHORT).show();
				}

				@Override
				protected void resetLoader(Loader<Void> args) {
					
				}
			});
			getLoaderManager().initLoader(2, null, callbacks);
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		String product_name = mProductName.getText().toString();
		int product_qty = mProductQty.getValue();
		Long product_freshness = (long)mProductFreshness.getValue();
		int product_type = mProductType.getType();
		Product p = new Product();
		p.setFreshLength(product_freshness);
		p.setProductName(product_name);
		p.setQty(product_qty);
		p.setId(mProductId);
		p.setType(product_type);
		
		LoaderCallbacks<Integer> callbacks = (new GenericLoaderCallbacks<Product, Integer>(getActivity(), p){

			@Override
			protected Integer doInBackground(Product data) {
				return (new ProductHelper(context)).updateProduct(data);
			}

			@Override
			protected void loadFinished(Integer output) {
				getActivity().finish();
				Toast.makeText(getActivity(),"Product Saved",Toast.LENGTH_SHORT).show();
			}

			@Override
			protected void resetLoader(Loader<Integer> args) {
			}

			
		});
		getLoaderManager().initLoader(1, null, callbacks);
	}
}