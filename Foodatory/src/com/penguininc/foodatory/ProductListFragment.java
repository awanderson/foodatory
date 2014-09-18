package com.penguininc.foodatory;

import java.sql.SQLException;
import java.util.List;

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

import com.penguininc.foodatory.adapter.ProductListAdapter;
import com.penguininc.foodatory.framework.BasicFragment;
import com.penguininc.foodatory.orm.dao.ProductDao;
import com.penguininc.foodatory.orm.object.Product;

public class ProductListFragment extends BasicFragment {

	private static final String DEBUG_TAG = "ProductListFragment";
	
	private ListView listview;
	ProductListAdapter adapter;
	//LoaderCallbacks<ArrayList<Product>> callbacks;
	private int mProductType;
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_listview;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		mProductType = getArguments().getInt(Product.PRODUCT_TYPE, Product.FRESH_FOOD);
		Log.d("ProductListFragment", "product type = " + mProductType);
		
		
		listview = (ListView)view.findViewById(R.id.listview);
		TextView emptyView = (TextView)view.findViewById(R.id.empty);
		if(mProductType == Product.FRESH_FOOD) {
			emptyView.setText("You have no Fresh Food!");
		} else if (mProductType == Product.DRY_GOOD) {
			emptyView.setText("You have no Dry Goods!");
		}else {
			emptyView.setText("You have no Condiments!");
		}
		emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.big_products, 0, 0);
		listview.setEmptyView(emptyView);
		
		/* Make on click launch info */
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Product p = adapter.getItem(position);
				Intent i = new Intent(getActivity(), EditProductActivity.class);
				Bundle b = new Bundle();
				b.putSerializable(Product.KEY, p);
				i.putExtras(b);
				startActivity(i);
			}
			
		});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try {
			ProductDao productDao = getHelper().getProductDao();
			List<Product> products = productDao.queryForType(mProductType);
			adapter = new ProductListAdapter(getActivity(), products);
			listview.setAdapter(adapter);
			
		} catch (SQLException e) {
			
		}
		
	}
	
}