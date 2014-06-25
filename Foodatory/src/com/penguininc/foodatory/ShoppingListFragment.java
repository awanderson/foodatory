package com.penguininc.foodatory;


import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
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

import com.penguininc.foodatory.adapter.ShoppingListAdapter;
import com.penguininc.foodatory.adapter.ShoppingListAdapter.ViewHolder;
import com.penguininc.foodatory.dailog.ProductPickerDialog;
import com.penguininc.foodatory.dailog.QuantityPickerDialog;
import com.penguininc.foodatory.sqlite.helper.InventoryHelper;
import com.penguininc.foodatory.sqlite.helper.ProductHelper;
import com.penguininc.foodatory.sqlite.helper.ShoppingListHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Inventory;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.sqlite.model.ShoppingListItem;
import com.penguininc.foodatory.templates.HomeScreenFragment;

public class ShoppingListFragment extends HomeScreenFragment {
	
	ListView listview;
	GenericLoaderCallbacks<Void, ArrayList<ShoppingListItem>> loadCallbacks;
	ShoppingListAdapter adapter;
	ShoppingListFragment mThis;
	
	private static final int NEW_SHOPPING_LIST_ITEM = 1;
	private static final int NEW_SHOPPING_LIST_ITEM_WITH_QUANITY = 2;
	private static final int NEW_SHOPPING_LIST_ITEM_VALUE = 3;
	private static final int LOAD_SHOPPING_LIST = 4;
	//remove means we add the item to products
	private static final int REMOVE_SHOPPING_ITEM = 5;
	private static final int CREATE_INVENTORY_TIEM = 6;
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
		mThis = this;
		loadCallbacks = (new GenericLoaderCallbacks<Void, ArrayList<ShoppingListItem>>(getActivity(), null) {

			@Override
			protected ArrayList<ShoppingListItem> doInBackground(Void data) {
				return (new ShoppingListHelper(context)).getShoppingList();
			}

			@Override
			protected void loadFinished(ArrayList<ShoppingListItem> output) {
				adapter = new ShoppingListAdapter(getActivity(), output, mThis);
				listview.setAdapter(adapter);
			}

			@Override
			protected void resetLoader(Loader<ArrayList<ShoppingListItem>> args) {
				listview.setAdapter(null);
			}
			
		});
		
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
		getLoaderManager().initLoader(LOAD_SHOPPING_LIST, null, loadCallbacks);
		
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
			frag.setTargetFragment(mThis, NEW_SHOPPING_LIST_ITEM);
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
				DialogFragment frag = new QuantityPickerDialog();
				frag.setTargetFragment(mThis, NEW_SHOPPING_LIST_ITEM_WITH_QUANITY);
				Bundle bundle = new Bundle();
				bundle.putLong(Product.PRODUCT_ID, data.getLongExtra(Product.PRODUCT_ID, -1));
				Log.d("ShoppingListFragment", "without q product id = " + data.getLongExtra(Product.PRODUCT_ID, -1));
				bundle.putInt(Product.PRODUCT_FRESHNESS, data.getIntExtra(Product.PRODUCT_FRESHNESS, 1));
				bundle.putInt(Product.PRODUCT_QTY, data.getIntExtra(Product.PRODUCT_QTY, 1));
				frag.setArguments(bundle);
				frag.show(getFragmentManager().beginTransaction(), "New Shopping List Item With Quantity");
			}
			break;
			
		case NEW_PRODUCT:
			if(resultCode == Activity.RESULT_OK) {
				//pass serialized product on, and launch quantity
				Bundle bundle = data.getExtras();
				Product p = (Product) bundle.getSerializable(Product.PRODUCT);
				DialogFragment frag = new QuantityPickerDialog();
				bundle.putBoolean(NEW_PRODUCT_KEY, true);
				frag.setArguments(bundle);
				frag.setTargetFragment(mThis, NEW_SHOPPING_LIST_ITEM_WITH_QUANITY);
				frag.show(getFragmentManager().beginTransaction(), "New Shopping List with Quantity");
				Log.d("ShoppingListFragment", "product name = " + p.getProductName());
			
			}
			break;
			
		case NEW_SHOPPING_LIST_ITEM_WITH_QUANITY:
			if(resultCode == Activity.RESULT_OK) {
				ShoppingListItem s = new ShoppingListItem();
				s.setQty(data.getIntExtra(QuantityPickerDialog.CHOSEN_QUANTITY, -1));
				s.setChecked(false);
				
				boolean new_product = false;
				Product p;
				if(data.getBooleanExtra(NEW_PRODUCT_KEY, false)) {
					new_product= true;
					p = (Product)data.getSerializableExtra(Product.PRODUCT);
				} else {
					p = new Product();
					p.setId(data.getLongExtra(Product.PRODUCT_ID, -1));
					s.setProduct(p);
				}
				
				//we're done changing our shopping list item
				//so put in a final variable for our callback
				final ShoppingListItem finalShoppingItem = s;
				
				//Save new product
				LoaderCallbacks<Long> saveProductCallbacks = 
						(new GenericLoaderCallbacks<Product, Long>(getActivity(), p) {
							
							@Override
							protected Long doInBackground(Product data) {
								return (new ProductHelper(context)).createProduct(data);
							}
							
							@Override
							protected void loadFinished(Long output) {
								//create a new temp product and set the id
								//to the output, which is the id of our new
								//product. We only need to set the id
								//in this context because that's the only
								//thing we're saving
								Product p = new Product();
								p.setId(output);
								finalShoppingItem.setProduct(p);
								LoaderCallbacks<Long> callbacks = generateSaveShoppingListItemCallbacks(finalShoppingItem);
								LoaderManager l = getLoaderManager();
								if(l.getLoader(NEW_SHOPPING_LIST_ITEM_VALUE) != null) {
									l.restartLoader(NEW_SHOPPING_LIST_ITEM_VALUE, null, callbacks);
								} else {
									l.initLoader(NEW_SHOPPING_LIST_ITEM_VALUE, null, callbacks);
								}
							}
							
							@Override
							protected void resetLoader(Loader<Long> args) {
								
							}
							
				});
					
				LoaderManager l = getLoaderManager();
				//save new product first, and then we'll save the shopping list item
				// on completion
				if(new_product) {
					if(l.getLoader(NEW_PRODUCT) != null) {
						l.restartLoader(NEW_PRODUCT, null, saveProductCallbacks);
					} else {
						l.initLoader(NEW_PRODUCT, null, saveProductCallbacks);
					}
				} else {
					//if our product already exists, just save the corresponding
					//inventory
					if(l.getLoader(NEW_SHOPPING_LIST_ITEM_VALUE) != null) {
						l.restartLoader(NEW_SHOPPING_LIST_ITEM_VALUE, null,
								generateSaveShoppingListItemCallbacks(s));
					} else {
						l.initLoader(NEW_SHOPPING_LIST_ITEM_VALUE, null, 
								generateSaveShoppingListItemCallbacks(s));
					}
				}
				
			}
			break;
			
		case DELETE_SHOPPING_LIST_ITEM:
			if(resultCode == Activity.RESULT_OK) {
				long id = data.getLongExtra(ShoppingListItem.SHOPPING_LIST_ITEM_ID, 0);
				final int position = data.getIntExtra(ShoppingListAdapter.SHOPPING_LIST_ITEM_POSITION, 0);
				LoaderCallbacks<Void> deleteCallbacks =
						(new GenericLoaderCallbacks<Long, Void>(getActivity(), id) {

							@Override
							protected Void doInBackground(Long data) {
								(new ShoppingListHelper(context)).deleteShoppingListItem(data);
								return null;
							}

							@Override
							protected void loadFinished(Void output) {
								ShoppingListItem s = adapter.getItem(position);
								adapter.remove(s);
								adapter.notifyDataSetChanged();
							}

							@Override
							protected void resetLoader(Loader<Void> args) {
								
							}
							
						});
				LoaderManager l = getLoaderManager();
				if(l.getLoader(DELETE_SHOPPING_LIST_ITEM) != null) {
					l.restartLoader(DELETE_SHOPPING_LIST_ITEM, null, deleteCallbacks);
				} else {
					l.initLoader(DELETE_SHOPPING_LIST_ITEM, null, deleteCallbacks);
				}
			}
			break;
			
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// Save state of checks
		ArrayList<ShoppingListItem> shoppingList = adapter.getShoppingList();
		for(ShoppingListItem s : shoppingList) {
			//only update if it is checked, then remove it from the list, and add it to inventory
			if(s.isChecked()) {
				//remove shopping list items
				LoaderCallbacks<Void> removeCallbacks = 
						(new GenericLoaderCallbacks<Long, Void>(getActivity(), s.getId()) {

							@Override
							protected Void doInBackground(Long data) {
								(new ShoppingListHelper(context)).deleteShoppingListItem(data);
								return null;
							}

							@Override
							protected void loadFinished(Void output) {
								if(getLoaderManager().getLoader(LOAD_SHOPPING_LIST) != null) {
									getLoaderManager().restartLoader(LOAD_SHOPPING_LIST, null, loadCallbacks);
								}
							}

							@Override
							protected void resetLoader(Loader<Void> args) {
								
							}

							
						});
				LoaderManager l = getLoaderManager();
				if(l.getLoader(REMOVE_SHOPPING_ITEM) != null) {
					l.restartLoader(REMOVE_SHOPPING_ITEM, null, removeCallbacks);
				} else {
					l.initLoader(REMOVE_SHOPPING_ITEM, null, removeCallbacks);
				}
				
				//add product to inventory
				Inventory i = new Inventory();
				Calendar date = Calendar.getInstance();
				i.setDateAdded(date);
				date.add(Calendar.DATE, (int)s.getProduct().getFreshLength());
				i.setDateExpire(date);
				i.setQty(s.getQty() * s.getProduct().getQty());
				i.setProduct(s.getProduct());
				LoaderCallbacks<Long> createInventory =
						(new GenericLoaderCallbacks<Inventory, Long>(getActivity(), i) {

							@Override
							protected Long doInBackground(Inventory data) {
								return (new InventoryHelper(context)).createInventory(data);
							}

							@Override
							protected void loadFinished(Long output) {
								
							}

							@Override
							protected void resetLoader(Loader<Long> args) {
							}
					
						});
				
				if(l.getLoader(CREATE_INVENTORY_TIEM) != null) {
					l.restartLoader(CREATE_INVENTORY_TIEM, null, createInventory);
				} else {
					l.initLoader(CREATE_INVENTORY_TIEM, null, createInventory);
				}
			}
			
			
		}
		
	}
	
	private LoaderCallbacks<Long> generateSaveShoppingListItemCallbacks(ShoppingListItem s) {
		//save new shopping list item
		LoaderCallbacks<Long> saveCallbacks = 
				(new GenericLoaderCallbacks<ShoppingListItem, Long>(getActivity(), s) {

					@Override
					protected Long doInBackground(ShoppingListItem data) {
						return (new ShoppingListHelper(context)).createShoppingListItem(data);
					}

					@Override
					protected void loadFinished(Long output) {
						if(getLoaderManager().getLoader(LOAD_SHOPPING_LIST) != null) {
							getLoaderManager().restartLoader(LOAD_SHOPPING_LIST, null, loadCallbacks);
						}
					}

					@Override
					protected void resetLoader(Loader<Long> args) {
						
					}
					
					
				});
		
		return saveCallbacks;
	}
}
