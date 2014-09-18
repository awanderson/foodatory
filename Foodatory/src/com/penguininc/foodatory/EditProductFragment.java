package com.penguininc.foodatory;

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

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.framework.BasicFragment;
import com.penguininc.foodatory.listener.ProductTypeSwitchListener;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.view.CounterView;
import com.penguininc.foodatory.view.ProductTypeView;

public class EditProductFragment extends BasicFragment {

	
	Product mProduct;
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
		
		// get our product that was sent in the bundle
		mProduct = (Product)bundle.getSerializable(Product.KEY);
		
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
		
		// Set info
		mProductName.setText(mProduct.getProductName());
		mProductQty.setValue(mProduct.getQty());
		mProductFreshness.setValue((int)mProduct.getFreshLength());
		mProductType.setType(mProduct.getType());
		
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
			
			if(mProduct != null) {
				RuntimeExceptionDao<Product, Integer> dao = 
						getHelper().getProductRuntimeExceptionDao();
				dao.delete(mProduct);
				getActivity().finish();
			}
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mProduct != null) {
			String product_name = mProductName.getText().toString();
			int product_qty = mProductQty.getValue();
			int product_freshness = mProductFreshness.getValue();
			int product_type = mProductType.getType();
			mProduct.setFreshLength(product_freshness);
			mProduct.setProductName(product_name);
			mProduct.setQty(product_qty);
			mProduct.setType(product_type);
			
			RuntimeExceptionDao<Product, Integer> dao = 
					getHelper().getProductRuntimeExceptionDao();
			dao.update(mProduct);
		}
		
	}
}