package com.penguininc.foodatory;

import java.util.ArrayList;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.penguininc.foodatory.adapter.InventoryListAdapter;
import com.penguininc.foodatory.adapter.InventoryListAdapter.ViewHolder;
import com.penguininc.foodatory.sqlite.helper.InventoryHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Inventory;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.templates.BasicFragment;

public class InventoryListFragment extends BasicFragment{

	private final static int GET_INVENTORY = 0;
	public final static int DELETE_INVENTORY = 1;
	private ListView listview;
	InventoryListAdapter adapter;
	GenericLoaderCallbacks<Integer, ArrayList<Inventory>> callbacks;
	private int mInventoryType;
	private ViewHolder mPrevHolder;
	
	
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
		final InventoryListFragment frag = this;
		int retention_lookup = getActivity().getPreferences(0)
				.getInt(SettingsFragment.PANTRY_RETENTION, SettingsFragment.PANTRY_RETENTION_DEFAULT);
		final int retention = SettingsFragment.PANTRY_RETENTION_ARRAY_DAYS[retention_lookup];
		callbacks = (new GenericLoaderCallbacks<Integer, ArrayList<Inventory>>(getActivity(), mInventoryType) {

			@Override
			protected ArrayList<Inventory> doInBackground(Integer data) {
				return (new InventoryHelper(context)).getAllInventoriesWithType(data, retention);
			}

			@Override
			protected void loadFinished(ArrayList<Inventory> output) {
				Log.d("InventoryListFragment", "size " + output.size());
				adapter = new InventoryListAdapter(getActivity(), output, getLoaderManager(), frag);
				listview.setAdapter(adapter);
			}

			@Override
			protected void resetLoader(Loader<ArrayList<Inventory>> args) {
				listview.setAdapter(null);
			}
		
		
		});
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
		getLoaderManager().initLoader(mInventoryType, null, callbacks);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		
	}
	
	public void refreshView() {
		if(getLoaderManager().getLoader(mInventoryType) != null) {
			getLoaderManager().restartLoader(mInventoryType, null, callbacks);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		
		case DELETE_INVENTORY:
			if(resultCode == Activity.RESULT_OK) {
				long id = data.getLongExtra(Inventory.INVENTORY_ID, 0);
				final int position = data.getIntExtra(InventoryListAdapter.INVENTORY_POSITION, 0);
				LoaderCallbacks<Void> deleteCallbacks = 
						(new GenericLoaderCallbacks<Long, Void>(getActivity(), id){

							@Override
							protected Void doInBackground(Long data) {
								(new InventoryHelper(context)).deleteInventory(data);
								return null;
							}

							@Override
							protected void loadFinished(Void output) {
								Log.d("InventoryListFragment", "Delete");
								Inventory i = adapter.getItem(position);
								adapter.remove(i);
								adapter.notifyDataSetChanged();
							}

							@Override
							protected void resetLoader(Loader<Void> args) {
								
							}
						
						
						});
				
				android.app.LoaderManager l = getLoaderManager();
				if(l.getLoader(DELETE_INVENTORY) != null) {
					l.restartLoader(DELETE_INVENTORY, null, deleteCallbacks);
				} else {
					l.initLoader(DELETE_INVENTORY, null, deleteCallbacks);
				}
			}
		
		}
	}
	
}