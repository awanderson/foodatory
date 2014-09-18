package com.penguininc.foodatory.dailog;

/**
 * Interface to create a new product
 * Can be used as a dialog, or as a fragment
 * To specify usage, set BasicActivity.EMBEDDED in bundle
 * 
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.framework.BasicActivity;
import com.penguininc.foodatory.framework.BasicActivityInterface;
import com.penguininc.foodatory.interfaces.BasicFragmentInterface;
import com.penguininc.foodatory.listener.ProductTypeSwitchListener;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.view.CounterView;
import com.penguininc.foodatory.view.OrmLiteDialogFragment;
import com.penguininc.foodatory.view.ProductTypeView;

public class NewProductDialog extends OrmLiteDialogFragment
	implements BasicFragmentInterface{

	private static final int NEW_PRODUCT = 0;
	
	EditText mProductName;
	CounterView mProductQty;
	CounterView mProductFreshness;
	ProductTypeView mProductType;
	LinearLayout mProductQtyWrapper;
	LinearLayout mProductFreshnessWrapper;
	boolean mEmbedded;
	
	protected BasicActivityInterface mBasicActivityListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(getLayout(), container, false);
		setHasOptionsMenu(true);
		//set title
		changeTitle("Create New Product");
		
		mEmbedded = getArguments().getBoolean(BasicActivity.EMBEDDED);
		//only don't display title if we're a dialog and not embedded
		if(!mEmbedded) {
			//no title
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		}
		
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
				if(product_name.equals("")) {
					Toast.makeText(getActivity(),"Need Product Name",Toast.LENGTH_SHORT).show();
				} else {
					// get product properties from view and save it
					int product_qty = mProductQty.getValue();
					int product_freshness = mProductFreshness.getValue();
					int product_type = mProductType.getType();
					Product product = new Product();
					product.setFreshLength(product_freshness);
					product.setProductName(product_name);
					product.setQty(product_qty);
					product.setType(product_type);
					RuntimeExceptionDao<Product, Integer> dao = 
							getHelper().getProductRuntimeExceptionDao();
					dao.create(product);
					// send back our product
					Intent i = new Intent();
					i.putExtra(Product.KEY, product);
					if(getTargetFragment() != null) {
						getTargetFragment().onActivityResult(getTargetRequestCode(), 
								Activity.RESULT_OK, i);
						dismiss();
					} else {
						getActivity().finish();
					}
					
				}
			}
		});
		
		return view;
	}
		
	protected int getLayout() {
		return R.layout.fragment_new_product;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try{
			mBasicActivityListener = (BasicActivity)activity;
		}
		catch (ClassCastException e){
			mBasicActivityListener = null;
		}
	}

	@Override
	public void changeTitle(String title) {
		if(mBasicActivityListener != null) {
			mBasicActivityListener.changeTitle(title);
		}
	}
	
	public void closeDialog() {
		dismiss();
		
	}
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
}