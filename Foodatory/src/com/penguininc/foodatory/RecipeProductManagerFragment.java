package com.penguininc.foodatory;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.penguininc.foodatory.adapter.RecipeProductListAdapter;
import com.penguininc.foodatory.dailog.ProductPickerDialog;
import com.penguininc.foodatory.dailog.QuantityPickerDialog;
import com.penguininc.foodatory.sqlite.helper.RecipeProductHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.sqlite.model.RecipeProduct;
import com.penguininc.foodatory.templates.BasicFragment;

public class RecipeProductManagerFragment extends BasicFragment {
	
	public final static String DEBUG_TAG = "RecipeProductManagerFragment";
	
	private long mRecipeId;
	private ListView listview;
	private RecipeProductListAdapter adapter;
	private LoaderCallbacks<ArrayList<RecipeProduct>> callbacks;
	private RecipeProductManagerFragment mThis;
	private TextView emptyView;
	
	//constants for fragment returns
	private final static int NEW_PRODUCT = 1;
	private final static int NEW_PRODUCT_WITH_QUANTITY = 4;
	private final static int EDIT_PRODUCT = 5;
	public final static int DELETE_PRODUCT = 6;
	public final static int EDIT_PRODUCT_QUANTITY = 7;
	
	//constants for loader
	private final static int GET_RECIPE_PRODUCTS = 0;
	private final static int NEW_LOADER_VALUE = 3;
	private final static int DELETE_LOADER_VALUE = 4;
	private final static int EDIT_LOADER_VALUE = 5;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		Bundle b = getArguments();
		mRecipeId = b.getLong(Recipe.RECIPE_ID);
		listview = (ListView)view.findViewById(R.id.listview);
		emptyView = (TextView)view.findViewById(R.id.empty_list);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				RecipeProduct r = adapter.getItem(position);
				DialogFragment frag = new QuantityPickerDialog();
            	frag.setTargetFragment(mThis, EDIT_PRODUCT);
            	Bundle bundle = new Bundle();
            	bundle.putInt(QuantityPickerDialog.STARTING_VALUE_KEY, r.getProductQty());
            	bundle.putInt(QuantityPickerDialog.SAVE_TARGET_KEY, EDIT_PRODUCT_QUANTITY);
            	bundle.putBoolean(QuantityPickerDialog.DELETE_TOGGLE_KEY, true);
            	bundle.putInt(QuantityPickerDialog.DELETE_TARGET_KEY, DELETE_PRODUCT);
            	frag.setArguments(bundle);
            	frag.show(getFragmentManager().beginTransaction(), "Edit Product");
            	
			}
		});
		
		callbacks = (new GenericLoaderCallbacks<Long,
			ArrayList<RecipeProduct>>(getActivity(), mRecipeId) {

			@Override
			protected ArrayList<RecipeProduct> doInBackground(Long data) {
				return (new RecipeProductHelper(context)).getAllProducts(data);
			}

			@Override
			protected void loadFinished(ArrayList<RecipeProduct> output) {
				adapter = new RecipeProductListAdapter(getActivity(), output);
				listview.setEmptyView(emptyView);
				listview.setAdapter(adapter);
				
			}

			@Override
			protected void resetLoader(
					Loader<ArrayList<RecipeProduct>> args) {
				listview.setAdapter(null);
			}
		
		});
		mThis = this;
		
		Button newProduct = (Button)view.findViewById(R.id.new_product);
		newProduct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment frag = new ProductPickerDialog();
				frag.setTargetFragment(mThis, NEW_PRODUCT);
				frag.setArguments(getArguments());
				frag.show(getFragmentManager().beginTransaction(), "New Product");
			}
		});
		
		getLoaderManager().initLoader(GET_RECIPE_PRODUCTS, null, callbacks);
		
		return view;
		
	}

	@Override
	protected int getLayout() {
		return R.layout.fragment_recipe_product_manager;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("recipe product", "in on activity");
	    switch(requestCode) {
	    	// product picked, need to display quantity now
	        case NEW_PRODUCT:
	        	Log.d("RecipeProductManager", "in new product " + requestCode);
	            if (resultCode == Activity.RESULT_OK) {
	            	Log.d("recipeProductManager", "result okay");
	            	//get quantity dialog
	            	DialogFragment frag = new QuantityPickerDialog();
	            	frag.setTargetFragment(mThis, NEW_PRODUCT_WITH_QUANTITY);
	            	Bundle bundle = new Bundle();
	            	bundle.putLong(Product.PRODUCT_ID, data.getLongExtra(Product.PRODUCT_ID, -1));
	            	frag.setArguments(bundle);
	            	frag.show(getFragmentManager().beginTransaction(), "New Product With Quantity");
	            	
	            } else if (resultCode == Activity.RESULT_CANCELED){
	                	
	            }
	            break;
	            
	        case NEW_PRODUCT_WITH_QUANTITY:
	        	if (resultCode == Activity.RESULT_OK) {
	        		Log.d("ReipceProductManager","in new product with quantity " + requestCode);
	        		Log.d(DEBUG_TAG, "quantity = " + data.getIntExtra(QuantityPickerDialog.CHOSEN_QUANTITY, -1));
	        		Recipe r = new Recipe();
	            	r.setId(mRecipeId);
	            	Product p = new Product();
	            	p.setId(data.getLongExtra(Product.PRODUCT_ID, -1));
	            	RecipeProduct recipeProduct = new RecipeProduct();
	            	recipeProduct.setProduct(p);
	            	recipeProduct.setRecipe(r);
	            	recipeProduct.setProductQty(data.getIntExtra(QuantityPickerDialog.CHOSEN_QUANTITY, -1));
	            	
	            	//Save new recipe product
	            	LoaderCallbacks<Long> saveCallbacks = 
	            		(new GenericLoaderCallbacks<RecipeProduct, Long> (getActivity(), recipeProduct) {

							@Override
							protected Long doInBackground(RecipeProduct data) {
								return (new RecipeProductHelper(context)).createRecipeProduct(data);
							}
							
							@Override
							protected void loadFinished(Long output) {
								getLoaderManager().restartLoader(GET_RECIPE_PRODUCTS, null, callbacks);
								Log.d("RecipeProductManager", "Reset Loader");
							}

							@Override
							protected void resetLoader(Loader<Long> args) {
								
							}
	            	});
	            	
	            	LoaderManager l = getLoaderManager();
	            	if(l.getLoader(NEW_LOADER_VALUE) != null) {
	            		l.restartLoader(NEW_LOADER_VALUE, null, saveCallbacks);
	            	} else {
	            		l.initLoader(NEW_LOADER_VALUE, null, saveCallbacks);
	            	}
	        	}
	        	
	            break;
	        
	        case EDIT_PRODUCT:
	        	if(resultCode == DELETE_PRODUCT) {
	        		long id = data.getLongExtra(RecipeProduct.RECIPE_PRODUCT_ID, -1);
	        		LoaderCallbacks<Void> deleteCallbacks = 
	        				(new GenericLoaderCallbacks<Long, Void> (getActivity(), id){

								@Override
								protected Void doInBackground(Long data) {
									(new RecipeProductHelper(context)).deleteRecipeProduct(data);
									return null;
								}

								@Override
								protected void loadFinished(Void output) {
									getLoaderManager().restartLoader(GET_RECIPE_PRODUCTS, null, callbacks);
									
								}

								@Override
								protected void resetLoader(Loader<Void> args) {
									
								}
	        				});
	        		LoaderManager l = getLoaderManager();
	        		
	        		if(l.getLoader(DELETE_LOADER_VALUE) != null) {
	        			l.restartLoader(DELETE_LOADER_VALUE, null, deleteCallbacks);
	        		} else {
	        			l.initLoader(DELETE_LOADER_VALUE, null, deleteCallbacks);
	        		}
	        	
	        	} else if (resultCode == EDIT_PRODUCT_QUANTITY) {
	        		Log.d("RecipeProductManager", "right result code");
	        		Recipe r = new Recipe();
	            	r.setId(mRecipeId);
	            	Product p = new Product();
	            	p.setId(data.getLongExtra(Product.PRODUCT_ID, -1));
	            	RecipeProduct recipeProduct = new RecipeProduct();
	            	recipeProduct.setId(data.getLongExtra(RecipeProduct.RECIPE_PRODUCT_ID, -1));
	            	recipeProduct.setProduct(p);
	            	recipeProduct.setRecipe(r);
	            	recipeProduct.setProductQty(data.getIntExtra(Product.PRODUCT_QTY, -1));
	        		
	            	LoaderCallbacks<Integer> editProductCallbacks = 
	        				(new GenericLoaderCallbacks<RecipeProduct, Integer>(getActivity(), recipeProduct) {
	        					
								@Override
								protected Integer doInBackground(
										RecipeProduct data) {
									RecipeProductHelper helper = new RecipeProductHelper(context);
									Log.d("RecipeProductManager", "doInBackground");
									return helper.updateRecipeProduct(data);
									
								}

								@Override
								protected void loadFinished(Integer output) {
									getLoaderManager().restartLoader(GET_RECIPE_PRODUCTS, null, callbacks);
								}

								@Override
								protected void resetLoader(Loader<Integer> args) {
									
								}
	        				
	        				});
	        		LoaderManager l = getLoaderManager();
	        		if(l.getLoader(EDIT_LOADER_VALUE) != null) {
	        			l.restartLoader(EDIT_LOADER_VALUE, null, editProductCallbacks);
	        		} else {
	        			l.initLoader(EDIT_LOADER_VALUE, null, editProductCallbacks);
	        		}
	        		
	        	}
	        	
	        	break;
	            
	    }
	}

	
}