package com.penguininc.foodatory;

import android.app.Fragment;
import android.content.Intent;

import com.penguininc.foodatory.dailog.NewProductDialog;
import com.penguininc.foodatory.templates.BasicActivity;

public class NewProductActivity extends BasicActivity {

	@Override
	protected Fragment createFragment() {
		return new NewProductDialog();
	}
	
	@Override
	public Intent getParentActivityIntent() {
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(MainActivity.LOAD_FRAGMENT, MainActivity.PRODUCT_FRAGMENT);
		return i;
	}
	
}