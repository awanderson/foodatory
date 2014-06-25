package com.penguininc.foodatory;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.penguininc.foodatory.sqlite.helper.RecipeHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Product;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.templates.BasicActivity;
import com.penguininc.foodatory.view.Tab;
import com.penguininc.foodatory.view.TabView;

public class EditRecipeActivity extends BasicActivity {
	
	private long mRecipeId;
	private Activity mThis;
	TabView<EditRecipeFragment, RecipeProductManagerFragment, Void> tabs;
	Tab<EditRecipeFragment> tab1;
	Tab<RecipeProductManagerFragment> tab2;
	
	@Override
	protected Fragment createFragment() {
		return null;
	}
	
	@Override
	public Intent getParentActivityIntent() {
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(MainActivity.LOAD_FRAGMENT, MainActivity.RECIPE_FRAGMENT);
		return i;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_recipe);
		mThis = this;
		mRecipeId = getIntent().getExtras().getLong(Recipe.RECIPE_ID);
		
		tabs = (TabView<EditRecipeFragment, RecipeProductManagerFragment, Void>) findViewById(R.id.tab_view);
		
		Bundle bundle = getIntent().getExtras();
		bundle.putInt(Product.PRODUCT_TYPE, Product.FRESH_FOOD);
		tab1 = new Tab<EditRecipeFragment>(EditRecipeFragment.class, bundle);
		
		bundle.putInt(Product.PRODUCT_TYPE, Product.DRY_GOOD);
		tab2 = new Tab<RecipeProductManagerFragment>(RecipeProductManagerFragment.class, bundle);
		
		
		tabs.setFrag1(tab1, "Info");
		tabs.setFrag2(tab2, "Ingredients");
		
		tabs.setupTabs(this);
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.edit_recipe, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_delete){
			LoaderCallbacks<Void> callbacks = 
			 	(new GenericLoaderCallbacks<Long, Void>(mThis, mRecipeId) {
				 		
			 		@Override
			 		protected Void doInBackground(Long data) {
			 			(new RecipeHelper(context)).deleteRecipe(data);
			 			return null;
			 		}
						
			 		@Override
			 		protected void loadFinished(Void output) {
			 			finish();
			 			Toast.makeText(mThis,"Recipe Removed",Toast.LENGTH_SHORT).show();
					
			 		}

			 		@Override
			 		protected void resetLoader(Loader<Void> args) {
					
			 		}
			 		
			});
			getLoaderManager().initLoader(2, null, callbacks);
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
}