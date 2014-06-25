package com.penguininc.foodatory;


import java.util.Calendar;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.penguininc.foodatory.dailog.ProductPickerDialog;
import com.penguininc.foodatory.dailog.QuantityPickerDialog;
import com.penguininc.foodatory.sqlite.helper.InventoryHelper;
import com.penguininc.foodatory.sqlite.helper.ProductHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Inventory;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.templates.HomeScreenFragment;
import com.penguininc.foodatory.view.Tab;
import com.penguininc.foodatory.view.TabView;

public class InventoryFragment extends HomeScreenFragment {
	
	TabView<InventoryListFragment, InventoryListFragment, InventoryListFragment> tabs;
	Tab<InventoryListFragment> tab1;
	Tab<InventoryListFragment> tab2;
	Tab<InventoryListFragment> tab3;
	InventoryFragment mThis;
	
	private static final int NEW_INVENTORY = 1;
	private static final int NEW_INVENTORY_WITH_QUANTITY = 2;
	private static final int NEW_INVENTORY_VALUE = 3;
	public static final int NEW_PRODUCT = 4;
	
	
	//used as a boolean in the data passed back to determine if a new
	//product needs to be saved
	public static final String NEW_PRODUCT_KEY = "new_product";
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_three_tabs;
	}
	
	//@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container,savedInstanceState);
		
		tabs = (TabView<InventoryListFragment, InventoryListFragment, InventoryListFragment>) v.findViewById(R.id.tab_view);
		
		Bundle bundle = new Bundle();
		bundle.putInt(Product.PRODUCT_TYPE, Product.FRESH_FOOD);
		tab1 = new Tab<InventoryListFragment>(InventoryListFragment.class, bundle);
		
		bundle = new Bundle();
		bundle.putInt(Product.PRODUCT_TYPE, Product.DRY_GOOD);
		tab2 = new Tab<InventoryListFragment>(InventoryListFragment.class, bundle);
		
		bundle = new Bundle();
		bundle.putInt(Product.PRODUCT_TYPE, Product.CONDIMENT);
		tab3 = new Tab<InventoryListFragment>(InventoryListFragment.class, bundle);
		
		tabs.setFrag1(tab1, "Perishables");
		tabs.setFrag2(tab2, "Dry Goods");
		tabs.setFrag3(tab3, "Condiments");
		
		tabs.setupTabs(getActivity());
		
		mThis = this;
		
		Log.d("InventoryFragment", "onCreateView");
		
		return v;
		
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.inventory, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_new) {
			DialogFragment frag = new ProductPickerDialog();
			Bundle bundle = new Bundle();
			bundle.putInt(ProductPickerDialog.NEW_PRODUCT_REQUEST_CODE_KEY, NEW_PRODUCT);
			frag.setArguments(bundle);
			frag.setTargetFragment(mThis, NEW_INVENTORY);
			frag.show(getFragmentManager().beginTransaction(), "New Inventory");
			/*
			FragmentManager fm = getFragmentManager();
			(new ProductPickerDialog()).show(fm, "fragment_new_inventory");
			*/
		} else {
			super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		
		case NEW_INVENTORY:
			if(resultCode == Activity.RESULT_OK) {
				DialogFragment frag = new QuantityPickerDialog();
				frag.setTargetFragment(mThis, NEW_INVENTORY_WITH_QUANTITY);
				Bundle bundle = new Bundle();
				bundle.putLong(Product.PRODUCT_ID, data.getLongExtra(Product.PRODUCT_ID, -1));
				bundle.putInt(Product.PRODUCT_FRESHNESS, data.getIntExtra(Product.PRODUCT_FRESHNESS, 1));
				bundle.putInt(Product.PRODUCT_QTY, data.getIntExtra(Product.PRODUCT_QTY, 1));
				frag.setArguments(bundle);
				frag.show(getFragmentManager().beginTransaction(), "New Inventory with Quantity");
				
			}
			break;
		
		case NEW_PRODUCT:
			if(resultCode == Activity.RESULT_OK) {
				//pass serialized product on, and launch quantity
				Bundle bundle = data.getExtras();
				Product p = (Product) bundle.getSerializable(Product.PRODUCT);
				DialogFragment frag = new QuantityPickerDialog();
				bundle.putBoolean(NEW_PRODUCT_KEY, true);
				frag.setArguments(bundle);
				frag.setTargetFragment(mThis, NEW_INVENTORY_WITH_QUANTITY);
				frag.show(getFragmentManager().beginTransaction(), "New Inventory with Quantity");
				Log.d("InventoryFragment", "product name = " + p.getProductName());
			}
			break;
			
		case NEW_INVENTORY_WITH_QUANTITY:
			if(resultCode == Activity.RESULT_OK) {
				
				//create a new inventory and save get the corresponding
				//fields
				Inventory i = new Inventory();
				i.setQty(data.getIntExtra(QuantityPickerDialog.CHOSEN_QUANTITY, -1));
				Calendar date = Calendar.getInstance();
				i.setDateAdded(date);
				
				
				boolean new_product = false;
				Product p;
				int product_freshness;
				//get serialized product from data bundle
				if(data.getBooleanExtra(NEW_PRODUCT_KEY, false)) {
					new_product = true;
					p = (Product)data.getSerializableExtra(Product.PRODUCT);
					product_freshness = (int)p.getFreshLength();
				} else {
					// load product id from bundle
					p = new Product();
					p.setId(data.getLongExtra(Product.PRODUCT_ID, -1));
					i.setProduct(p);
					product_freshness = data.getIntExtra(Product.PRODUCT_FRESHNESS, 1);
				}
				
				date.add(Calendar.DATE, product_freshness);
				i.setDateExpire(date);
				
				//we're done changing inventory so put in a final variable
				//to use our callback
				final Inventory finalInventory = i;
				
				//Save new product
				LoaderCallbacks<Long> saveProductCallbacks = 
						(new GenericLoaderCallbacks<Product, Long>(getActivity(), p) {
							
							@Override
							protected Long doInBackground(Product data) {
								return (new ProductHelper(context)).createProduct(data);
							}
							
							@Override
							protected void loadFinished(Long output) {
								//create a new temp product and set the id
								//to the output, which is the id of our new
								//product. We only need to set the id
								//in this context because that's the only
								//thing we're saving
								Product p = new Product();
								p.setId(output);
								finalInventory.setProduct(p);
								LoaderCallbacks<Long> callbacks = generateInventoryCallbacks(finalInventory);
								LoaderManager l = getLoaderManager();
								if(l.getLoader(NEW_INVENTORY_VALUE) != null) {
									l.restartLoader(NEW_INVENTORY_VALUE, null, callbacks);
								} else {
									l.initLoader(NEW_INVENTORY_VALUE, null, callbacks);
								}
							}
							
							@Override
							protected void resetLoader(Loader<Long> args) {
								
							}
							
				});
					
				LoaderManager l = getLoaderManager();
				//save new product first, and then we'll save the inventory on
				//completion
				if(new_product) {
					if(l.getLoader(NEW_PRODUCT) != null) {
						l.restartLoader(NEW_PRODUCT, null, saveProductCallbacks);
					} else {
						l.initLoader(NEW_PRODUCT, null, saveProductCallbacks);
					}
				} else {
					//if our product already exists, just save the corresponding
					//inventory
					if(l.getLoader(NEW_INVENTORY_VALUE) != null) {
						l.restartLoader(NEW_INVENTORY_VALUE, null, generateInventoryCallbacks(i));
					} else {
						l.initLoader(NEW_INVENTORY_VALUE, null, generateInventoryCallbacks(i));
					}
				}
				
				
			}
			
			break;
		}
	}
	
	/**
	 * private method to get a inventorycallback because
	 * we need to use different i's
	 * @param i is the inventory to be saved in the db
	 * @return the callback that saves i in the db
	 */
	
	private LoaderCallbacks<Long> generateInventoryCallbacks(Inventory i) {
		LoaderCallbacks<Long> saveInventoryCallbacks =
				(new GenericLoaderCallbacks<Inventory, Long>(getActivity(), i){
						
					@Override
					protected Long doInBackground(Inventory data) {
						Log.d("InventoryFragment", "Saving inventory");
						return (new InventoryHelper(context)).createInventory(data);
					}
						
					@Override
					protected void loadFinished(Long output) {
						if(tab1.getFragment() != null) {
							((InventoryListFragment)tab1.getFragment()).refreshView();
						}
						if(tab2.getFragment() != null) {
							((InventoryListFragment)tab2.getFragment()).refreshView();
						}
						if(tab3.getFragment() != null) {
							((InventoryListFragment)tab3.getFragment()).refreshView();
						}
					}
						
					@Override
					protected void resetLoader(Loader<Long> args) {
					
					}
		});
		return saveInventoryCallbacks;
	}
	
}
