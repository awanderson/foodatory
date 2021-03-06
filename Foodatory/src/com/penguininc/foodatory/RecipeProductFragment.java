package com.penguininc.foodatory;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
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

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.adapter.RecipeProductListAdapter;
import com.penguininc.foodatory.dailog.ProductPickerDialog;
import com.penguininc.foodatory.dailog.QuantityPickerDialog;
import com.penguininc.foodatory.framework.BasicFragment;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.orm.object.Recipe;
import com.penguininc.foodatory.orm.object.RecipeProduct;

public class RecipeProductFragment extends BasicFragment {
	
	public final static String DEBUG_TAG = "RecipeProductManagerFragment";
	
	private Recipe mRecipe;
	private ListView listview;
	private RecipeProductListAdapter adapter;
	private RecipeProductFragment mThis;
	private TextView emptyView;
	private RuntimeExceptionDao<RecipeProduct, Integer> recipeProductDao;
	private Product newProduct;
	
	//constants for fragment returns
	private final static int NEW_PRODUCT = 1;
	private final static int NEW_RECIPE_PRODUCT = 2;
	private final static int NEW_RECIPE_PRODUCT_WITH_QUANTITY = 4;
	private final static int EDIT_PRODUCT = 5;
	public final static int DELETE_PRODUCT = 6;
	public final static int EDIT_PRODUCT_QUANTITY = 7;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		Bundle bundle = getArguments();
		mRecipe = (Recipe)bundle.getSerializable(Recipe.KEY);
		
		// refresh our recipe to get the latest recipeProduct
		RuntimeExceptionDao<Recipe, Integer> recipeDao = getHelper().getRecipeRuntimeExceptionDao();
		recipeDao.refresh(mRecipe);
		
		listview = (ListView)view.findViewById(R.id.listview);
		emptyView = (TextView)view.findViewById(R.id.empty_list);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				RecipeProduct rp = adapter.getItem(position);
				DialogFragment frag = new QuantityPickerDialog();
            	frag.setTargetFragment(mThis, EDIT_PRODUCT);
            	Bundle bundle = new Bundle();
            	bundle.putSerializable(RecipeProduct.KEY, rp);
            	bundle.putInt(QuantityPickerDialog.STARTING_VALUE_KEY,
            			rp.getProductQty());
            	bundle.putInt(QuantityPickerDialog.SAVE_TARGET_KEY, EDIT_PRODUCT_QUANTITY);
            	bundle.putBoolean(QuantityPickerDialog.DELETE_TOGGLE_KEY, true);
            	bundle.putInt(QuantityPickerDialog.DELETE_TARGET_KEY, DELETE_PRODUCT);
            	frag.setArguments(bundle);
            	frag.show(getFragmentManager().beginTransaction(), "Edit Product");
            	
			}
		});
		
		List<RecipeProduct> recipeProducts = mRecipe.getRecipeProducts();
		if(recipeProducts != null) {
			adapter = new RecipeProductListAdapter(getActivity(), recipeProducts);
			listview.setAdapter(adapter);
		}
		listview.setEmptyView(emptyView);
		
		mThis = this;
		
		Button newProduct = (Button)view.findViewById(R.id.new_product);
		newProduct.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ProductPickerDialog frag = new ProductPickerDialog();
				frag.setRemoveProducts(getProducts());
				Bundle bundle = new Bundle();
				bundle.putInt(ProductPickerDialog.NEW_PRODUCT_REQUEST_CODE_KEY,
						NEW_PRODUCT);
				frag.setTargetFragment(mThis, NEW_RECIPE_PRODUCT);
				frag.setArguments(bundle);
				frag.show(getFragmentManager().beginTransaction(), "New Product");
			}
		});
		
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
	    	
	    // new product
	    case NEW_RECIPE_PRODUCT:
	    case NEW_PRODUCT:
	    	if (resultCode == Activity.RESULT_OK) {
	          	// save serialized product
	           	Bundle bundle = data.getExtras();
	           	newProduct = (Product) bundle.getSerializable(Product.KEY);
	           	//get quantity dialog
	           	DialogFragment frag = new QuantityPickerDialog();
	           	bundle.putInt(QuantityPickerDialog.STARTING_VALUE_KEY, 1);
	           	// set our super incrementer and decrementer only if
				// we don't have a condiment
				if(newProduct.getType() != Product.CONDIMENT) {
					bundle.putInt(QuantityPickerDialog.SUPER_INCREMENTER_KEY, 
							newProduct.getQty());
					bundle.putInt(QuantityPickerDialog.SUPER_DECREMENTER_KEY, 
							newProduct.getQty());
				}
	           	frag.setArguments(bundle);
	           	frag.setTargetFragment(this, NEW_RECIPE_PRODUCT_WITH_QUANTITY);
	           	frag.show(getFragmentManager().beginTransaction(),
	           			"New Recipe Product With Quantity");
	        }
	        break;
	            
	        case NEW_RECIPE_PRODUCT_WITH_QUANTITY:
	        	if (resultCode == Activity.RESULT_OK) {
	        		// Create a new recipe product and populate its fields
	            	RecipeProduct recipeProduct = new RecipeProduct();
	            	recipeProduct.setProduct(newProduct);
	            	recipeProduct.setRecipe(mRecipe);
	            	recipeProduct.setProductQty(
	            			data.getIntExtra(QuantityPickerDialog.CHOSEN_QUANTITY, -1));
	            	
	            	if(recipeProductDao == null) {
	            		recipeProductDao = getHelper().getRecipeProductRuntimeExceptionDao();
	            	}
	            	// make sure we have an adapter
	            	if(adapter == null) {
	            		List<RecipeProduct> recipeProducts = 
	            				new ArrayList<RecipeProduct>();
	            		recipeProducts.add(recipeProduct);
	            		adapter = new RecipeProductListAdapter(getActivity(), recipeProducts);
	            		listview.setAdapter(adapter);
	            	} else {
	            		adapter.add(recipeProduct);
	            		adapter.notifyDataSetChanged();
	            	}
	            	mRecipe.addRecipeProduct(recipeProduct);
	        	}
	        	
	            break;
	        
	        case EDIT_PRODUCT:
	        	Log.d(DEBUG_TAG, "in EDIT_PRODUCT");
	        	if(resultCode == DELETE_PRODUCT) {
	        		Log.d(DEBUG_TAG, "in DELETE_PRODUCT");
	        		RecipeProduct recipeProduct = 
	        				(RecipeProduct)data.getSerializableExtra(RecipeProduct.KEY);
	        		if(recipeProductDao == null) {
	        			recipeProductDao = getHelper().getRecipeProductRuntimeExceptionDao();
	        		}
	        		recipeProductDao.delete(recipeProduct);
	        		adapter.remove(recipeProduct);
	        		adapter.notifyDataSetChanged();
	        	
	        	} else if (resultCode == EDIT_PRODUCT_QUANTITY) {
	        		Log.d("RecipeProductManager", "right result code");
	        		RecipeProduct recipeProduct = 
	        				(RecipeProduct)data.getSerializableExtra(RecipeProduct.KEY);
	        		
	        		Log.d(DEBUG_TAG, "RecipeProduct = " + recipeProduct.getProduct().getProductName());
	        		
	        		if(recipeProductDao == null) {
	        			recipeProductDao = getHelper().getRecipeProductRuntimeExceptionDao();
	        		}
	        		int qty = data.getIntExtra(QuantityPickerDialog.CHOSEN_QUANTITY, 1);
	        		recipeProduct.setProductQty(qty);
	        		recipeProductDao.update(recipeProduct);
	        		adapter.notifyDataSetChanged();
	        		
	        	}
	        	
	        	break;
	    }
	}
	/**
	 * Function to get all the products that are currently
	 * displayed in our recipeproduct list, used so we can
	 * not display them to the user when they add a new
	 * product
	 * @return a list of all the products in the 
	 * recipeproduct list. Doesn't check for duplicates,
	 * assumed list is properly maintained and doesn't
	 * contain duplicates
	 */
	private List<Product> getProducts() {
		List<Product> products = new ArrayList<Product>();
		for(int i = 0; i < adapter.getCount(); i++) {
			products.add(adapter.getItem(i).getProduct());
		}
		return products;
	}
}