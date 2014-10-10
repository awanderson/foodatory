package com.penguininc.foodatory;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.adapter.ShoppingListAdapter;
import com.penguininc.foodatory.dailog.ProductPickerDialog;
import com.penguininc.foodatory.dailog.QuantityPickerDialog;
import com.penguininc.foodatory.framework.HomeScreenFragment;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.orm.object.ShoppingListItem;

public class ShoppingListFragment extends HomeScreenFragment {
	
	ListView listview;
	ShoppingListAdapter adapter;
	Product newProduct;
	RuntimeExceptionDao<ShoppingListItem, Integer> shoppingListDao;
	
	private static final int NEW_SHOPPING_LIST_ITEM = 1;
	private static final int NEW_SHOPPING_LIST_ITEM_WITH_QUANITY = 2;
	//delete means we don't add the item to products
	public final static int DELETE_SHOPPING_LIST_ITEM = 7;
	private static final int NEW_PRODUCT = 8;
	public static final String NEW_PRODUCT_KEY = "new_product";
	
	public static final String DEBUG_TAG = "ShoppingListFragment";
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_listview;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		listview = (ListView)view.findViewById(R.id.listview);
		TextView emptyView = (TextView)view.findViewById(R.id.empty);
		emptyView.setText("Empty Shopping List!");
		emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.big_shopping_bag, 0, 0);
		listview.setEmptyView(emptyView);
		shoppingListDao = 
				getHelper().getShoppingListRuntimeExceptionDao();
		
		List<ShoppingListItem> shoppingList = shoppingListDao.queryForAll();
		adapter = new ShoppingListAdapter(getActivity(), shoppingList, this);
		listview.setAdapter(adapter);
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.shopping_cart, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_new) {
			ProductPickerDialog frag = new ProductPickerDialog();
			frag.setRemoveProducts(getProducts());
			Bundle bundle = new Bundle();
			bundle.putInt(ProductPickerDialog.NEW_PRODUCT_REQUEST_CODE_KEY, NEW_PRODUCT);
			frag.setArguments(bundle);
			frag.setTargetFragment(this, NEW_SHOPPING_LIST_ITEM);
			frag.show(getFragmentManager().beginTransaction(), "New Shopping List Item");
		} else {
			super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		
		case NEW_SHOPPING_LIST_ITEM:	
		case NEW_PRODUCT:
			if(resultCode == Activity.RESULT_OK) {
				
				//pass serialized product on, and launch quantity
				Bundle bundle = data.getExtras();
				newProduct = (Product) bundle.getSerializable(Product.KEY);
				// set our super incrementer and decrementer only if
				// we don't have a condiment
				if(newProduct.getType() != Product.CONDIMENT) {
					bundle.putInt(QuantityPickerDialog.SUPER_INCREMENTER_KEY, 
							newProduct.getQty());
					bundle.putInt(QuantityPickerDialog.SUPER_DECREMENTER_KEY, 
							newProduct.getQty());
				}
				bundle.putInt(QuantityPickerDialog.STARTING_VALUE_KEY, 
						newProduct.getQty());
				DialogFragment frag = new QuantityPickerDialog();
				frag.setArguments(bundle);
				frag.setTargetFragment(this, NEW_SHOPPING_LIST_ITEM_WITH_QUANITY);
				frag.show(getFragmentManager().beginTransaction(), "New Shopping List with Quantity");
			}
			break;
			
		case NEW_SHOPPING_LIST_ITEM_WITH_QUANITY:
			if(resultCode == Activity.RESULT_OK) {
				
				ShoppingListItem item = new ShoppingListItem();
				item.setQty(data.getIntExtra(QuantityPickerDialog.CHOSEN_QUANTITY, -1));
				item.setChecked(false);
				item.setProduct(newProduct);
				
				shoppingListDao.create(item);
				
				// add item to our listview
				adapter.add(item);
				adapter.notifyDataSetChanged();
				
			}
			break;
			
		case DELETE_SHOPPING_LIST_ITEM:
			if(resultCode == Activity.RESULT_OK) {
				
				ShoppingListItem item = (ShoppingListItem)
						data.getSerializableExtra(ShoppingListItem.KEY);
				shoppingListDao.delete(item);
				adapter.remove(item);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Save state of checks
		List<ShoppingListItem> shoppingList = adapter.getShoppingList();
		for(ShoppingListItem item : shoppingList) {
			//only update if it is checked, then remove it from the list, and add it to inventory
			if(item.isChecked()) {
				//remove shopping list items
				shoppingListDao.delete(item);
				//add product to inventory
				Pantry pantry = new Pantry();
				Date date = new Date();
				pantry.setDateAdded(new Date());
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				
				// add number of fresh days to our calendar object
				cal.add(Calendar.DATE, item.getProduct().getFreshLength());
				pantry.setDateExpire(cal.getTime());
				pantry.setQty(item.getQty());
				pantry.setProduct(item.getProduct());
				
				RuntimeExceptionDao<Pantry, Integer> pantryDao = 
						getHelper().getPantryRuntimeExceptionDao();
				pantryDao.create(pantry);
				
			}
			
		}
	}
	
	/**
	 * Function to get all the products that are currently
	 * displayed in our shopping list, used so we can
	 * not display them to the user when they add a new
	 * product
	 * @return a list of all the products in the 
	 * shopping list. Doesn't check for duplicates,
	 * assumed list is properly maintained and doesn't
	 * contain duplicates
	 */
	private List<Product> getProducts() {
		List<Product> products = new ArrayList<Product>();
		for(int i = 0; i < adapter.getCount(); i++) {
			products.add(adapter.getItem(i).getProduct());
		}
		Log.d(DEBUG_TAG, "product length = " + products.size());
		return products;
	}
}
