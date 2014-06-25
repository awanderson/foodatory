package com.penguininc.foodatory;


import java.util.ArrayList;

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
import android.widget.Toast;

import com.penguininc.foodatory.adapter.RecipeListAdapter;
import com.penguininc.foodatory.dailog.NewRecipeDialog;
import com.penguininc.foodatory.sqlite.helper.RecipeHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.templates.HomeScreenFragment;

public class RecipesFragment extends HomeScreenFragment {
	
	private final static int GET_RECIPES = 0;
	
	private RecipeListAdapter adapter;
	private LoaderCallbacks<ArrayList<Recipe>> callbacks;
	private ListView listview;
	
	private final static int NEW_RECIPE = 1;
	
	RecipesFragment mThis;

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
		mThis = this;
		View view = super.onCreateView(inflater, container, savedInstanceState);
		listview = (ListView)view.findViewById(R.id.listview);
		TextView emptyView = (TextView)view.findViewById(R.id.empty);
		emptyView.setText("You have no Recipes!");
		emptyView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.big_recipe, 0, 0);
		listview.setEmptyView(emptyView);
		callbacks = (new GenericLoaderCallbacks<Void, ArrayList<Recipe>>(getActivity(), null) {

			@Override
			protected ArrayList<Recipe> doInBackground(Void data) {
				return (new RecipeHelper(context)).getAllRecipes();
			}

			@Override
			protected void loadFinished(ArrayList<Recipe> output) {
				adapter = new RecipeListAdapter(getActivity(), output);
				listview.setAdapter(adapter);
				
			}

			@Override
			protected void resetLoader(Loader<ArrayList<Recipe>> args) {
				listview.setAdapter(null);
			}
		});
		
		getLoaderManager().initLoader(GET_RECIPES, null, callbacks);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Recipe r = adapter.getItem(position);
				Intent i = new Intent(getActivity(), EditRecipeActivity.class);
				Bundle b = new Bundle();
				b.putLong(Recipe.RECIPE_ID, r.getId());
				i.putExtras(b);
				startActivity(i);
				
			}
			
		});
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_new) {
			DialogFragment frag = new NewRecipeDialog();
			frag.setTargetFragment(mThis, NEW_RECIPE);
			frag.show(getFragmentManager().beginTransaction(), "New Recipe");
			
		} else {
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(GET_RECIPES, null, callbacks);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch(requestCode) {
		case NEW_RECIPE:
			if(resultCode == Activity.RESULT_OK) {
				Log.d("RecipeFragment", "in onActivityResult");
				String recipeName = data.getStringExtra(NewRecipeDialog.RECIPE_NAME);
				String recipeDescription = data.getStringExtra(NewRecipeDialog.RECIPE_DESCRIPTION);
				Recipe r = new Recipe();
				r.setName(recipeName);
				r.setDescription(recipeDescription);
				
				LoaderCallbacks<Long> callbacks = 
						(new GenericLoaderCallbacks<Recipe, Long>(getActivity(), r) {

							@Override
							protected Long doInBackground(Recipe data) {
								return (new RecipeHelper(context)).createRecipe(data);
							}

							@Override
							protected void loadFinished(Long output) {
								Intent i = new Intent(getActivity(), EditRecipeActivity.class);
								Bundle b = new Bundle();
								b.putLong(Recipe.RECIPE_ID, output);
								i.putExtras(b);
								startActivity(i);
								//getActivity().finish();
								//Toast.makeText(getActivity(),"Recipe Created",Toast.LENGTH_SHORT).show();
								
							}

							@Override
							protected void resetLoader(Loader<Long> args) {
								
							}
							
						});
				LoaderManager l = getLoaderManager();
				if(l.getLoader(NEW_RECIPE) == null) {
					l.initLoader(NEW_RECIPE, null, callbacks);
				} else {
					l.restartLoader(NEW_RECIPE, null, callbacks);
				}
			}
			break;
		}
	}
}


