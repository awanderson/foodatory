package com.penguininc.foodatory;


import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.dailog.ProductPickerDialog;
import com.penguininc.foodatory.dailog.QuantityPickerDialog;
import com.penguininc.foodatory.framework.HomeScreenFragment;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.view.Tab;
import com.penguininc.foodatory.view.TabView;

public class InventoryFragment extends HomeScreenFragment {
	
	TabView<InventoryListFragment, InventoryListFragment, InventoryListFragment> tabs;
	Tab<InventoryListFragment> tab1;
	Tab<InventoryListFragment> tab2;
	Tab<InventoryListFragment> tab3;
	
	// stores a new product to keep with a new pantry item
	Product newProduct;
	
	private static final int NEW_INVENTORY = 1;
	private static final int NEW_INVENTORY_WITH_QUANTITY = 2;
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
			frag.setTargetFragment(this, NEW_INVENTORY);
			frag.show(getFragmentManager().beginTransaction(), "New Inventory");
		} else {
			super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		
		case NEW_INVENTORY:
		case NEW_PRODUCT:
			if(resultCode == Activity.RESULT_OK) {
				//save serialized product on, and launch quantity
				Bundle bundle = data.getExtras();
				newProduct = (Product) bundle.getSerializable(Product.KEY);
				DialogFragment frag = new QuantityPickerDialog();
				bundle.putInt(QuantityPickerDialog.STARTING_VALUE_KEY, newProduct.getQty());
				bundle.putBoolean(QuantityPickerDialog.DELETE_TOGGLE_KEY, false);
				bundle.putInt(QuantityPickerDialog.SAVE_TARGET_KEY, Activity.RESULT_OK);
				// set our super incrementer and decrementer only if
				// we don't have a condiment
				if(newProduct.getType() != Product.CONDIMENT) {
					bundle.putInt(QuantityPickerDialog.SUPER_INCREMENTER_KEY, 
							newProduct.getQty());
					bundle.putInt(QuantityPickerDialog.SUPER_DECREMENTER_KEY, 
							newProduct.getQty());
				}
				frag.setArguments(bundle);
				frag.setTargetFragment(this, NEW_INVENTORY_WITH_QUANTITY);
				frag.show(getFragmentManager().beginTransaction(), "New Inventory with Quantity");
			}
			break;
			
		case NEW_INVENTORY_WITH_QUANTITY:
			if(resultCode == Activity.RESULT_OK) {
				
				// Create a new pantry and populate its fields
				Pantry pantry = new Pantry();
				pantry.setQty(data.getIntExtra(
						QuantityPickerDialog.CHOSEN_QUANTITY, -1));
				Date date = new Date();
				pantry.setDateAdded(date);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				
				// add number of fresh days to our calendar object
				cal.add(Calendar.DATE, newProduct.getFreshLength());
				pantry.setDateExpire(cal.getTime());
				pantry.setProduct(newProduct);
				
				RuntimeExceptionDao<Pantry, Integer> pantryDao = 
						getHelper().getPantryRuntimeExceptionDao();
				pantryDao.create(pantry);
				
				// refresh correct tab
				if(pantry.getProduct().getType() == Product.FRESH_FOOD
						&& tab1.getFragment() != null) {
					((InventoryListFragment)(tab1.getFragment())).refreshView();
				} else if(pantry.getProduct().getType() == Product.DRY_GOOD
						&& tab2.getFragment() != null) {
					((InventoryListFragment)(tab2.getFragment())).refreshView();
				} else if(pantry.getProduct().getType() == Product.CONDIMENT
						&& tab3.getFragment() != null){
					((InventoryListFragment)(tab3.getFragment())).refreshView();
				}
				
			}
			
			break;
		}
	}
	
	
}
