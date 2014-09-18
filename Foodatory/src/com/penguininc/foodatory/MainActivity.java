package com.penguininc.foodatory;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.penguininc.foodatory.adapter.NavDrawerListAdapter;
import com.penguininc.foodatory.interfaces.ABSNewButtonListener;
import com.penguininc.foodatory.interfaces.MainActivityInterface;
import com.penguininc.foodatory.model.NavDrawerItem;

public class MainActivity extends Activity
	implements MainActivityInterface{

	//nav drawer title
	private CharSequence mDrawerTitle;
	
	//app title
	private CharSequence mTitle;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	// slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
	
    Menu mMenu;
    
    //static integers used in intents to tell the activity
    //which fragment to display
    public static int INVENTORY_FRAGMENT = 0;
    public static int SHOPPING_CART_FRAGMENT = 1;
    public static int RECIPE_FRAGMENT = 2;
    public static int PRODUCT_FRAGMENT = 3;
    public static int SETTING_FRAGMENT = 4;
    public static String LOAD_FRAGMENT = "load fragment";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().setIcon(R.drawable.icon_no_background);
		
		mTitle = mDrawerTitle = getTitle();
		
		// load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		
		// nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        navDrawerItems = new ArrayList<NavDrawerItem>();
        
        //adding nav drawer items to array
        //inventory
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        //shopping cart
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        //recipes
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        //products
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        //settings
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        
        // Recycle the typed array
        navMenuIcons.recycle();
        
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        /*
        if (savedInstanceState == null && getIntent().getIntExtra(LOAD_FRAGMENT, 0) == 0) {
            // on first time display view for first nav item
            displayView(0);
        }
        */
        int display_frag = getIntent().getIntExtra(LOAD_FRAGMENT, 0);
        displayView(display_frag);
        
        
	}
     
	/**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        default:
            return super.onOptionsItemSelected(item);
        }
    }
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }
 
    
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
        case 0:
            fragment = new InventoryFragment();
            break;
        case 1:
            fragment = new ShoppingListFragment();
            break;
        case 2:
            fragment = new RecipeFragment();
            break;
        case 3:
            fragment = new ProductsFragment();
            break;
        case 4:
            fragment = new SettingsFragment();
            break;
        default:
            break;
        }
 
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
            	.replace(R.id.frag_container, fragment).commit();
 
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	public void addNewButton(ABSNewButtonListener listener) {
		listener.onNewButtonClickListener();
		
	}
	
}
