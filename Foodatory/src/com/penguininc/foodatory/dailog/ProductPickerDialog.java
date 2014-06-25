package com.penguininc.foodatory.dailog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
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

import com.penguininc.foodatory.InventoryFragment;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.adapter.SimpleProductListAdapter;
import com.penguininc.foodatory.sqlite.helper.RecipeProductHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.templates.BasicActivity;

public class ProductPickerDialog extends DialogFragment {
	
	private ListView listview;
	SimpleProductListAdapter adapter;
	LoaderCallbacks<ArrayList<Product>> callbacks;
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
		callbacks = (new GenericLoaderCallbacks<Void, ArrayList<Product>>(getActivity(), null){

			@Override
			protected ArrayList<Product> doInBackground(Void data) {
				return (new RecipeProductHelper(context)).getProductsNoInRecipe(mRecipeId);
			}
			
			@Override
			protected void loadFinished(ArrayList<Product> output) {
				adapter = new SimpleProductListAdapter(getActivity(), output);
				listview.setEmptyView(emptyView);
				listview.setAdapter(adapter);
				mFilter.addTextChangedListener(mTextWatcher);
			}
			
			@Override
			protected void resetLoader(Loader<ArrayList<Product>> args) {
				listview.setAdapter(null);
			}
			
		});

		getLoaderManager().initLoader(0, null, callbacks);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Intent i = new Intent();
				long productId = adapter.getItem(position).getId();
				int productFreshness = (int) adapter.getItem(position).getFreshLength();
				i.putExtra(Product.PRODUCT_ID, productId);
				i.putExtra(Product.PRODUCT_FRESHNESS, productFreshness);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
				dismiss();
			}
			
		});
		
		Button addProduct = (Button)view.findViewById(R.id.add_product);
		final Fragment mThis = this;
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