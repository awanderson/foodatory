package com.penguininc.foodatory;

import android.app.Fragment;
import android.content.Intent;

import com.penguininc.foodatory.templates.BasicActivity;

public class EditProductActivity extends BasicActivity {
	
	@Override
	protected Fragment createFragment() {
		return new EditProductFragment();
	}
	
	@Override
	public Intent getParentActivityIntent() {
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(MainActivity.LOAD_FRAGMENT, MainActivity.PRODUCT_FRAGMENT);
		return i;
	}
}