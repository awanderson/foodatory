package com.penguininc.foodatory.dailog;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.templates.BasicActivity;
import com.penguininc.foodatory.view.OrmLiteDialogFragment;

public class ProductPickerDialog extends OrmLiteDialogFragment {
	
	private ListView listview;
	SimpleProductListAdapter adapter;
	long mRecipeId;
	TextView emptyView;
	EditText mFilter;
	TextWatcher mTextWatcher;
	int mNewProductRequestCode;
	
	private final static int NEW_PRODUCT = 1;
	
	//used to set the request code if new product is set
	public final static String NEW_PRODUCT_REQUEST_CODE_KEY = "new_product_key";
	
	public ProductPickerDialog() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//no title
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		mRecipeId = -1;
		
		if(getArguments() != null) {
			mRecipeId = getArguments().getLong(Recipe.RECIPE_ID);
			Log.d("ProductPickerDialog", "Setting recipe id = " + mRecipeId);
		}
		
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
				Log.d("ProductPickerDialog", "filter applied");
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
}