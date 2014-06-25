package com.penguininc.foodatory;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.penguininc.foodatory.sqlite.helper.RecipeHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.templates.BasicFragment;

public class NewRecipeFragment extends BasicFragment {
	
	private static final int NEW_RECIPE = 1;
	
	EditText mRecipeName;
	EditText mRecipeDescription;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		// set title
		changeTitle("Create New Recipe");
		
		//save button
		Button save = (Button)view.findViewById(R.id.save);
		mRecipeName = (EditText)view.findViewById(R.id.recipe_name);
		//mRecipeDescription = (EditText)view.findViewById(R.id.recipe_description);
		
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String recipe_name = mRecipeName.getText().toString();
				//String recipe_description = mRecipeDescription.getText().toString();
				Recipe r = new Recipe();
				r.setName(recipe_name);
				//r.setDescription(recipe_description);
				
				LoaderCallbacks<Long> callbacks = 
						(new GenericLoaderCallbacks<Recipe, Long>(getActivity(), r) {

							@Override
							protected Long doInBackground(Recipe data) {
								return (new RecipeHelper(context)).createRecipe(data);
							}

							@Override
							protected void loadFinished(Long output) {
								getActivity().finish();
								Toast.makeText(getActivity(),"Recipe Created",Toast.LENGTH_SHORT).show();
								
							}

							@Override
							protected void resetLoader(Loader<Long> args) {
								
							}
							
						});
				getLoaderManager().initLoader(0, null, callbacks);
			}
			
		});
		
		return view;
	}
	
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_new_recipe;
	}
	
}