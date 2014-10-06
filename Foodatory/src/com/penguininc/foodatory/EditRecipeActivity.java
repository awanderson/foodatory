package com.penguininc.foodatory;

import java.sql.SQLException;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.penguininc.foodatory.framework.BasicActivity;
import com.penguininc.foodatory.orm.dao.RecipeDao;
import com.penguininc.foodatory.orm.object.Product;
import com.penguininc.foodatory.orm.object.Recipe;
import com.penguininc.foodatory.view.Tab;
import com.penguininc.foodatory.view.TabView;

public class EditRecipeActivity extends BasicActivity {
	
	TabView<EditRecipeFragment, RecipeProductFragment, Void> tabs;
	Tab<EditRecipeFragment> tab1;
	Tab<RecipeProductFragment> tab2;
	Recipe recipe;
	
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
		
		recipe = (Recipe)getIntent().getSerializableExtra(Recipe.KEY);
		
		tabs = (TabView<EditRecipeFragment, RecipeProductFragment, Void>) findViewById(R.id.tab_view);
		
		Bundle bundle = getIntent().getExtras();
		tab1 = new Tab<EditRecipeFragment>(EditRecipeFragment.class, bundle);
		
		bundle.putInt(Product.PRODUCT_TYPE, Product.DRY_GOOD);
		tab2 = new Tab<RecipeProductFragment>(RecipeProductFragment.class, bundle);
		
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
			/*
			RuntimeExceptionDao<Recipe, Integer> recipeDao = 
					getHelper().getRecipeRuntimeExceptionDao();
			*/
			try {
				RecipeDao recipeDao = getHelper().getRecipeDao();
				recipeDao.delete(recipe);
			} catch (SQLException e) {
				
			}
			
			finish();
 			Toast.makeText(this,"Recipe Removed",Toast.LENGTH_SHORT).show();
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
}