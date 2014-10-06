package com.penguininc.foodatory;


import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
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

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.adapter.RecipeListAdapter;
import com.penguininc.foodatory.dailog.NewRecipeDialog;
import com.penguininc.foodatory.framework.HomeScreenFragment;
import com.penguininc.foodatory.orm.object.Recipe;

public class RecipeFragment extends HomeScreenFragment {
	
	
	private RecipeListAdapter adapter;
	private ListView listview;
	private RuntimeExceptionDao<Recipe, Integer> recipeDao;
	
	private final static int NEW_RECIPE = 1;
	
	private static final String DEBUG_TAG = "RecipeFragment";
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.recipes, menu);
	}

	@Override
	protected int getLayout() {
		return R.layout.fragment_listview;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		listview = (ListView)view.findViewById(R.id.listview);
		TextView emptyView = (TextView)view.findViewById(R.id.empty);
		emptyView.setText("You have no Recipes!");
		emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.big_recipe, 0, 0);
		listview.setEmptyView(emptyView);  
		recipeDao = getHelper().getRecipeRuntimeExceptionDao();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Recipe recipe = adapter.getItem(position);
				Intent i = new Intent(getActivity(), EditRecipeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Recipe.KEY, recipe);
				i.putExtras(bundle);
				startActivity(i);
				
			}
			
		});
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_new) {
			DialogFragment frag = new NewRecipeDialog();
			frag.setTargetFragment(this, NEW_RECIPE);
			frag.show(getFragmentManager().beginTransaction(), "New Recipe");
			
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		List<Recipe> recipes = recipeDao.queryForAll();
		adapter = new RecipeListAdapter(getActivity(), recipes);
		listview.setAdapter(adapter);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch(requestCode) {
		case NEW_RECIPE:
			if(resultCode == Activity.RESULT_OK) {
				String recipeName = data.getStringExtra(NewRecipeDialog.RECIPE_NAME);
				//String recipeDescription = data.getStringExtra(NewRecipeDialog.RECIPE_DESCRIPTION);
				Recipe recipe = new Recipe();
				recipe.setName(recipeName);
				int randColor = getRandomColor();
				recipe.setColor(randColor);
				recipeDao.create(recipe);
				Intent i = new Intent(getActivity(), EditRecipeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Recipe.KEY, recipe);
				i.putExtras(bundle);
				startActivity(i);
			}
			break;
		}
	}
	
	private int getRandomColor() {
		int rand = (int)(Math.random() * 4);
		if(rand == 0) {
			return Recipe.BLUE;
		} else if (rand == 1) {
			return Recipe.GREEN;
		} else if (rand == 2) {
			return Recipe.RED;
		} else {
			return Recipe.BLACK;
		}
	}
}


