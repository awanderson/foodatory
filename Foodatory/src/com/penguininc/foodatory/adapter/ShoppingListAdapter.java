package com.penguininc.foodatory.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.penguininc.foodatory.orm.object.ShoppingListItem;

public class ShoppingListAdapter extends ArrayAdapter<ShoppingListItem> {
	
	private Context context;
	private List<ShoppingListItem> shoppingList;
	private ShoppingListFragment fragment;
	
	public final static String SHOPPING_LIST_ITEM_POSITION = 
			"shopping_list_item_position";
	
	public final static String DEBUG_TAG = "ShoppingListAdapter";
	
	public ShoppingListAdapter(Context context, List<ShoppingListItem> shoppingList,
			ShoppingListFragment fragment) {
		super(context, R.layout.list_item_shopping_item, shoppingList);
		this.context = context;
		Collections.sort(shoppingList, new ShoppingListAdapterComparator());
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
		// do this so we can get our shopping list item in onClick
		mHolder.shoppingListChecked.setTag(s);
		mHolder.shoppingListChecked.setChecked(s.isChecked());
		mHolder.shoppingListChecked.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckedTextView ctv = (CheckedTextView) v;
				ShoppingListItem s = (ShoppingListItem)ctv.getTag();
				if(s.isChecked()) {
					ctv.setChecked(false);
					s.setChecked(false);
				} else {
					ctv.setChecked(true);
					s.setChecked(true);
				}
			}
		});
		
		mHolder.removeItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShoppingListItem item = shoppingList.get(p);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ShoppingListItem.KEY, item);
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
	}
	
	public List<ShoppingListItem> getShoppingList() {
		return shoppingList;
	}
	
	@Override
	public void add(ShoppingListItem item) {
		super.add(item);
		Collections.sort(shoppingList, new ShoppingListAdapterComparator());
	}
	
	public class ShoppingListAdapterComparator
			implements Comparator<ShoppingListItem>{
		
		@Override
	    public int compare(ShoppingListItem s1, ShoppingListItem s2) {
	        return s1.getProduct().getProductName().compareTo(
	        		s2.getProduct().getProductName());
	    }
	}
	
	
}