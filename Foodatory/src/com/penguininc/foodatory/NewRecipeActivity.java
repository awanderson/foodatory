
package com.penguininc.foodatory;

import android.content.Intent;
import android.app.Fragment;

import com.penguininc.foodatory.templates.BasicActivity;

public class NewRecipeActivity extends BasicActivity {

	
	@Override
	protected Fragment createFragment() {
		return new NewRecipeFragment();
	}
	
	@Override
	public Intent getParentActivityIntent() {
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(MainActivity.LOAD_FRAGMENT, MainActivity.RECIPE_FRAGMENT);
		return i;
	}
	
}