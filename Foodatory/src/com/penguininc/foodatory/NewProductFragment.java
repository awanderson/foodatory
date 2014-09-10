package com.penguininc.foodatory;

/**
 * Not in used anymore,
 * replaced by NewProductDailog
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.listener.ProductTypeSwitchListener;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.templates.BasicFragment;
import com.penguininc.foodatory.view.CounterView;
import com.penguininc.foodatory.view.ProductTypeView;

public class NewProductFragment extends BasicFragment {

	EditText mProductName;
	CounterView mProductQty;
	CounterView mProductFreshness;
	ProductTypeView mProductType;
	LinearLayout mProductQtyWrapper;
	LinearLayout mProductFreshnessWrapper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);

		//set title
		changeTitle("Create New Product");
		
		// save button
		Button save = (Button)view.findViewById(R.id.save);
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
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String product_name = mProductName.getText().toString();
				int product_qty = mProductQty.getValue();
				int product_freshness = mProductFreshness.getValue();
				int product_type = mProductType.getType();
				Product p = new Product();
				p.setFreshLength(product_freshness);
				p.setProductName(product_name);
				p.setQty(product_qty);
				p.setType(product_type);
				
				RuntimeExceptionDao<Product, Integer> dao = 
						getHelper().getProductRuntimeExceptionDao();
				dao.create(p);
			}
		});
		
		return view;
	}
		
	@Override
	protected int getLayout() {
		return R.layout.fragment_new_product;
	}
	
}