package com.penguininc.foodatory.dailog;

/**
 * Interface to create a new product
 * Can be used as a dialog, or as a fragment
 * To specify usage, set BasicActivity.EMBEDDED in bundle
 * 
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
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

import com.penguininc.foodatory.InventoryFragment;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.interfaces.BasicFragmentInterface;
import com.penguininc.foodatory.listener.ProductTypeSwitchListener;
import com.penguininc.foodatory.sqlite.helper.ProductHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.templates.BasicActivity;
import com.penguininc.foodatory.templates.BasicActivityInterface;
import com.penguininc.foodatory.view.CounterView;
import com.penguininc.foodatory.view.ProductTypeView;

public class NewProductDialog extends DialogFragment
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
					int product_qty = mProductQty.getValue();
					Long product_freshness = (long)mProductFreshness.getValue();
					int product_type = mProductType.getType();
					Product p = new Product();
					p.setFreshLength(product_freshness);
					p.setProductName(product_name);
					p.setQty(product_qty);
					p.setType(product_type);
					/* 
					 * if we're embedded, save it now and then destroy
					 * activity. Bad practice, so we'll need to fix this
					 * later
					 */
					if(mEmbedded) {
						LoaderManager lm = getLoaderManager();
						lm.initLoader(NEW_PRODUCT, null, (LoaderCallbacks<Long>)
								(new GenericLoaderCallbacks<Product, Long>(getActivity(), p) {

							@Override
							protected Long doInBackground(Product data) {
								return (new ProductHelper(context)).createProduct((data));
							}

							@Override
							protected void loadFinished(Long output) {
								getActivity().finish();
								Toast.makeText(getActivity(),"Product Added",Toast.LENGTH_SHORT).show();
							}

							@Override
							protected void resetLoader(Loader<Long> args) {
								
							}
							
						}));
					} else {
						/*
						 * if we're a dialog, we'll create a bundle,
						 * put the product there, and then return to
						 * our target fragment, where it's saved there
						 */
						Bundle bundle = new Bundle();
						bundle.putSerializable(Product.PRODUCT, p);
						Intent i = new Intent();
						i.putExtras(bundle);
						getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
						dismiss();
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