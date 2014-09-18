package com.penguininc.foodatory;

import java.util.ArrayList;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.penguininc.foodatory.framework.HomeScreenFragment;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.view.Tab;
import com.penguininc.foodatory.view.TabView;

public class ProductsFragment extends HomeScreenFragment{

	private final static int GET_PRODUCTS = 0;
	private int saved_tab_state;
	TabView<ProductListFragment, ProductListFragment, ProductListFragment> tabs;
	Tab<ProductListFragment> tab1;
	Tab<ProductListFragment> tab2;
	Tab<ProductListFragment> tab3;
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_three_tabs;
	}
	
	@SuppressWarnings("unchecked")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		tabs = (TabView<ProductListFragment, ProductListFragment, ProductListFragment>) view.findViewById(R.id.tab_view);
		
		Bundle bundle1 = new Bundle();
		bundle1.putInt(Product.PRODUCT_TYPE, Product.FRESH_FOOD);
		tab1 = new Tab<ProductListFragment>(ProductListFragment.class, bundle1);
		
		Bundle bundle2 = new Bundle();
		bundle2.putInt(Product.PRODUCT_TYPE, Product.DRY_GOOD);
		tab2 = new Tab<ProductListFragment>(ProductListFragment.class, bundle2);
		
		Bundle bundle3 = new Bundle();
		bundle3.putInt(Product.PRODUCT_TYPE, Product.CONDIMENT);
		tab3 = new Tab<ProductListFragment>(ProductListFragment.class, bundle3);
		
		tabs.setFrag1(tab1, "Perishables");
		tabs.setFrag2(tab2, "Dry Goods");
		tabs.setFrag3(tab3, "Condiments");
		
		tabs.setupTabs(getActivity());
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.products, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_new) {
			Intent intent = new Intent(getActivity(), NewProductActivity.class);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(saved_tab_state != 0) {
			tabs.loadState(saved_tab_state);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		saved_tab_state = tabs.getCurrentState();
	}
	
	
}