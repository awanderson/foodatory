package com.penguininc.foodatory;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mobeta.android.dslv.DragSortListView;
import com.penguininc.foodatory.adapter.DirectionListAdapter;
import com.penguininc.foodatory.dailog.NewDirectionDialog;
import com.penguininc.foodatory.framework.BasicFragment;
import com.penguininc.foodatory.orm.object.Direction;
import com.penguininc.foodatory.orm.object.Recipe;
import com.penguininc.foodatory.utilities.ListviewUtilities;

public class RecipeDirectionFragment extends BasicFragment {

	private Recipe recipe;
	private DragSortListView listview;
	private TextView emptyView;
	private DirectionListAdapter adapter;
	private RuntimeExceptionDao<Direction, Integer> directionDao;
	
	private static final int NEW_DIRECTION = 2;
	
	private int update_loader_value = 100;
	
	private DragSortListView.DropListener onDrop =
	    new DragSortListView.DropListener() {
	        @Override
	        public void drop(int from, int to) {
	        	List<Direction> directions = adapter.getDirections();
	            Direction item = directions.get(from);
	            directions.remove(item);
	            directions.add(to, item);
	            if(from > to) {
	            	for(int i = to; i <= from; i++) {
	            		Direction d = directions.get(i);
	            		d.setOrder(i + 1);
	            	}
	            } else {
	            	for(int i = from; i <= to; i++) {
	            		Direction d = directions.get(i);
	            		d.setOrder(i + 1);
	            	}
	            }
	            adapter.notifyDataSetChanged();
	        }
	    };

	private DragSortListView.RemoveListener onRemove = 
	    new DragSortListView.RemoveListener() {
	        @Override
	        public void remove(int which) {
	        	Direction direction = adapter.getItem(which);
	            adapter.remove(direction);
	            if(directionDao == null) {
	            	directionDao = getHelper().getDirectionRuntimeExceptionDao();
	            }
	            directionDao.delete(direction);
	            ListviewUtilities.setListViewHeightBasedOnChildren(listview);
	            update_loader_value++;
	            
	            // Update the count for every element after
	            List<Direction> directions = adapter.getDirections();
	            for(int i = which; i < directions.size(); i++) {
            		direction = directions.get(i);
            		direction.setOrder(i + 1);
            	}
	            adapter.notifyDataSetChanged();
	        }
	    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d("RecipeDirection", "in on create view");
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		Bundle bundle = getArguments();
		recipe = (Recipe)bundle.getSerializable(Recipe.KEY);
		
		Button newDirection = (Button)view.findViewById(R.id.new_direction);
		final Fragment targetFragment = this;
		newDirection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment frag = new NewDirectionDialog();
				frag.setTargetFragment(targetFragment, NEW_DIRECTION);
				frag.setArguments(getArguments());
				frag.show(getFragmentManager().beginTransaction(), "New Direction");
			}
		});
		
		listview = (DragSortListView)view.findViewById(R.id.listview);
		listview.setDropListener(onDrop);
		listview.setRemoveListener(onRemove);
		
		/*
		//disable scroll by default, re-enable later
		listview.setOnTouchListener(new OnTouchListener() {

		    public boolean onTouch(View v, MotionEvent event) {
		        return (event.getAction() == MotionEvent.ACTION_MOVE);
		    }
		});
		*/
		emptyView = (TextView)view.findViewById(R.id.empty_list);
		// refresh our recipe to get the latest directions
		RuntimeExceptionDao<Recipe, Integer> recipeDao = 
				getHelper().getRecipeRuntimeExceptionDao();
		recipeDao.refresh(recipe);
		
		List<Direction> directions = recipe.getDirections();
		if(directions != null) {
			adapter = new DirectionListAdapter(getActivity(), directions);
			listview.setAdapter(adapter);
			ListviewUtilities.setListViewHeightBasedOnChildren(listview);
		}
		listview.setEmptyView(emptyView);
		return view;
		
	}
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_direction;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		
		case NEW_DIRECTION:
			if(resultCode == Activity.RESULT_OK) {
				Direction direction = new Direction();
				direction.setRecipe(recipe);
				direction.setContent(data.getStringExtra(NewDirectionDialog.DIRECTION_CONTENT));
				if(directionDao == null) {
					directionDao = getHelper().getDirectionRuntimeExceptionDao();
				}
				// if we have no adapter, means we've had no prior directions
				if(adapter == null) {
					direction.setOrder(1);
					List<Direction> directions = new ArrayList<Direction>();
					directions.add(direction);
					adapter = new DirectionListAdapter(getActivity(), directions);
					listview.setAdapter(adapter);
				} else {
					direction.setOrder(adapter.getCount() + 1);
					adapter.add(direction);
					adapter.notifyDataSetChanged();
				}
				directionDao.create(direction);
				ListviewUtilities.setListViewHeightBasedOnChildren(listview);
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(adapter != null) {
			//save reordering now
			List<Direction> originalDirections = adapter.getOriginalDirections();
			List<Direction> directions = adapter.getDirections();
			for(int i = 0; i < directions.size() ; i++) {
				
				//remove the items that remain from the original list
				//to determine what needs to get deleted
				originalDirections.remove(
						directions.get(i));
				
				//check if item is in same position as it originally was
				if(originalDirections.size() < (i+1) || !directions.get(i).equals(
						originalDirections.get(i))){
					// item changed, update db
					Direction direction = directions.get(i);
					if(directionDao == null) {
						directionDao = getHelper().getDirectionRuntimeExceptionDao();
					}
					directionDao.update(direction);
					update_loader_value++;
				}
			}
		}
		
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		listview.setAdapter(null);
	}
	
	public void enableScroll() {
	}
}