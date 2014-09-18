package com.penguininc.foodatory;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.adapter.ShoppingListAdapter;
import com.penguininc.foodatory.adapter.ShoppingListAdapter.ViewHolder;
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
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				ViewHolder mHolder = (ViewHolder) v.getTag();
				if(mHolder.isChecked) {
					adapter.uncheckViewHolder(mHolder, position);
				} else {
					adapter.checkViewHolder(mHolder, position);
				}
			}
			
		});
		
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
			DialogFragment frag = new ProductPickerDialog();
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
			if(resultCode == Activity.RESULT_OK) {
				/*
				 * Save the product for later use when we 
				 * we're saving the shopping list item
				 * (NEW_SHOPPING_LIST_ITEM_WITH_QUANITY
				 */
				newProduct = (Product)data.getSerializableExtra(Product.KEY);
				
				DialogFragment frag = new QuantityPickerDialog();
				frag.setTargetFragment(this, NEW_SHOPPING_LIST_ITEM_WITH_QUANITY);
				Bundle bundle = new Bundle();
				bundle.putInt(QuantityPickerDialog.STARTING_VALUE_KEY, 1);
				frag.setArguments(bundle);
				frag.show(getFragmentManager().beginTransaction(), "New Shopping List Item With Quantity");
			}
			break;
			
		case NEW_PRODUCT:
			if(resultCode == Activity.RESULT_OK) {
				//pass serialized product on, and launch quantity
				Bundle bundle = data.getExtras();
				newProduct = (Product) bundle.getSerializable(Product.KEY);
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
				cal.add(Calendar.DATE, newProduct.getFreshLength());
				pantry.setDateExpire(cal.getTime());
				pantry.setQty(item.getQty());
				pantry.setProduct(newProduct);
				
				RuntimeExceptionDao<Pantry, Integer> pantryDao = 
						getHelper().getPantryRuntimeExceptionDao();
				pantryDao.create(pantry);
				
			}
			
		}
		
	}
	
}
