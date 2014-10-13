package com.penguininc.foodatory.dailog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.adapter.SimpleProductListAdapter;
import com.penguininc.foodatory.framework.BasicActivity;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.view.OrmLiteDialogFragment;

public class ProductPickerDialog extends OrmLiteDialogFragment {
	
	private ListView listview;
	SimpleProductListAdapter adapter;
	long mRecipeId;
	TextView emptyView;
	EditText mFilter;
	TextWatcher mTextWatcher;
	int mNewProductRequestCode;
	List<Product> removeProducts;
	
	//used to set the request code if new product is set
	public final static String NEW_PRODUCT_REQUEST_CODE_KEY = "new_product_key";
	
	private final static String DEBUG_TAG = "ProductPickerDialog";
	
	public ProductPickerDialog() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//no title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		mNewProductRequestCode = getArguments().getInt(NEW_PRODUCT_REQUEST_CODE_KEY, 0);
		
		View view = inflater.inflate(R.layout.dialog_new_inventory, container);
		
		mFilter = (EditText)view.findViewById(R.id.filter);
		
		mTextWatcher = (new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				adapter.getFilter().filter(cs.toString());
			}
			
		});
		
		listview = (ListView)view.findViewById(R.id.listview);
		listview.setTextFilterEnabled(true);
		listview.setClickable(true);
		emptyView = (TextView)view.findViewById(R.id.empty_list);
		RuntimeExceptionDao<Product, Integer> productDao = getHelper()
				.getProductRuntimeExceptionDao();
		List<Product> products = productDao.queryForAll();
		if(removeProducts != null) {
			products = removeProducts(products);
		}
		adapter = new SimpleProductListAdapter(getActivity(), products);
		listview.setEmptyView(emptyView);
		listview.setAdapter(adapter);
		mFilter.addTextChangedListener(mTextWatcher);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Intent i = new Intent();
				Product product = adapter.getItem(position);
				i.putExtra(Product.KEY, product);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
				dismiss();
			}
			
		});
		
		Button addProduct = (Button)view.findViewById(R.id.add_product);
		//launch new product dialog when add product is clicked
		addProduct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment dialog = new NewProductDialog();
				Bundle bundle = new Bundle();
				//set it as a dialog, not embedded fragment
				bundle.putBoolean(BasicActivity.EMBEDDED, false);
				dialog.setArguments(bundle);
				
				dialog.setTargetFragment(getTargetFragment(), mNewProductRequestCode);
				dialog.show(getFragmentManager().beginTransaction(), "New Product");
				dismiss();
			}
		});
		
		
		return view;
	}
	
	/**
	 * This function will remove all the products
	 * in the list from the products it displays
	 * to the user
	 * @param products list of products you don't
	 * want displayed to the user
	 */
	public void setRemoveProducts(List<Product> products) {
		// in actuality, we just set the list of products
		// and we subtract them in onCreate
		removeProducts = products;
	}
	
	/**
	 * Quick and dirty function that removes
	 * all products in removeProducts from our
	 * "good" list of products. Bad efficiency,
	 * will need to be updated
	 */
	private List<Product> removeProducts(List<Product> products) {
		List<Product> goodProducts = new ArrayList<Product>();
		for(Product product : products) {
			boolean goodProduct = true;
			for(Product badProduct : removeProducts) {
				if(product.getId() == badProduct.getId()) {
					goodProduct = false;
				}
			}
			if(goodProduct) {
				goodProducts.add(product);
			}
		}
		return goodProducts;
	}
}