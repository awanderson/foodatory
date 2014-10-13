package com.penguininc.foodatory;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.adapter.InventoryListAdapter;
import com.penguininc.foodatory.adapter.InventoryListAdapter.ViewHolder;
import com.penguininc.foodatory.dailog.QuantityPickerDialog;
import com.penguininc.foodatory.framework.BasicFragment;
import com.penguininc.foodatory.orm.dao.PantryDao;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.orm.object.ShoppingListItem;

public class InventoryListFragment extends BasicFragment{

	private final static String DEBUG_TAG = "InventoryListFragment";
	
	
	public final static int DELETE_INVENTORY = 1;
	public final static int NEW_SHOPPING_LIST_ITEM = 2;
	
	private ListView listview;
	InventoryListAdapter adapter;
	private int mInventoryType;
	private ViewHolder mPrevHolder;
	private int mRetention;
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_listview;
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		mInventoryType = getArguments().getInt(Product.PRODUCT_TYPE, Product.FRESH_FOOD);
		TextView emptyView = (TextView)view.findViewById(R.id.empty);
		if(mInventoryType == Product.FRESH_FOOD) {
			emptyView.setText("You have no Fresh Food!");
		} else if(mInventoryType == Product.DRY_GOOD){
			emptyView.setText("Your have no Dry Goods!");
		} else {
			emptyView.setText("Your have no Condiments!");
		}
		
		emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.big_pantry, 0, 0);
		
		listview = (ListView)view.findViewById(R.id.listview);
		listview.setEmptyView(emptyView);
		
		// Get the number of days we keep our products from our settings
		int retention_lookup = getActivity().getPreferences(0)
				.getInt(SettingsFragment.PANTRY_RETENTION, SettingsFragment.PANTRY_RETENTION_DEFAULT);
		mRetention = SettingsFragment.PANTRY_RETENTION_ARRAY_DAYS[retention_lookup];
		
		try {
			PantryDao pantryDao = getHelper().getPantryDao();
			List<Pantry> pantries = pantryDao.queryForType(mInventoryType, mRetention);
			adapter = new InventoryListAdapter(getActivity(), pantries, pantryDao, this);
			listview.setAdapter(adapter);
		} catch (SQLException e) {
			
		}
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				ViewHolder mHolder = (ViewHolder) v.getTag();
				
				//if our expandable section is already visible, hide it
				if(mHolder.isExpanded) {
					mHolder.expandable.setVisibility(View.GONE);
					mHolder.isExpanded = false;
					mPrevHolder = null;
					
				} else { //show expandable section
					//hide other visible section
					if(mPrevHolder != null) {
						mPrevHolder.expandable.findViewById(R.id.expandable).setVisibility(View.GONE);
						mPrevHolder.isExpanded = false;
						
					}
					mHolder.expandable.setVisibility(View.VISIBLE);
					mHolder.isExpanded = true;
					mPrevHolder = mHolder;
				}
				
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		
	}
	
	public void refreshView() {
		try {
			PantryDao pantryDao = getHelper().getPantryDao();
			List<Pantry> pantries = pantryDao.queryForType(mInventoryType, mRetention);
			adapter = new InventoryListAdapter(getActivity(), pantries, pantryDao, this);
			listview.setAdapter(adapter);
		} catch (SQLException e) {
			
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		
		// new shopping list item was clicked in our inventory list adapter
		case NEW_SHOPPING_LIST_ITEM:
			if(resultCode == Activity.RESULT_OK) {
				// get our product first
				Product product = (Product)data.getSerializableExtra(Product.KEY);
				if(product == null) {
					return;
				}
				ShoppingListItem item = new ShoppingListItem();
				item.setQty(data.getIntExtra(
						QuantityPickerDialog.CHOSEN_QUANTITY, -1));
				item.setProduct(product);
				item.setChecked(false);
				RuntimeExceptionDao<ShoppingListItem, Integer> shoppingListDao = 
						getHelper().getShoppingListRuntimeExceptionDao();
				shoppingListDao.create(item);
				Toast.makeText(getActivity(), product.getProductName() + 
						" added to your shopping list", Toast.LENGTH_SHORT).show();
				
			}
		case DELETE_INVENTORY:
			if(resultCode == Activity.RESULT_OK) {
				Pantry pantry = (Pantry)data.getSerializableExtra(Pantry.KEY);
				RuntimeExceptionDao<Pantry, Integer> pantryDao = getHelper()
						.getPantryRuntimeExceptionDao();
				pantryDao.delete(pantry);
				adapter.remove(pantry);
				adapter.notifyDataSetChanged();
			}
		
		}
	}
	
}