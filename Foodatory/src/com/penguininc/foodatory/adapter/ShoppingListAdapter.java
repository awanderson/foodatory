package com.penguininc.foodatory.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.ShoppingListFragment;
import com.penguininc.foodatory.dailog.ConfirmationDialog;
import com.penguininc.foodatory.sqlite.model.Inventory;
import com.penguininc.foodatory.sqlite.model.ShoppingListItem;

public class ShoppingListAdapter extends ArrayAdapter<ShoppingListItem> {
	
	private Context context;
	private ArrayList<ShoppingListItem> shoppingList;
	private ShoppingListFragment fragment;
	
	public final static String SHOPPING_LIST_ITEM_POSITION = "shopping_list_item_position";
	
	public ShoppingListAdapter(Context context, ArrayList<ShoppingListItem> shoppingList,
			ShoppingListFragment fragment) {
		super(context, R.layout.list_item_shopping_item, shoppingList);
		this.context = context;
		this.shoppingList = shoppingList;
		this.fragment = fragment;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		ShoppingListItem s = shoppingList.get(position);
		final int p = position;
		if(convertView == null) {
			mHolder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_item_shopping_item, null);
			mHolder.shoppingListName = (TextView)convertView.findViewById(R.id.shopping_list_name);
			mHolder.shoppingListQuantity = (TextView)convertView.findViewById(R.id.shopping_list_quantity);
			mHolder.shoppingListChecked = (CheckedTextView)convertView.findViewById(R.id.shopping_list_checked);
			mHolder.removeItem = (TextView)convertView.findViewById(R.id.shopping_list_remove);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag();
		}
		
		mHolder.shoppingListName.setText(s.getProduct().getProductName());
		mHolder.shoppingListQuantity.setText(String.valueOf(s.getQty()));
		
		/* Loads previoius state of checked */
		if(mHolder.isChecked) {
			checkViewHolder(mHolder, position);
		} else {
			uncheckViewHolder(mHolder, position);
		}
		//duplicate final object for onclicklistener
		final ViewHolder viewHolder = mHolder;
		final int finalPosition = position;
		mHolder.shoppingListChecked.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(viewHolder.isChecked) {
					uncheckViewHolder(viewHolder, finalPosition);
				} else {
					checkViewHolder(viewHolder, finalPosition);
				}
			}
		});
		
		mHolder.removeItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShoppingListItem item = shoppingList.get(p);
				Bundle bundle = new Bundle();
				bundle.putLong(ShoppingListItem.SHOPPING_LIST_ITEM_ID, item.getId());
				bundle.putInt(SHOPPING_LIST_ITEM_POSITION, p);
				DialogFragment frag = new ConfirmationDialog();
				frag.setArguments(bundle);
				frag.setTargetFragment(fragment, ShoppingListFragment.DELETE_SHOPPING_LIST_ITEM);
				frag.show(fragment.getFragmentManager().beginTransaction(), "Are you sure you want to delete");
				
				
			}
		});
		return convertView;
	}
	
	public static class ViewHolder {
		public TextView shoppingListName, shoppingListQuantity, removeItem;
		public CheckedTextView shoppingListChecked;
		public boolean isChecked;
	}
	
	public void checkViewHolder(ViewHolder viewHolder, int position) {
		viewHolder.isChecked = true;
		viewHolder.shoppingListChecked.setChecked(true);
		ShoppingListItem s = shoppingList.get(position);
		s.setChecked(true);
	}
	
	public void uncheckViewHolder(ViewHolder viewHolder, int position) {
		viewHolder.isChecked = false;
		viewHolder.shoppingListChecked.setChecked(false);
		ShoppingListItem s = shoppingList.get(position);
		s.setChecked(false);
	}
	
	public ArrayList<ShoppingListItem> getShoppingList() {
		return shoppingList;
	}
}