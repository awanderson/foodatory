package com.penguininc.foodatory.adapter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penguininc.foodatory.InventoryListFragment;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.dailog.ConfirmationDialog;
import com.penguininc.foodatory.orm.dao.PantryDao;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.sqlite.helper.InventoryHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Inventory;
import com.penguininc.foodatory.sqlite.model.Product;

public class InventoryListAdapter extends ArrayAdapter<Pantry> {
	
	private Context context;
	private List<Pantry> pantries;
	/* We need this to increment or decrement the quantity */
	PantryDao pantryDao;
	/* We need this to set as the target for the delete dialog */
	private InventoryListFragment fragment;
	
	private static final int DECREMENT = 1;
	private static final int INCREMENT = 2;
	private static final int DELETE = 3;
	private static final int SHOPPING_LIST = 4;
	
	public static final String INVENTORY_POSITION = "inventory_position";
	
	public class PantryComparator implements Comparator<Pantry> {
		
		@Override
		public int compare(Pantry p1, Pantry p2) {
			return (int) (p1.getDateExpire().getTime()
					- p2.getDateExpire().getTime());
			
		}
	}
	
	
	public InventoryListAdapter(Context context, List<Pantry> pantries,
			PantryDao pantryDao, InventoryListFragment fragment) {
		super(context, R.layout.list_item_inventory, pantries);
		this.context = context;
		Collections.sort(pantries, new PantryComparator());
		this.pantries = pantries;
		this.pantryDao = pantryDao;
		this.fragment = fragment;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int p = position;
		Pantry pantry = pantries.get(position);
		ViewHolder mHolder = null;
		if (convertView == null) {
			mHolder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_item_inventory, null);
			mHolder.decrement = (TextView)convertView.findViewById(R.id.decrement);
			mHolder.increment = (TextView)convertView.findViewById(R.id.increment);
			mHolder.delete = (TextView) convertView.findViewById(R.id.delete);
			mHolder.shoppingList = (TextView) convertView.findViewById(R.id.shopping_list);
			mHolder.expandable = (LinearLayout) convertView.findViewById(R.id.expandable);
			mHolder.inventory_name = (TextView) convertView.findViewById(R.id.inventory_name);
			mHolder.inventory_qty = (TextView) convertView.findViewById(R.id.inventory_qty);
			mHolder.inventory_expire_date = (TextView) convertView.findViewById(R.id.inventory_expire_date);
			mHolder.isExpanded = false;
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag();
		}
		
		mHolder.inventory_name.setText(pantry.getProduct().getProductName());
		mHolder.inventory_qty.setText(String.valueOf(pantry.getQty()));
		
		int days = (int)( (pantry.getDateExpire().getTime() - Calendar.getInstance().getTime().getTime()) / (1000 * 60 * 60 * 24)) + 1;
		if(pantry.getProduct().getType() != Product.CONDIMENT) {
			//save padding values, padding gets messed up when we change background
			int pL = mHolder.inventory_qty.getPaddingLeft();
			int pT = mHolder.inventory_qty.getPaddingTop();
			int pR = mHolder.inventory_qty.getPaddingRight();
			int pB = mHolder.inventory_qty.getPaddingBottom();
			if(pantry.getProduct().getType() == Product.FRESH_FOOD) {
				mHolder.inventory_expire_date.setText("Expires: " + days + " days");
				if(days < 1) {
					mHolder.inventory_qty.setBackgroundResource(R.drawable.button_black_background);
				} else if(days == 1) {
					mHolder.inventory_qty.setBackgroundResource(R.drawable.button_red_background);
				} else if (days == 2) {
					mHolder.inventory_qty.setBackgroundResource(R.drawable.button_orange_background);
				} else if (days == 3) {
					mHolder.inventory_qty.setBackgroundResource(R.drawable.button_yellow_background);
				} else {
					mHolder.inventory_qty.setBackgroundResource(R.drawable.button_green_background);
				}
			} else {
				// set dry goods to green background
				mHolder.inventory_qty.setBackgroundResource(R.drawable.button_green_background);
			}
				
			mHolder.inventory_qty.setPadding(pL, pT, pR, pB);
		} else {
			mHolder.inventory_qty.setVisibility(View.GONE);
			mHolder.increment.setVisibility(View.GONE);
			mHolder.decrement.setVisibility(View.GONE);
		}
		/* set date expired to date purchased for things that don't expire */
		if(pantry.getProduct().getType() != Product.FRESH_FOOD) {
			// Create an instance of SimpleDateFormat used for formatting 
			// the string representation of date (month/day/year)
			DateFormat df = new SimpleDateFormat("MMM d");
			String purchases = df.format((pantry.getDateAdded().getTime()));
			mHolder.inventory_expire_date.setText("Purchased on : " + purchases);
		}
			
		// Prevents onItemClick from being fired on listview when expandable is touched
		/*
		expandable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		*/
		final TextView inventoryQty = mHolder.inventory_qty;
		
		mHolder.decrement.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Pantry pantry = pantries.get(p);
				if(pantry.getQty() != 0) {
					pantry.setQty(pantry.getQty() - 1);
					try {
						pantryDao.update(pantry);
						inventoryQty.setText(String.valueOf(pantry.getQty()));
					} catch (SQLException e) {
					}
					
				}
			}
		});
		mHolder.increment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Pantry pantry = pantries.get(p);
				pantry.setQty(pantry.getQty() + 1);
				try {
					pantryDao.update(pantry);
					inventoryQty.setText(String.valueOf(pantry.getQty()));
				} catch (SQLException e) {
				}
				
				
			}
		});
		
		mHolder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Pantry pantry = pantries.get(p);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Pantry.KEY, pantry);
				DialogFragment frag = new ConfirmationDialog();
				frag.setArguments(bundle);
				frag.setTargetFragment(fragment, InventoryListFragment.DELETE_INVENTORY);
				frag.show(fragment.getFragmentManager().beginTransaction(), "Are you sure you want to delete");
				
			}
		});
		
		mHolder.shoppingList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
		
		/* Loads previous state of expandable */
		if(mHolder.isExpanded) {
			mHolder.expandable.setVisibility(View.VISIBLE);
		} else if(mHolder.expandable.getVisibility() == View.VISIBLE) {
			mHolder.expandable.setVisibility(View.INVISIBLE);
		}
		return convertView;
		
	}
	
	public static class ViewHolder {
		TextView inventory_name, inventory_expire_date, inventory_qty;
		TextView decrement, increment, delete, shoppingList;
		public LinearLayout expandable;
		public boolean isExpanded;
	}
}